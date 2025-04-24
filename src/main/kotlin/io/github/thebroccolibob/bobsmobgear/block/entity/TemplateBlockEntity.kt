package io.github.thebroccolibob.bobsmobgear.block.entity

import io.github.thebroccolibob.bobsmobgear.data.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.data.TemplateRecipeInput
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.extend
import io.github.thebroccolibob.bobsmobgear.util.getList
import io.github.thebroccolibob.bobsmobgear.util.toEquipmentSlot
import io.github.thebroccolibob.bobsmobgear.util.toNbtList
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
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
import net.minecraft.recipe.RecipeEntry
import net.minecraft.registry.RegistryWrapper
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
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
    val fluidStorage: SingleVariantStorage<FluidVariant> = object : SingleVariantStorage<FluidVariant>() {
        override fun getCapacity(variant: FluidVariant): Long = FluidConstants.BUCKET

        override fun getBlankVariant(): FluidVariant = FluidVariant.blank()

        override fun canInsert(variant: FluidVariant): Boolean {
            return super.canInsert(variant) && getMatch(getRecipeInput(withFluid = variant)) != null
        }

        override fun onFinalCommit() {
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

        if (!tryAddNextItem(stack, player, hand)) {
            if (!FluidStorageUtil.interactWithFluidStorage(fluidStorage, player, hand))
                return false
        }

        if (!world.isClient)
            getRecipeInput().let { input -> getMatch(input)?.let {
                if (!it.value.requiresHammer || hammerHits >= 3) // TODO unhardcode?
                    craft(world, it, input)
            } }

        world.emitGameEvent(GameEvent.BLOCK_CHANGE, getPos(), GameEvent.Emitter.of(player, cachedState))
        this.updateListeners()

        return true
    }

    private fun tryAddNextItem(stack: ItemStack, player: PlayerEntity, hand: Hand): Boolean {
        when {
            getMatch(getRecipeInput()) != null -> {
                if (!stack.isIn(BobsMobGearItems.SMITHING_HAMMER_TAG)) return false
                if (stack.isDamageable)
                    stack.damage(1, player, hand.toEquipmentSlot())
                // TODO sound
                hammerHits++
            }
            !baseStack.isEmpty -> {
                // any ingredient slot is open
                if (!(0..<ingredientsInventory.size).any { ingredientsInventory[it].isEmpty }
                    || getMatch(getRecipeInput(withIngredient = stack, skipFluid = true, ingredientsPartial = true)) == null) return false

                ingredientsInventory[ingredientsInventory.indexOf(ItemStack.EMPTY)] = stack.splitUnlessCreative(1, player)

                // TODO sound
            }
            else -> {
                if (getMatch(getRecipeInput(withBase = stack, skipIngredients = true, skipFluid = true)) == null) return false

                baseStack = stack.splitUnlessCreative(1, player)

                // TODO sound
            }
        }

        return true
    }

    private fun craft(world: World, recipe: RecipeEntry<TemplateRecipe>, input: TemplateRecipeInput) {
        val itemPos = pos.toBottomCenterPos()
        world.spawnEntity(ItemEntity(world, itemPos.x, itemPos.y, itemPos.z, recipe.value.craft(input, world.registryManager)))
        clearItems()
        clearFluid()
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS)
        // TODO remove block
        // TODO particles
    }

    private fun getMatch(input: TemplateRecipeInput): RecipeEntry<TemplateRecipe>? =
        world?.recipeManager?.getFirstMatch(TemplateRecipe, input, world)?.getOrNull()

    private fun updateListeners() {
        markDirty()
        world?.updateListeners(getPos(), cachedState, cachedState, Block.NOTIFY_ALL)
    }

    override fun writeNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        nbt.putInt(HAMMER_HITS, hammerHits)
        nbt.put(BASE_STACK, baseStack.encodeAllowEmpty(registryLookup))
        nbt.put(INGREDIENTS, ingredientsInventory.map { it.encodeAllowEmpty(registryLookup) }.toNbtList())
        nbt.put(FLUID_STORAGE, NbtCompound().also { SingleVariantStorage.writeNbt(fluidStorage, FluidVariant.CODEC, it, registryLookup) })
    }

    override fun readNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        hammerHits = nbt.getInt(HAMMER_HITS)
        baseStack = ItemStack.fromNbtOrEmpty(registryLookup, nbt.getCompound(BASE_STACK))
        ingredientsInventory = DefaultedList.copyOf(ItemStack.EMPTY, *nbt.getList(INGREDIENTS, NbtElement.COMPOUND_TYPE).map { ItemStack.fromNbtOrEmpty(registryLookup, it as NbtCompound) }.extend(9, ItemStack.EMPTY).toTypedArray())
        SingleVariantStorage.readNbt(fluidStorage, FluidVariant.CODEC, { FluidVariant.blank() }, nbt.getCompound(FLUID_STORAGE), registryLookup)
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
        const val HAMMER_HITS = "hammer_hits"
        const val BASE_STACK = "base_stack"
        const val INGREDIENTS = "ingredients"
        const val FLUID_STORAGE = "fluid_storage"
    }
}
