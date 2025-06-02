package io.github.thebroccolibob.bobsmobgear.block.entity

import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipeInput
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItemTags
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearSounds
import io.github.thebroccolibob.bobsmobgear.util.*
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.recipe.RecipeEntry
import net.minecraft.registry.RegistryWrapper
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.event.GameEvent
import kotlin.jvm.optionals.getOrNull

class TemplateBlockEntity(type: BlockEntityType<out TemplateBlockEntity>, pos: BlockPos, state: BlockState) :
    BlockEntity(type, pos, state) {

    constructor(pos: BlockPos, state: BlockState) : this(BobsMobGearBlocks.TEMPLATE_BLOCK_ENTITY, pos, state)

    private var hammerHits = 0;
    var baseStack: ItemStack = ItemStack.EMPTY
        private set
    var ingredientsInventory: DefaultedList<ItemStack> = DefaultedList.ofSize(9, ItemStack.EMPTY)
        private set
    val fluidStorage = TemplateFluidStorage()

    private var capacity = FluidConstants.BUCKET

    fun getRecipeInput(
        withBase: ItemStack? = null,
        withIngredient: ItemStack? = null,
        withFluid: FluidVariant? = null,
        skipIngredients: Boolean = false,
        skipFluid: Boolean = false,
        ingredientsPartial: Boolean = false
    ) = TemplateRecipeInput(
        world?.getBlockState(pos.down()) ?: Blocks.AIR.defaultState,
        cachedState.block,
        withBase ?: baseStack,
        if (skipIngredients) null else ingredientsInventory.filter { !it.isEmpty }.let { if (withIngredient == null) it else it + withIngredient },
        if (skipFluid) null else withFluid ?: fluidStorage.variant,
        if (skipFluid || withFluid != null) null else fluidStorage.amount,
        ingredientsPartial
    )

    fun onUseItem(stack: ItemStack, player: PlayerEntity, hand: Hand): Boolean {
        val world = world ?: return false

        if (stack.isEmpty) {
            if (baseStack.isEmpty && ingredientsInventory.all { it.isEmpty }) return false

            ItemScatterer.spawn(world, pos, getItems())
            clearItems()
            world.playSound(null, pos, BobsMobGearSounds.TEMPLATE_REMOVE_ITEM, SoundCategory.BLOCKS)
            return true
        }

        if (!tryAddNextItem(stack, player, hand)) {
            if (!FluidStorageUtil.interactWithFluidStorage(fluidStorage, player, hand))
                return false
        }

        if (!world.isClient)
            getRecipeInput().let { input -> getMatch(input)?.let {
                if (!it.value.requiresHammer || hammerHits >= REQUIRED_HAMMERS)
                    craft(world as ServerWorld, it, input)
            } }

        world.emitGameEvent(GameEvent.BLOCK_CHANGE, getPos(), GameEvent.Emitter.of(player, cachedState))
        this.updateListeners()

        return true
    }

    private fun tryAddNextItem(stack: ItemStack, player: PlayerEntity, hand: Hand): Boolean {
        when {
            getMatch(getRecipeInput()) != null -> {
                if (!(stack isIn BobsMobGearItemTags.SMITHING_HAMMERS) || player.itemCooldownManager.isCoolingDown(stack.item)) return false
                if (stack.isDamageable)
                    stack.damage(1, player, hand.toEquipmentSlot())
                player.itemCooldownManager.set(stack.item, 10)
                world?.playSound(null, pos, BobsMobGearSounds.TEMPLATE_HAMMER, SoundCategory.BLOCKS)
                hammerHits++
            }
            !baseStack.isEmpty -> {
                // any ingredient slot is open
                if (!(0..<ingredientsInventory.size).any { ingredientsInventory[it].isEmpty }
                    || getMatch(getRecipeInput(withIngredient = stack, skipFluid = true, ingredientsPartial = true)) == null) return false

                ingredientsInventory[ingredientsInventory.indexOf(ItemStack.EMPTY)] = stack.splitUnlessCreative(1, player)

                world?.playSound(null, pos, BobsMobGearSounds.TEMPLATE_ADD_ITEM, SoundCategory.BLOCKS)
            }
            else -> {
                if (getMatch(getRecipeInput(withBase = stack, skipIngredients = true, skipFluid = true)) == null) return false

                baseStack = stack.splitUnlessCreative(1, player)

                world?.playSound(null, pos, BobsMobGearSounds.TEMPLATE_ADD_ITEM, SoundCategory.BLOCKS)
            }
        }

        return true
    }

    private fun craft(world: ServerWorld, recipe: RecipeEntry<TemplateRecipe>, input: TemplateRecipeInput) {
        val itemPos = pos.toCenterPos()
        world.spawnEntity(ItemEntity(world, itemPos.x, itemPos.y - 0.125, itemPos.z, recipe.value.craft(input, world.registryManager), 0.0, 0.0, 0.0))
        clearItems()
        clearFluid()
        world.setBlockState(pos, cachedState.fluidState.blockState)
        world.playSound(null, pos, BobsMobGearSounds.TEMPLATE_CRAFT, SoundCategory.BLOCKS)
        world.spawnParticles(ParticleTypes.CRIT, itemPos.x, itemPos.y, itemPos.z, 6, 0.0, 0.0, 0.0, 0.2)
    }

    private fun getMatch(input: TemplateRecipeInput): RecipeEntry<TemplateRecipe>? =
        world?.recipeManager?.getFirstMatch(TemplateRecipe, input, world)?.getOrNull()

    private fun updateListeners() {
        markDirty()
        world?.updateListeners(getPos(), cachedState, cachedState, Block.NOTIFY_ALL)
    }

    override fun writeNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        nbt.putInt(HAMMER_HITS_NBT, hammerHits)
        nbt.put(BASE_STACK_NBT, baseStack.encodeAllowEmpty(registryLookup))
        nbt.put(INGREDIENTS_NBT, ingredientsInventory.map { it.encodeAllowEmpty(registryLookup) }.toNbtList())
        nbt.put(FLUID_NBT, NbtCompound().also { fluidStorage.writeNbt(it, registryLookup)})
        nbt.putLong(CAPACITY_NBT, capacity)
    }

    override fun readNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        hammerHits = nbt.getInt(HAMMER_HITS_NBT)
        baseStack = ItemStack.fromNbtOrEmpty(registryLookup, nbt.getCompound(BASE_STACK_NBT))
        ingredientsInventory = DefaultedList.copyOf(ItemStack.EMPTY, *nbt.getList(INGREDIENTS_NBT, NbtElement.COMPOUND_TYPE).map { ItemStack.fromNbtOrEmpty(registryLookup, it as NbtCompound) }.extend(9, ItemStack.EMPTY).toTypedArray())
        fluidStorage.readNbt(nbt.getCompound(FLUID_NBT), registryLookup)
        capacity = nbt.getLong(CAPACITY_NBT)
    }

    override fun toInitialChunkDataNbt(registryLookup: RegistryWrapper.WrapperLookup): NbtCompound =
        createNbt(registryLookup)

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    fun clearItems() {
        baseStack = ItemStack.EMPTY
        ingredientsInventory.clear()
        hammerHits = 0
    }

    fun clearFluid() {
        fluidStorage.amount = 0
        fluidStorage.variant = FluidVariant.blank()
    }

    fun getItems(): DefaultedList<ItemStack> = DefaultedList.copyOf(ItemStack.EMPTY, baseStack, *ingredientsInventory.toTypedArray())

    companion object {
        const val HAMMER_HITS_NBT = "hammer_hits"
        const val BASE_STACK_NBT = "base_stack"
        const val INGREDIENTS_NBT = "ingredients"
        const val CAPACITY_NBT = "capacity"
        const val FLUID_NBT = "fluid"

        private const val REQUIRED_HAMMERS = 3
    }

    inner class TemplateFluidStorage : SingleFluidStorage() {
        override fun getCapacity(variant: FluidVariant): Long = this@TemplateBlockEntity.capacity

        override fun canInsert(variant: FluidVariant): Boolean {
            return super.canInsert(variant) && getMatch(getRecipeInput(withFluid = variant)) != null
        }

        override fun onFinalCommit() {
            this@TemplateBlockEntity.capacity = getMatch(getRecipeInput(withFluid = variant))?.value?.fluidAmount ?: FluidConstants.BUCKET
            markDirty()

            // TODO networking?
//            if (world?.isClient != false) return
//            val buf = PacketByteBufs.create();
//            // Write your data here.
//            PlayerLookup.tracking(this@TemplateBlockEntity).forEach { player ->
//                ServerPlayNetworking.send(player, YOUR_IDENTIFIER, buf);
//            }
        }
    }

}
