package io.github.thebroccolibob.bobsmobgear.block.entity

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos

class ForgeHeaterBlockEntity(type: BlockEntityType<out ForgeHeaterBlockEntity>, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state) {
        constructor(pos: BlockPos, state: BlockState) : this(BobsMobGearBlocks.FORGE_HEATER_BLOCK_ENTITY, pos, state)
}