package io.github.thebroccolibob.bobsmobgear.block

import io.github.thebroccolibob.bobsmobgear.block.entity.ForgeHeaterBlockEntity
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.util.*
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.ItemActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import kotlin.jvm.optionals.getOrNull

class ForgeHeaterBlock(settings: Settings) : AbstractForgeBlock(settings), BlockEntityProvider {
    override fun createBlockEntity(pos: BlockPos, state: BlockState) = ForgeHeaterBlockEntity(pos, state)

    override fun onUseWithItem(
        stack: ItemStack,
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ItemActionResult {
        val fuel = FuelRegistry.INSTANCE[stack.item]
        if (fuel == null || fuel <= 0) return ItemActionResult.FAIL

        if (world.isClient) return ItemActionResult.SUCCESS

        val connection = state[CONNECTION]
        val facing = state[FACING]
        val heatIncrease = if (connection.isConnected) fuel / 2 else fuel

        for (entityPos in run {
            if (connection.isConnected)
                Connection.CONNECTED.map { pos - connection.offset(facing) + it.offset(facing) }
            else
                listOf(pos)
        }) {
            world.getBlockEntity(entityPos, BobsMobGearBlocks.FORGE_HEATER_BLOCK_ENTITY).getOrNull()
                ?.addHeat(heatIncrease)
        }
        stack.decrementUnlessCreative(1, player)
        world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, player.soundCategory) // TODO custom sounds

        return ItemActionResult.SUCCESS
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        if (!state[LIT]) return

        val (x, y, z) = pos.toBottomCenterPos()

        if (random.nextDouble() < 0.1) {
            world.playSound(null, pos, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS)
        }

        if (!state[CONNECTION].isFront) return

        val direction = state[FACING]
        val axis = direction.axis
        val offset = 0.52
        val dhrz = (random.nextDouble() - 0.5) * 10 / 16.0
        val dx = if (axis === Direction.Axis.X) direction.offsetX * offset else dhrz
        val dy = (random.nextDouble() * 6 + 3) / 16
        val dz = if (axis === Direction.Axis.Z) direction.offsetZ * offset else dhrz
        world.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0)
        world.addParticle(ParticleTypes.FLAME, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? =
        if (!world.isClient && type == BobsMobGearBlocks.FORGE_HEATER_BLOCK_ENTITY)
            ForgeHeaterBlockEntity as BlockEntityTicker<T>
        else
            null
}