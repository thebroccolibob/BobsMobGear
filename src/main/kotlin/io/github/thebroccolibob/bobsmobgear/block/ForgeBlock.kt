package io.github.thebroccolibob.bobsmobgear.block

import io.github.thebroccolibob.bobsmobgear.block.entity.ForgeBlockEntity
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.isOf
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.item.ItemPlacementContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.WorldAccess

class ForgeBlock(private val heaterBlock: Block, settings: Settings) : AbstractForgeBlock(settings), BlockEntityProvider {

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return super.getPlacementState(ctx)
            .with(LIT, ctx.world[ctx.blockPos.down()].let { it isOf heaterBlock && it[LIT] })
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos).let {
            if (pos.down() == neighborPos)
                it.with(LIT, neighborState isOf heaterBlock && neighborState[LIT])
            else it
        }
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = ForgeBlockEntity(pos, state)
}