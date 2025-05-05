package io.github.thebroccolibob.bobsmobgear.block

import io.github.thebroccolibob.bobsmobgear.block.entity.ForgeBlockEntity
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.isOf
import io.github.thebroccolibob.bobsmobgear.util.minus
import io.github.thebroccolibob.bobsmobgear.util.plus
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.Hand
import net.minecraft.util.ItemActionResult
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class ForgeBlock(settings: Settings) : Block(settings), BlockEntityProvider {
    init {
        defaultState = stateManager.defaultState
            .with(CONNECTION, Connection.NONE)
            .with(LIT, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(CONNECTION, FACING, LIT)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        val placedOn = ctx.world[ctx.blockPos.offset(ctx.side.opposite)]
        val facing = if (!ctx.shouldCancelInteraction() && placedOn isOf this) placedOn[FACING] else ctx.horizontalPlayerFacing.opposite

        for (start in Connection.CONNECTED) {
            if (Connection.CONNECTED.all { check ->
                start == check ||
                ctx.world[ctx.blockPos - start.offset(facing) + check.offset(facing)].let {
                    it isOf this && it[CONNECTION] == Connection.NONE && it[FACING] == facing
                }
            })
                return defaultState
                    .with(FACING, facing)
                    .with(CONNECTION, start)
        }
        return defaultState
            .with(FACING, facing)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        val connection = state[CONNECTION]
        val facing = state[FACING]
        if (connection.isConnected) {
            for (check in Connection.CONNECTED)
                if (pos - connection.offset(facing) + check.offset(facing) == neighborPos)
                    if (!(neighborState isOf this) || neighborState[CONNECTION] != check || neighborState[FACING] != facing)
                        return state.with(CONNECTION, Connection.NONE)
            return state
        } else {
            if (!(neighborState isOf this) || !neighborState[CONNECTION].isConnected || neighborState[FACING] != facing) return state
            for (check in Connection.CONNECTED)
                if (pos - check.offset(facing) + neighborState[CONNECTION].offset(facing) == neighborPos)
                    return state.with(CONNECTION, check)
            return state
        }
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
        if (stack isOf this.asItem())
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
        TODO("Implement block entity interaction")
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = ForgeBlockEntity(pos, state)

    enum class Connection(val id: String, x: Int, z: Int) : StringIdentifiable {
        NONE("none", 0, 0),
        FRONT_LEFT("front_left", 1, 0),
        FRONT_RIGHT("front_right", 0, 0),
        BACK_LEFT("back_left", 1, 1),
        BACK_RIGHT("back_right", 0, 1);

        private val offset = Vec3i(x, 0, z);
        val isConnected get() = this != NONE

        val facingOffsets = Direction.entries.associateWith {
            when (it) {
                Direction.NORTH -> offset
                Direction.EAST -> Vec3i(-offset.z, offset.y, offset.x)
                Direction.SOUTH -> Vec3i(-offset.x, offset.y, -offset.z)
                Direction.WEST -> Vec3i(offset.z, offset.y, -offset.x)
                else -> offset
            }
        }

        fun offset(direction: Direction) = facingOffsets[direction]!!

        override fun asString(): String = id

        companion object {
            val CONNECTED = entries.filterNot { it == NONE }
        }
    }

    companion object {
        val CONNECTION: EnumProperty<Connection> = EnumProperty.of("connection", Connection::class.java)
        val FACING: DirectionProperty = Properties.HORIZONTAL_FACING
        val LIT: BooleanProperty = Properties.LIT
    }
}