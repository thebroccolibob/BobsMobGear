package io.github.thebroccolibob.bobsmobgear

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.particle.ParticleTypes.SMOKE
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import io.github.thebroccolibob.bobsmobgear.event.ItemTickCallback
import io.github.thebroccolibob.bobsmobgear.mixin.AbstractCauldronBlockInvoker
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearComponents
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.isOf
import org.joml.Matrix3f

@JvmField
val HEATED_COLOR_MATRIX = Matrix3f(
    1f, 0.2f, 0f,
    1f, 0.2f, 0f,
    1f, 0.2f, 0f
)

fun extinguishHeatedStack(stack: ItemStack, world: World, entity: Entity?, pos: Vec3d, soundCategory: SoundCategory) {
    if (entity is ItemEntity)
        entity.stack = stack.copy().apply { remove(BobsMobGearComponents.HEATED) }
    else
        stack.remove(BobsMobGearComponents.HEATED)

    world.playSound(entity as? PlayerEntity, pos.x, pos.y, pos.z, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, soundCategory)

    (world as? ServerWorld)?.run {
        spawnParticles(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 4, 0.25, 0.125, 0.25, 0.0)
    }
}

fun extinguishHeatedStack(stack: ItemStack, world: World, entity: Entity) {
    extinguishHeatedStack(stack, world, entity, Vec3d(entity.x, entity.getBodyY(0.5), entity.z), entity.soundCategory)
}

fun extinguishHeatedStack(stack: ItemStack, world: World, entity: Entity?, cauldronPos: BlockPos) {
    extinguishHeatedStack(
        stack,
        world,
        entity,
        Vec3d(cauldronPos.x + 0.5, cauldronPos.y + 0.9, cauldronPos.z + 0.5),
        entity?.soundCategory ?: SoundCategory.NEUTRAL
    )
}

fun registerHeatedLogic() {
    UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
        if (player.isSpectator) return@register ActionResult.PASS

        val stack = player[hand]
        val state = world[hitResult.blockPos]
        if (BobsMobGearComponents.HEATED !in stack
            || !(state isOf Blocks.WATER_CAULDRON)
            || (hitResult.side != Direction.UP))
            return@register ActionResult.PASS

        extinguishHeatedStack(stack, world, player, hitResult.blockPos)
        ActionResult.SUCCESS
    }

    ItemTickCallback.EVENT.register { entity, stack ->
        if (entity.world.isClient || BobsMobGearComponents.HEATED !in stack) return@register

        val state = entity.blockStateAtPos

        if (entity.isInsideWaterOrBubbleColumn
            || (
                    (state isOf Blocks.WATER_CAULDRON || state isOf Blocks.POWDER_SNOW_CAULDRON)
                            && (Blocks.WATER_CAULDRON as AbstractCauldronBlockInvoker).invokeIsEntityTouchingFluid(state, entity.blockPos, entity))
            || entity.wasInPowderSnow) {

            extinguishHeatedStack(stack, entity.world, entity)

            return@register
        }

        if (entity is LivingEntity)
            if (entity.isWet) {
                if (entity.age.mod(20) == 0)
                    entity.damage(entity.world.damageSources.onFire(), 1f) // TODO custom damage source?
            } else
                entity.setOnFireForTicks(20)
    }

    ItemTickCallback.EVENT.register { entity, stack ->
        if (!entity.world.isClient || entity !is ItemEntity || BobsMobGearComponents.HEATED !in stack) return@register

        with(entity) {
            repeat(random.nextInt(3)) {
                world.addParticle(SMOKE, getParticleX(1.0), getBodyY(random.nextDouble()), getParticleZ(1.0), 0.0, 0.0, 0.0 )
            }
        }
    }

    ServerLivingEntityEvents.AFTER_DAMAGE.register { entity, source, _, _, blocked ->
        if (!blocked && source.attacker?.weaponStack?.contains(BobsMobGearComponents.HEATED) == true) {
            entity.setOnFireFor(4f)
        }
    }

}