package io.github.thebroccolibob.bobsmobgear.block

import io.github.thebroccolibob.bobsmobgear.block.entity.ForgeBlockEntity
import io.github.thebroccolibob.bobsmobgear.mixin.FluidInvoker
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.isOf
import io.github.thebroccolibob.bobsmobgear.util.minus
import io.github.thebroccolibob.bobsmobgear.util.set
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
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
import net.minecraft.util.math.random.Random
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

        val blockEntity = getBlockEntity(world, pos) ?: return ItemActionResult.CONSUME_PARTIAL

        if (stack.isEmpty) {
            blockEntity.tryRemoveStack(world).also {
                if (!it.isEmpty) {
                    world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, player.soundCategory)
                    player[hand] = it
                    return ItemActionResult.SUCCESS
                }
            }
        }

        if (blockEntity.tryAddStack(world, stack)) {
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, player.soundCategory)
            return ItemActionResult.SUCCESS
        }

        if (blockEntity.tryExtractFluid(world, player, hand)) {
            return ItemActionResult.SUCCESS
        }

        return ItemActionResult.CONSUME_PARTIAL
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        if (random.nextFloat() > 0.25f) return

        val fluidStorage = getBlockEntity(world, pos)?.fluidStorage?.takeUnless { it.isResourceBlank } ?: return

        val connection = state[CONNECTION]
        val direction = state[FACING]
        val axis = direction.axis
        val rootPos = pos - connection.offset(direction)

        val xOffset = if (connection == Connection.NONE) 0.0 else 0.5

        val offset = 0.58
        val dx = 0.5 + if (axis === Direction.Axis.X) direction.offsetX * offset
            else (if (direction == Direction.NORTH) xOffset else -xOffset) + 2 / 16.0 * random.nextDouble() - 1 / 16.0
        val dy = 1 / 16.0 + 5 / 16.0 * random.nextDouble()
        val dz = 0.5 + if (axis === Direction.Axis.Z) direction.offsetZ * offset
            else (if (direction == Direction.EAST) xOffset else -xOffset) + 2 / 16.0 * random.nextDouble() - 1 / 16.0

        world.addParticle(
            (fluidStorage.variant.fluid as FluidInvoker).invokeGetParticle(),
            rootPos.x + dx,
            rootPos.y + dy,
            rootPos.z + dz,
            0.0,
            0.0,
            0.0
        )
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = ForgeBlockEntity(pos, state)

    @Suppress("UNCHECKED_CAST")
    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? =
        if (!world.isClient && type == BobsMobGearBlocks.FORGE_BLOCK_ENTITY)
            ForgeBlockEntity as BlockEntityTicker<T>
        else
            null

    companion object {
        private fun getBlockEntity( // TODO abstract this into an interface?
            world: World,
            pos: BlockPos
        ) = world.getBlockEntity(pos, BobsMobGearBlocks.FORGE_BLOCK_ENTITY).getOrNull()
    }
}