package io.github.thebroccolibob.bobsmobgear.block

import io.github.thebroccolibob.bobsmobgear.block.entity.TemplateBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

// TODO make directional
class TemplateBlock(settings: Settings) : Block(settings), BlockEntityProvider {
    init {
        defaultState = defaultState.with(FACING, Direction.NORTH)
    }

    override fun getOutlineShape(state: BlockState?, world: BlockView?, pos: BlockPos?, context: ShapeContext?): VoxelShape = SHAPE

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState =
        defaultState.with(FACING, ctx.horizontalPlayerFacing)

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        return state.with(FACING, rotation.rotate(state.get(FACING)))
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        return state.rotate(mirror.getRotation(state.get(FACING)))
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = TemplateBlockEntity(pos, state)

    override fun onUseWithItem(
        stack: ItemStack,
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ItemActionResult {
        val templateBlockEntity = world.getBlockEntity(pos) as? TemplateBlockEntity ?: return super.onUseWithItem(stack, state, world, pos, player, hand, hit)

        if (templateBlockEntity.onUseItem(stack, player, hand))
            return ItemActionResult.SUCCESS

        return ItemActionResult.CONSUME_PARTIAL
    }

    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean
    ) {
        if (!state.isOf(newState.block))
            (world.getBlockEntity(pos) as? TemplateBlockEntity)?.let {
                ItemScatterer.spawn(world, pos, it.getItems())
            }
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    companion object {
        private val SHAPE: VoxelShape = createCuboidShape(1.0, 0.0, 1.0, 15.0, 2.0, 15.0)

        val FACING: DirectionProperty = Properties.HORIZONTAL_FACING
    }
}
