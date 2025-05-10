package io.github.thebroccolibob.bobsmobgear.block.entity

import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock.Companion.LIT
import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock.Companion.iterateConnected
import io.github.thebroccolibob.bobsmobgear.recipe.ForgingRecipe
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.util.toDefaultedList
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.jvm.optionals.getOrNull

class ForgeBlockEntity(type: BlockEntityType<out ForgeBlockEntity>, pos: BlockPos, state: BlockState, private val inventory: SimpleInventory) : BlockEntity(type, pos, state), Inventory by inventory {
    constructor(pos: BlockPos, state: BlockState) : this(BobsMobGearBlocks.FORGE_BLOCK_ENTITY, pos, state, SimpleInventory(1))

    val fluidStorage: SingleFluidStorage = object : SingleFluidStorage() {
        override fun getCapacity(variant: FluidVariant?): Long = FluidConstants.BUCKET

        override fun onFinalCommit() {
            super.onFinalCommit()
            this@ForgeBlockEntity.updateListeners()
        }

//        override fun supportsInsertion(): Boolean = false
    }

    private var progress = 0

    fun getConnected(world: World): List<ForgeBlockEntity> =
        listOf(this) + iterateConnected(pos, cachedState).mapNotNull {
            if (it == pos) null else world.getBlockEntity(it, BobsMobGearBlocks.FORGE_BLOCK_ENTITY).getOrNull()
        }

    fun tryAddStack(world: World, stack: ItemStack): Boolean {
        if (!world.recipeManager.listAllOfType(ForgingRecipe).any { recipe ->
            recipe.value.ingredients.any { it.test(stack) }
        })
            return false

        var inserted = false
        for (forge in getConnected(world)) {
            val existingStack = forge.getStack(0)
            if ((!existingStack.isEmpty && !ItemStack.areItemsAndComponentsEqual(stack, existingStack))
                || existingStack.count >= existingStack.maxCount) continue // Can insert

            inserted = true

            if (existingStack.isEmpty) {
                forge.setStack(0, stack.copy())
                stack.count = 0
            } else if (existingStack.count + stack.count <= existingStack.maxCount) {
                existingStack.count += stack.count
                stack.count = 0
            } else {
                stack.decrement(existingStack.maxCount - existingStack.count + stack.count)
                existingStack.count = existingStack.maxCount
            }

            updateListeners()
            if (stack.isEmpty)
                break
        }
        return inserted
    }

    fun tryRemoveStack(world: World): ItemStack {
        for (forge in getConnected(world)) {
            val existingStack = forge.removeStack(0)
            if (existingStack.isEmpty) continue

            forge.updateListeners()
            return existingStack
        }
        return ItemStack.EMPTY
    }

    fun tryExtractFluid(world: World, player: PlayerEntity, hand: Hand): Boolean = getConnected(world).any {
        FluidStorageUtil.interactWithFluidStorage(FilteringStorage.extractOnlyOf(it.fluidStorage), player, hand)
    }

    fun getStacks(world: World) = getConnected(world).flatMap { it.inventory.heldStacks }.toDefaultedList()

    private fun updateListeners() {
        markDirty()
        world?.updateListeners(getPos(), cachedState, cachedState, Block.NOTIFY_ALL)
    }

    override fun markDirty() {
        inventory.markDirty()
        super.markDirty()
    }

    override fun writeNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        Inventories.writeNbt(nbt, inventory.heldStacks, registryLookup)
        nbt.put(FLUID_NBT, NbtCompound().also { fluidStorage.writeNbt(it, registryLookup) })
        nbt.putInt(PROGRESS_NBT, progress)
    }

    override fun readNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        Inventories.readNbt(nbt, inventory.heldStacks, registryLookup)
        fluidStorage.readNbt(nbt.getCompound(FLUID_NBT), registryLookup)
        progress = nbt.getInt(PROGRESS_NBT)
    }

    override fun toInitialChunkDataNbt(registryLookup: RegistryWrapper.WrapperLookup): NbtCompound =
        createNbt(registryLookup)

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    companion object : BlockEntityTicker<ForgeBlockEntity> {
        val PROGRESS_NBT = "progress"
        val FLUID_NBT = "fluid"

        override fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: ForgeBlockEntity) {
            with (blockEntity) {
                val stacks = getStacks(world)
                val recipe = if (inventory.isEmpty || !state[LIT]) null else
                    world.recipeManager.getFirstMatch(ForgingRecipe, ForgingRecipe.Input(stacks), world).getOrNull()?.value

                if (recipe == null) {
                    if (progress > 0)
                        progress--
                    return
                }

                progress++

                if (progress < recipe.forgingTime) return

                Transaction.openOuter().use {
                    val accepted = fluidStorage.insert(recipe.result, recipe.resultAmount, it)
                    if (accepted < recipe.resultAmount) {
                        it.abort()
                        return
                    }
                    it.commit()
                }
                recipe.subtractItems(stacks)
                updateListeners()

                progress = 0
            }
        }
    }
}