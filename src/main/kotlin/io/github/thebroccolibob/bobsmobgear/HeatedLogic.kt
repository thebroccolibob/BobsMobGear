package io.github.thebroccolibob.bobsmobgear

import io.github.thebroccolibob.bobsmobgear.event.ItemTickCallback
import io.github.thebroccolibob.bobsmobgear.mixin.AbstractCauldronBlockInvoker
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.isOf
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.block.Blocks
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import org.joml.Matrix3f

@JvmField
val HEATED_COLOR_MATRIX = Matrix3f(
    1f, 0.2f, 0f,
    1f, 0.2f, 0f,
    1f, 0.2f, 0f
)

fun registerHeatedLogic() {
    UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
        if (player.isSpectator) return@register ActionResult.PASS

        val stack = player[hand]
        val state = world[hitResult.blockPos]
        if (BobsMobGearItems.HEATED !in stack
            || !(state isOf Blocks.WATER_CAULDRON)
            || (hitResult.side != Direction.UP))
            return@register ActionResult.PASS

        stack.remove(BobsMobGearItems.HEATED)
        world.playSound(player, hitResult.blockPos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, player.soundCategory)
        (world as? ServerWorld)?.run {
            val particlePos = hitResult.blockPos.toCenterPos().add(0.0, 0.4, 0.0)
            spawnParticles(ParticleTypes.CLOUD, particlePos.x, particlePos.y, particlePos.z, 4, 0.25, 0.125, 0.25, 0.0)
        }

        ActionResult.SUCCESS
    }

    ItemTickCallback.EVENT.register { entity, stack ->
        if (BobsMobGearItems.HEATED !in stack) return@register

        val state = entity.blockStateAtPos

        if (entity.isInsideWaterOrBubbleColumn
            || (
                    (state isOf Blocks.WATER_CAULDRON || state isOf Blocks.POWDER_SNOW_CAULDRON)
                            && (Blocks.WATER_CAULDRON as AbstractCauldronBlockInvoker).invokeIsEntityTouchingFluid(state, entity.blockPos, entity))
            || entity.wasInPowderSnow) {

            stack.remove(BobsMobGearItems.HEATED)
            entity.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 1f, 1f)
            (entity.world as? ServerWorld)?.run {
                val particlePos = Vec3d(entity.x, entity.getBodyY(0.5), entity.z)
                spawnParticles(ParticleTypes.CLOUD, particlePos.x, particlePos.y, particlePos.z, 4, 0.25, 0.125, 0.25, 0.0)
            }

            return@register
        }

        if (entity is LivingEntity)
            if (entity.isWet) {
                if (entity.age.mod(20) == 0)
                    entity.damage(entity.world.damageSources.onFire(), 1f) // TODO custom damage source?
            } else
                entity.setOnFireForTicks(20)
    }

    ServerLivingEntityEvents.AFTER_DAMAGE.register { entity, source, _, _, blocked ->
        if (!blocked && source.attacker?.weaponStack?.contains(BobsMobGearItems.HEATED) == true) {
            entity.setOnFireFor(4f)
        }
    }

}