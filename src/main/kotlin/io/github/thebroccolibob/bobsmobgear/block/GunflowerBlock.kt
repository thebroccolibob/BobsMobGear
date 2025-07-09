package io.github.thebroccolibob.bobsmobgear.block

import com.mojang.serialization.MapCodec
import io.github.thebroccolibob.bobsmobgear.util.set
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.PlantBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.World.ExplosionSourceType

class GunflowerBlock(val budTicks: Int, val grownTicks: Int, val wiltTicks: Int, val power: Float, settings: Settings) : PlantBlock(settings) {
    init {
        defaultState = stateManager.defaultState
            .with(AGE, Age.BUD)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(AGE)
    }

    override fun getCodec(): MapCodec<out GunflowerBlock> = throw NotImplementedError()

    override fun canPlantOnTop(floor: BlockState, world: BlockView, pos: BlockPos): Boolean {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP)
    }

    override fun onPlaced(
        world: World,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        itemStack: ItemStack
    ) {
        world.scheduleBlockTick(pos, this, nextDelay(state[AGE]))
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        world[pos, AGE] = when (state[AGE]) {
            Age.BUD -> Age.GROWN
            Age.GROWN -> Age.WILTED
            Age.WILTED -> {
                world.breakBlock(pos, false)
                return
            }
        }
        world.scheduleBlockTick(pos, this, nextDelay(state[AGE]))
    }

    fun explode(state: BlockState, world: World, pos: BlockPos, cause: Entity) {
        world.createExplosion(cause, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, power, ExplosionSourceType.BLOCK)
    }

    private fun nextDelay(age: Age) = when (age) {
        Age.BUD -> budTicks
        Age.GROWN -> grownTicks
        Age.WILTED -> wiltTicks
    }

    enum class Age(private val id: String) : StringIdentifiable {
        BUD("bud"), GROWN("grown"), WILTED("wilted");
        override fun asString(): String = id
    }

    companion object {
        val AGE: EnumProperty<Age> = EnumProperty.of("age", Age::class.java)
    }
}