package io.github.thebroccolibob.bobsmobgear.block

import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import io.github.thebroccolibob.bobsmobgear.block.entity.TemplateBlockEntity
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.isIn
import io.github.thebroccolibob.bobsmobgear.util.isOf
import io.github.thebroccolibob.bobsmobgear.util.validateTicker

class TemplateBlock(settings: Settings) : Block(settings), BlockEntityProvider {
    init {
        defaultState = defaultState
            .with(METAL, false)
            .with(FACING, Direction.NORTH)
    }

    override fun getOutlineShape(state: BlockState?, world: BlockView?, pos: BlockPos?, context: ShapeContext?): VoxelShape =
        SHAPE

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(METAL, FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState =
        defaultState
            .with(METAL, ctx.run { world[blockPos.down()] isIn BobsMobGearBlocks.SMITHING_SURFACE })
            .with(FACING, ctx.horizontalPlayerFacing)

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState =
        state.with(METAL, world[pos.down()] isIn BobsMobGearBlocks.SMITHING_SURFACE)

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState =
        state.with(FACING, rotation.rotate(state.get(FACING)))

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState =
        state.rotate(mirror.getRotation(state.get(FACING)))

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
        if (!(state isOf newState.block))
            (world.getBlockEntity(pos) as? TemplateBlockEntity)?.let {
                ItemScatterer.spawn(world, pos, it.getItems())
            }
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun getSoundGroup(state: BlockState): BlockSoundGroup =
        if (state[METAL]) BlockSoundGroup.METAL else super.getSoundGroup(state)

    override fun <T : BlockEntity> getTicker(
        world: World?,
        state: BlockState?,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? = validateTicker(type, BobsMobGearBlocks.TEMPLATE_BLOCK_ENTITY, TemplateBlockEntity)

    companion object {
        private val SHAPE: VoxelShape = createCuboidShape(1.0, 0.0, 1.0, 15.0, 2.0, 15.0)

        val FACING: DirectionProperty = Properties.HORIZONTAL_FACING
        val METAL: BooleanProperty = BooleanProperty.of("metal")
    }
}
