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
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*

class EnderEyeSpearEntity : AbstractEnderSpearEntity {
    constructor(type: EntityType<out EnderEyeSpearEntity>, world: World) : super(type, world)
    constructor(owner: LivingEntity, world: World, stack: ItemStack) : super(BobsMobGearEntities.ENDER_EYE_SPEAR, owner, world, stack)
    constructor(x: Double, y: Double, z: Double, world: World, stack: ItemStack) : super(BobsMobGearEntities.ENDER_EYE_SPEAR, x, y, z, world, stack)

    private var hasHit = false
    private var endermanHits = 0
    private var returnTicks = 0
    private var returnTime = if (hasLoyalty)
            (world as? ServerWorld)
                ?.let { EnchantmentHelper.getTridentReturnAcceleration(it, itemStack, owner) }
                ?.takeIf { it != 0 }
                ?.let { 80 / it }
                ?: 0
            else 0
    var maxRange = 16.0

    var targetUUID: UUID? = null
    var target by entityProperty(::targetUUID)

    override fun getDefaultItemStack(): ItemStack = BobsMobGearItems.IRON_ENDER_EYE_SPEAR.defaultStack

    private fun updateTarget() {
        if (age % 2 == 0 && target?.isAlive != true)
            target = findTarget(world, pos, velocity.normalize(), maxRange.toDouble(), ::canHit)
    }

    override fun canHit(entity: Entity): Boolean {
        return entity != owner && super.canHit(entity)
    }

    override fun tick() {
        super.tick()
        if (!world.isClient)
            updateTarget()

        if (hasHit) {
            if (returnTime <= 0) return
            returnTicks++
            if (returnTicks > returnTime) {
                teleportToOwner()
                returnTime = 0
            }
            return
        }
        if (inGround) return

        val target = target ?: return

        velocity *= 0.8
        val positionDiff = target.eyePos - pos
//            val adjust = (target.eyePos - (pos + difference.normalize() * (1 * (velocity dot difference.normalize()))))
        val distanceToTarget = positionDiff.length()
        val approxTime = distanceToTarget / velocity.length()
        val estimatedMovement = velocity * approxTime
        val targetVelocity = positionDiff * 2.0 - estimatedMovement
        val velocityDiff = targetVelocity - velocity
        velocity += velocityDiff.normalize() * velocityDiff.length().coerceAtMost(0.2)
        velocityDirty = true
    }

    override fun hasNoGravity(): Boolean = !hasHit && target != null

    override fun onDeflected(deflector: Entity?, fromAttack: Boolean) {
        target = null
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        if (hasHit) return
        val entity = entityHitResult.entity
        val stack = itemStack
        val damageSource = damageSources.create(BobsMobGearDamageTypes.PROJECTILE_TELEFRAG, this, owner)
        val damage = getWeaponDamage(world, stack, entity, damageSource)

        val isEnderman = entity is EndermanEntity

        if (isEnderman)
            endermanHits++
        else
            playSound(hitSound, 1f, 1f)


        if (isEnderman && endermanHits >= 4) {
            entity.damage(damageSources.create(BobsMobGearDamageTypes.BASE_TELEFRAG, this, owner), damage)
        } else if (entity.damage(damageSource, damage)) {
            (world as? ServerWorld)?.let {
                EnchantmentHelper.onTargetDamaged(it, entity, damageSource, stack)
            }

            if (entity is LivingEntity) {
                knockback(entity, damageSource)
                onHit(entity)
            }

            if (isEnderman) {
                bounce()
                return
            }
        }

        hasHit = true

        bounce()
    }

    override fun onBlockHit(blockHitResult: BlockHitResult) {
        super.onBlockHit(blockHitResult)
        hasHit = true
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putInt(RETURN_TIME_NBT, returnTime)
        nbt.putDouble(MAX_RANGE_NBT, maxRange)
        nbt.putBoolean(HAS_HIT_NBT, hasHit)
        targetUUID?.let { nbt.putUuid(TARGET_UUID_NBT, it) }
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        returnTime = nbt.getInt(RETURN_TIME_NBT)
        maxRange = nbt.getDouble(MAX_RANGE_NBT)
        hasHit = nbt.getBoolean(HAS_HIT_NBT)
        targetUUID = if (nbt.containsUuid(TARGET_UUID_NBT)) nbt.getUuid(TARGET_UUID_NBT) else null
    }

    companion object {
        private const val RETURN_TIME_NBT = "return_time"
        private const val MAX_RANGE_NBT = "max_range"
        private const val TARGET_UUID_NBT = "target_uuid"
        private const val HAS_HIT_NBT = "has_hit"

        /**
         * @param direction Should be a unit vector
         */
        fun findTarget(world: World, origin: Vec3d, direction: Vec3d, maxDistance: Double, canHit: Predicate<Entity>): Entity? {
            val end = origin + direction * maxDistance
            return world.getOtherEntities(null, Box.of(origin, 2 * maxDistance, 2 * maxDistance, 2 * maxDistance), canHit).minByOrNull {
                if ((it.pos - origin) dot direction > 0)
                    distanceToLine(it.pos, origin, end)
                else
                    it.pos.distanceTo(origin)
            }
        }

        private fun distanceToLine(point: Vec3d, lineA: Vec3d, lineB: Vec3d): Double =
            ((point - lineA) cross (point - lineB)).length() / (lineB - lineA).length()
    }
}