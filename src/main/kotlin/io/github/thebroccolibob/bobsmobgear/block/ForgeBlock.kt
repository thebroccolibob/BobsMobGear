package io.github.thebroccolibob.bobsmobgear.block

import io.github.thebroccolibob.bobsmobgear.block.entity.ForgeBlockEntity
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.isOf
import io.github.thebroccolibob.bobsmobgear.util.set
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.ItemActionResult
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import kotlin.jvm.optionals.getOrNull

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

    override fun onStateReplaced(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        newState: BlockState?,
        moved: Boolean
    ) {
        ItemScatterer.onStateReplaced(state, newState, world, pos)
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun onUseWithItem(
        stack: ItemStack,
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ItemActionResult {
        if ((stack.item as? BlockItem)?.block == this) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

        if (world.isClient) return ItemActionResult.SUCCESS
        val blockEntity = getBlockEntity(world, pos) ?: return ItemActionResult.CONSUME_PARTIAL

        if (stack.isEmpty) {
            player[hand] = blockEntity.tryRemoveStack().also {
                if (!it.isEmpty)
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, player.soundCategory)
            }
            return ItemActionResult.SUCCESS
        }

        if (blockEntity.tryAddStack(stack)) {
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, player.soundCategory)
            return ItemActionResult.SUCCESS
        }

        // TODO Liquid interaction

        return ItemActionResult.CONSUME_PARTIAL
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = ForgeBlockEntity(pos, state)

    // TODO particles

    companion object {
        private fun getBlockEntity(
            world: World,
            pos: BlockPos
        ) = world.getBlockEntity(pos, BobsMobGearBlocks.FORGE_BLOCK_ENTITY).getOrNull()
    }
}