package io.github.thebroccolibob.bobsmobgear.block

import io.github.thebroccolibob.bobsmobgear.block.entity.TemplateBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.ItemActionResult
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

// TODO make directional
class TemplateBlock(settings: Settings) : Block(settings), BlockEntityProvider {
    override fun createBlockEntity(pos: BlockPos, state: BlockState) = TemplateBlockEntity(pos, state)

    override fun getOutlineShape(state: BlockState?, world: BlockView?, pos: BlockPos?, context: ShapeContext?): VoxelShape = SHAPE

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
        world: World?,
        pos: BlockPos?,
        newState: BlockState,
        moved: Boolean
    ) {
        ItemScatterer.onStateReplaced(state, newState, world, pos)
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    companion object {
        private val SHAPE: VoxelShape = createCuboidShape(1.0, 0.0, 1.0, 15.0, 2.0, 15.0)
    }
}
