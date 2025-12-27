package io.github.thebroccolibob.bobsmobgear.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i
import net.minecraft.world.WorldAccess
import io.github.thebroccolibob.bobsmobgear.util.*

open class AbstractForgeBlock(settings: Settings) : Block(settings) {
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
        val facing = if (!ctx.shouldCancelInteraction() && ctx.side.isHorizontal && placedOn isOf this) placedOn[FACING] else ctx.horizontalPlayerFacing.opposite
        return defaultState
            .with(CONNECTION, getConnection(ctx.world, ctx.blockPos, facing))
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
                        return state.with(CONNECTION, getConnection(world, pos, facing))
            return state
        } else {
            if (!(neighborState isOf this) || !neighborState[CONNECTION].isConnected || neighborState[FACING] != facing) return state
            for (check in Connection.CONNECTED)
                if (pos - check.offset(facing) + neighborState[CONNECTION].offset(facing) == neighborPos)
                    return state.with(CONNECTION, check)
            return state
        }
    }

    private fun getConnection(world: WorldAccess, pos: BlockPos, facing: Direction): Connection {
        for (start in Connection.CONNECTED)
            if (Connection.CONNECTED.all { check ->
                    start == check ||
                            world[pos - start.offset(facing) + check.offset(facing)].let {
                                it isOf this && it[CONNECTION] == Connection.NONE && it[FACING] == facing
                            }
                })
                return start

        return Connection.NONE
    }

    enum class Connection(val id: String, x: Int, z: Int) : StringIdentifiable {
        NONE("none", 0, 0),
        FRONT_LEFT("front_left", 1, 0),
        FRONT_RIGHT("front_right", 0, 0),
        BACK_LEFT("back_left", 1, 1),
        BACK_RIGHT("back_right", 0, 1);

        private val offset = Vec3i(x, 0, z);
        val isConnected get() = this != NONE
        val isRoot get() = this == NONE || this == FRONT_LEFT

        private val facingOffsets by lazy { // For some reason making this not lazy causes an ExceptionInInitializerError
            Direction.entries.associateWith {
                when (it) {
                    Direction.NORTH -> offset
                    Direction.EAST -> Vec3i(-offset.z, offset.y, offset.x)
                    Direction.SOUTH -> Vec3i(-offset.x, offset.y, -offset.z)
                    Direction.WEST -> Vec3i(offset.z, offset.y, -offset.x)
                    else -> offset
                }
            }
        }

        val isFront get() = when (this) {
            NONE, FRONT_LEFT, FRONT_RIGHT -> true
            else -> false
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

        fun iterateConnected(pos: BlockPos, state: BlockState): List<BlockPos> {
            val connection = state[CONNECTION]
            if (!connection.isConnected)
                return listOf(pos)
            val facing = state[FACING]
            return Connection.CONNECTED.map {
                pos - connection.offset(facing) + it.offset(facing)
            }
        }
    }
}