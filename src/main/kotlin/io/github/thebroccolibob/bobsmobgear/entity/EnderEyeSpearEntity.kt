package io.github.thebroccolibob.bobsmobgear.entity

import com.google.common.base.Predicate
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearDamageTypes
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearEntities
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.*
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.EndermanEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class EnderEyeSpearEntity : AbstractEnderSpearEntity {
    constructor(type: EntityType<out EnderEyeSpearEntity>, world: World) : super(type, world)
    constructor(owner: LivingEntity, world: World, stack: ItemStack) : super(BobsMobGearEntities.ENDER_EYE_SPEAR, owner, world, stack)
    constructor(x: Double, y: Double, z: Double, world: World, stack: ItemStack) : super(BobsMobGearEntities.ENDER_EYE_SPEAR, x, y, z, world, stack)

    var target: Entity? = null
        private set

    private var dealtDamage = false

    override fun getDefaultItemStack(): ItemStack = BobsMobGearItems.IRON_ENDER_EYE_SPEAR.defaultStack

    private fun updateTarget() {
        if (age % 2 == 0 && target?.isAlive != true)
            target = findTarget(world, pos, velocity.normalize(), 64.0, ::canHit)
    }

    override fun canHit(entity: Entity): Boolean {
        return entity != owner && super.canHit(entity)
    }

    override fun tick() {
        super.tick()
        if (world.isClient) return
        updateTarget()
        if (!dealtDamage && !inGround) {
            val target = target ?: return

            velocity *= 0.95
            val difference = target.eyePos - pos
//            val adjust = (target.eyePos - (pos + difference.normalize() * (1 * (velocity dot difference.normalize()))))
            val approxTime = distanceTo(target) / velocity.length()
            val adjust = (target.eyePos + difference.normalize() * (velocity.length() * approxTime) - (pos + velocity * approxTime)) - velocity
            velocity += adjust.normalize() * adjust.length().coerceAtMost(0.5)
        }
    }

    override fun hasNoGravity(): Boolean = !dealtDamage && target != null

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        if (dealtDamage) return
        val entity = entityHitResult.entity
        val stack = itemStack
        val damageSource = damageSources.create(BobsMobGearDamageTypes.PROJECTILE_TELEFRAG, this, owner)
        val damage = getWeaponDamage(world, stack, entity, damageSource)

        playSound(hitSound, 1f, 1f)
        if (entity.damage(damageSource, damage)) {
            if (entity is EndermanEntity) {
                // TODO
                return
            }

            (world as? ServerWorld)?.let {
                EnchantmentHelper.onTargetDamaged(it, entity, damageSource, stack)
            }

            if (entity is LivingEntity) {
                knockback(entity, damageSource)
                onHit(entity)
            }

        }
        dealtDamage = true

        if (hasLoyalty)
            teleportToOwner()
        else
            velocity = velocity.multiply(-0.01, -0.1, -0.01)
    }

    companion object {
        /**
         * @param direction Should be a unit vector
         */
        fun findTarget(world: World, origin: Vec3d, direction: Vec3d, maxDistance: Double, canHit: Predicate<Entity>): Entity? {
            val end = origin + direction * maxDistance
            return world.getOtherEntities(null, Box(
                origin.x - maxDistance * 2,
                origin.y - maxDistance * 2,
                origin.z - maxDistance * 2,
                origin.x + maxDistance * 2,
                origin.y + maxDistance * 2,
                origin.z + maxDistance * 2,
            ), canHit).minByOrNull { distanceToLine(it.pos, origin, end) }
        }

        private fun distanceToLine(point: Vec3d, lineA: Vec3d, lineB: Vec3d): Double =
            ((point - lineA) cross (point - lineB)).length() / (lineB - lineA).length()
    }
}