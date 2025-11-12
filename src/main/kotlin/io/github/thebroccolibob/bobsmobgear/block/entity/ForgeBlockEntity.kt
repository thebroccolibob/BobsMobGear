package io.github.thebroccolibob.bobsmobgear.block.entity

import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock.Companion.CONNECTION
import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock.Companion.FACING
import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock.Companion.LIT
import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock.Companion.iterateConnected
import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock.Connection
import io.github.thebroccolibob.bobsmobgear.recipe.ForgingRecipe
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.util.*
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
import net.minecraft.util.collection.DefaultedList
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
        iterateConnected(pos, cachedState).mapNotNull {
            if (it == pos) this else world.getBlockEntity(it, BobsMobGearBlocks.FORGE_BLOCK_ENTITY).getOrNull()
        }

    fun getConnectedThisAndAfter(world: World): List<ForgeBlockEntity> {
        val connection = cachedState[CONNECTION]
        val facing = cachedState[FACING]
        return Connection.CONNECTED
            .filter { it >= connection }
            .map { pos - connection.offset(facing) + it.offset(facing) }
            .mapNotNull { world.getBlockEntity(it, BobsMobGearBlocks.FORGE_BLOCK_ENTITY).getOrNull() }
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
        inventory.heldStacks.clear()
        Inventories.readNbt(nbt, inventory.heldStacks, registryLookup)
        fluidStorage.readNbt(nbt.getCompound(FLUID_NBT), registryLookup)
        progress = nbt.getInt(PROGRESS_NBT)
    }

    override fun toInitialChunkDataNbt(registryLookup: RegistryWrapper.WrapperLookup): NbtCompound =
        createNbt(registryLookup)

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> = BlockEntityUpdateS2CPacket.create(this)

    companion object : BlockEntityTicker<ForgeBlockEntity> {
        const val PROGRESS_NBT = "progress"
        const val FLUID_NBT = "fluid"

        override fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: ForgeBlockEntity) {
            if (!state[CONNECTION].isRoot) return
            val connectedForges = blockEntity.getConnected(world)

            if (!state[LIT]) {
                tickForgesRecipe(connectedForges, DefaultedList.of(), null)
                return
            }

            val strongHeat = world[pos.down()] isOf BobsMobGearBlocks.FORGE_HEATER

            if (!state[CONNECTION].isConnected) {
                world.recipeManager.getFirstMatch(ForgingRecipe, ForgingRecipe.Input(blockEntity.inventory.heldStacks, strongHeat), world).getOrNull()?.value?.let {
                    tickForgesRecipe(listOf(blockEntity), blockEntity.inventory.heldStacks, it)
                }
                return
            }

            val remainingForges = connectedForges.toMutableSet()
            while (remainingForges.isNotEmpty()) {
                val stacks = remainingForges.flatMap { it.inventory.heldStacks }.toDefaultedList()
                if (stacks.all { it.isEmpty }) break
                val recipe = world.recipeManager.getFirstMatch(ForgingRecipe, ForgingRecipe.Input(stacks, strongHeat), world).getOrNull()?.value ?: break
                val used = recipe.selectInventories(remainingForges) { inventory.heldStacks }
                tickForgesRecipe(used, stacks, recipe)
                remainingForges.removeAll(used)
            }
            tickForgesRecipe(remainingForges, DefaultedList.of(), null)
        }

        private fun tickForgesRecipe(forges: Iterable<ForgeBlockEntity>, stacks: DefaultedList<ItemStack>, recipe: ForgingRecipe?) {
            if (recipe == null) {
                for (blockEntity in forges) with (blockEntity) {
                    if (progress > 0)
                        progress--
                }
                return
            }

            for (blockEntity in forges)
                blockEntity.progress++

            if (forges.any { it.progress < recipe.forgingTime}) return

            // Craft

            Transaction.openOuter().use {
                val remaining = forges.fold(recipe.resultAmount) { remaining, forge ->
                    if (remaining <= 0) remaining else remaining - forge.fluidStorage.insert(recipe.result, remaining, it)
                }
                if (remaining > 0) {
                    it.abort()
                    return
                }
                it.commit()
            }
            recipe.subtractItems(stacks)
            for (forge in forges) {
                forge.progress = 0
                forge.updateListeners()
            }
        }
    }
}