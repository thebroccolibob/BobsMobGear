package io.github.thebroccolibob.bobsmobgear.block.entity

import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock.Companion.CONNECTION
import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock.Companion.FACING
import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock.Connection
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.util.minus
import io.github.thebroccolibob.bobsmobgear.util.plus
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.jvm.optionals.getOrNull

class ForgeBlockEntity(type: BlockEntityType<out ForgeBlockEntity>, pos: BlockPos, state: BlockState, private val inventory: SimpleInventory) : BlockEntity(type, pos, state), Inventory by inventory {
    constructor(pos: BlockPos, state: BlockState) : this(BobsMobGearBlocks.FORGE_BLOCK_ENTITY, pos, state, SimpleInventory(1))

    fun getConnected(world: World? = getWorld()): List<ForgeBlockEntity> {
        val connection = cachedState[CONNECTION]
        return if (!connection.isConnected || world == null)
            listOf(this)
        else {
            val facing = cachedState[FACING]
            listOf(this) + Connection.CONNECTED.mapNotNull { // Put this one first
                if (it == connection)
                    null
                else
                    world.getBlockEntity(
                        pos - connection.offset(facing) + it.offset(facing),
                        BobsMobGearBlocks.FORGE_BLOCK_ENTITY
                    ).getOrNull()
            }
        }
    }

    fun tryAddStack(stack: ItemStack): Boolean {
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

            forge.markDirty()
            if (stack.isEmpty)
                break
        }
        return inserted
    }

    fun tryRemoveStack(): ItemStack {
        for (forge in getConnected()) {
            val existingStack = forge.removeStack(0)
            if (existingStack.isEmpty) continue

            forge.markDirty()
            return existingStack
        }
        return ItemStack.EMPTY
    }

    override fun markDirty() {
        inventory.markDirty()
        super.markDirty()
    }

    override fun writeNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        Inventories.writeNbt(nbt, inventory.heldStacks, registryLookup)
    }

    override fun readNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        Inventories.readNbt(nbt, inventory.heldStacks, registryLookup)
    }
}