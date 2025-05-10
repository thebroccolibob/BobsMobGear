package io.github.thebroccolibob.bobsmobgear.block.entity

import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock
import io.github.thebroccolibob.bobsmobgear.block.ForgeHeaterBlock
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ForgeHeaterBlockEntity(type: BlockEntityType<out ForgeHeaterBlockEntity>, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state) {

    private var heatedTicks = 0

    val isHeated
        get() = heatedTicks > 0

    constructor(pos: BlockPos, state: BlockState) : this(BobsMobGearBlocks.FORGE_HEATER_BLOCK_ENTITY, pos, state)

    fun addHeat(heatedTicks: Int) {
        world?.setBlockState(pos, cachedState.with(AbstractForgeBlock.LIT, true))
        this.heatedTicks += heatedTicks
    }

    override fun writeNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        nbt.putInt(HEATED_TICKS, heatedTicks)
    }

    override fun readNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        heatedTicks = nbt.getInt(HEATED_TICKS)
    }

    companion object : BlockEntityTicker<ForgeHeaterBlockEntity> {
        const val HEATED_TICKS = "heated_ticks"

        /**@see [ForgeHeaterBlock.scheduledTick]*/
        override fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: ForgeHeaterBlockEntity) {
            with(blockEntity) {
                if (heatedTicks > 0) {
                    heatedTicks--
                    if (heatedTicks <= 0)
                        world.scheduleBlockTick(pos, state.block, 1)
                }
            }
        }
    }
}