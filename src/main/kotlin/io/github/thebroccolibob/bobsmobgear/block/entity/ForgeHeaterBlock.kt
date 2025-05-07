package io.github.thebroccolibob.bobsmobgear.block.entity

import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

class ForgeHeaterBlock(settings: Settings) : AbstractForgeBlock(settings), BlockEntityProvider {
    override fun createBlockEntity(pos: BlockPos, state: BlockState) = ForgeHeaterBlockEntity(pos, state)
}