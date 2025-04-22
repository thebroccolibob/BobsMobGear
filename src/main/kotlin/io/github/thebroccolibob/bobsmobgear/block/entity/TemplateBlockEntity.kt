package io.github.thebroccolibob.bobsmobgear.block.entity

import io.github.thebroccolibob.bobsmobgear.data.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.data.TemplateRecipeInput
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.extend
import io.github.thebroccolibob.bobsmobgear.util.getList
import io.github.thebroccolibob.bobsmobgear.util.toNbtList
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.recipe.RecipeEntry
import net.minecraft.registry.RegistryWrapper
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.jvm.optionals.getOrNull

class TemplateBlockEntity(type: BlockEntityType<out TemplateBlockEntity>, pos: BlockPos, state: BlockState) :
    BlockEntity(type, pos, state), Inventory {

    constructor(pos: BlockPos, state: BlockState) : this(BobsMobGearBlocks.TEMPLATE_BLOCK_ENTITY, pos, state)

    private var hammerHits = 0;
    private var baseStack: ItemStack = ItemStack.EMPTY
    private var ingredientsInventory: DefaultedList<ItemStack> = DefaultedList.ofSize(9, ItemStack.EMPTY)
    private val fluidStorage: SingleVariantStorage<FluidVariant> = object : SingleVariantStorage<FluidVariant>() {
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

        when {
            getMatch(getRecipeInput()) != null -> {
                if (!stack.isIn(BobsMobGearItems.SMITHING_HAMMER_TAG)) return false
                hammerHits++
                if (hammerHits < 3)
                    return true
            }
            getMatch(getRecipeInput(skipFluid = true)) != null -> {
                if (!FluidStorageUtil.interactWithFluidStorage(fluidStorage, player, hand))
                    return false
            }
            !baseStack.isEmpty -> {
                if (!(0..<ingredientsInventory.size).any { ingredientsInventory[it].isEmpty } || getMatch(getRecipeInput(withIngredient = stack, skipFluid = true, ingredientsPartial = true)) == null) return false

                ingredientsInventory[ingredientsInventory.indexOf(ItemStack.EMPTY)] = stack.splitUnlessCreative(1, player)
            }
            else -> {
                if (getMatch(getRecipeInput(withBase = stack, skipIngredients = true, skipFluid = true)) == null) return false
                baseStack = stack.splitUnlessCreative(1, player)
            }
        }

        if (!world.isClient)
            getRecipeInput().let { input -> getMatch(input)?.let {
                if (!it.value.requiresHammer || hammerHits >= 3) // TODO unhardcode?
                    craft(world, it, input)
            } }

        return true
    }

    private fun craft(world: World, recipe: RecipeEntry<TemplateRecipe>, input: TemplateRecipeInput) {
        val itemPos = pos.toBottomCenterPos()
        world.spawnEntity(ItemEntity(world, itemPos.x, itemPos.y, itemPos.z, recipe.value.craft(input, world.registryManager)))
        clear()
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS)
        // TODO remove block
    }

    private fun getMatch(input: TemplateRecipeInput): RecipeEntry<TemplateRecipe>? =
        world?.recipeManager?.getFirstMatch(TemplateRecipe, input, world)?.getOrNull()

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

    override fun clear() {
        baseStack = ItemStack.EMPTY
        ingredientsInventory.clear()
        fluidStorage.amount = 0
        fluidStorage.variant = FluidVariant.blank()
        hammerHits = 0
    }

    override fun size(): Int = 1 + ingredientsInventory.size

    override fun isEmpty(): Boolean = baseStack.isEmpty && ingredientsInventory.all { isEmpty }

    override fun getStack(slot: Int): ItemStack = when (slot) {
        0 -> baseStack
        else -> ingredientsInventory[slot - 1]
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack = when (slot) {
        0 -> baseStack.split(amount)
        else -> Inventories.splitStack(ingredientsInventory, slot - 1, amount)
    }.also {
        if (!isEmpty) markDirty()
    }

    override fun removeStack(slot: Int): ItemStack = when (slot) {
        0 -> baseStack.also { baseStack = ItemStack.EMPTY }
        else -> Inventories.removeStack(ingredientsInventory, slot - 1)
    }.also {
        if (!isEmpty) markDirty()
    }

    override fun setStack(slot: Int, stack: ItemStack) {
        when (slot) {
            0 -> baseStack = stack
            else -> ingredientsInventory[slot - 1] = stack
        }
        stack.capCount(getMaxCount(stack))
        markDirty()
    }

    override fun canPlayerUse(player: PlayerEntity): Boolean =
        Inventory.canPlayerUse(this, player)

    companion object {
        const val HAMMER_HITS = "hammer_hits"
        const val BASE_STACK = "base_stack"
        const val INGREDIENTS = "ingredients"
        const val FLUID_STORAGE = "fluid_storage"
    }
}
