package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearParticles
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.minus
import io.github.thebroccolibob.bobsmobgear.util.times
import net.minecraft.block.BlockState
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.AttributeModifiersComponent.Entry
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterials
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class WardenFistItem(settings: Settings) : ToolItem(ToolMaterials.NETHERITE, settings), UsingAttackable {

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user[hand]
        if (stack[BobsMobGearItems.MAX_SONIC_CHARGE]?.let { (stack[BobsMobGearItems.SONIC_CHARGE] ?: 0) < it } != false) return TypedActionResult.pass(stack)
        user.setCurrentHand(hand)
        user.playSound(SoundEvents.ENTITY_WARDEN_SONIC_CHARGE)
        return TypedActionResult.consume(stack)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
       if (world.isClient || user.itemUseTime < USE_TIME || (user as? PlayerEntity)?.itemCooldownManager?.isCoolingDown(this) == true) return
        world.playSoundFromEntity(null, user, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, user.soundCategory, 1f, 1f)
        (world as? ServerWorld)?.spawnParticles(BobsMobGearParticles.SONIC_SHOCKWAVE, user.x, user.getBodyY(0.45), user.z, 0, 0.0, 0.0, 0.0, 0.0)
        for (entity in world.getOtherEntities(user, Box.of(user.pos, 2 * BLAST_RANGE, 2 * BLAST_RANGE, 2 * BLAST_RANGE))) {
            if (user.squaredDistanceTo(entity) > BLAST_RANGE * BLAST_RANGE) continue

            val difference = entity.pos - user.pos
            val strength = 1 - difference.length() / BLAST_RANGE

            if (entity is LivingEntity)
                entity.damage(world.damageSources.sonicBoom(user), (10 * strength.toFloat()).coerceAtMost(5f))

            if (entity is ProjectileEntity) {
                entity.setVelocity(difference.normalize() * entity.velocity.length().coerceAtLeast(1.2))
                entity.velocityDirty = true
            } else {
                entity.addVelocity((difference.multiply(1.0, 0.0, 1.0).normalize() * 2.0 * strength).add(0.0, strength, 0.0))
                entity.velocityModified = true
            }
        }
        if (!user.isInCreativeMode)
            stack[BobsMobGearItems.SONIC_CHARGE] = 0
        (user as? PlayerEntity)?.itemCooldownManager?.set(this, COOLDOWN)
    }

    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity?): Int = 72000

    override fun getUseAction(stack: ItemStack?): UseAction = UseAction.BOW

    override fun canMine(state: BlockState?, world: World?, pos: BlockPos?, miner: PlayerEntity?): Boolean = false

    override fun postHit(stack: ItemStack?, target: LivingEntity?, attacker: LivingEntity?): Boolean = true

    override fun postDamageEntity(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND)
        if (attacker.activeItem != stack) return
        attacker.world.playSoundFromEntity(null, attacker, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, attacker.soundCategory, 1f, 1f)
        val velocity = (attacker.rotationVector.normalize() * 2.5).let {
            if (target.isOnGround && it.y < 0.5) it.withAxis(Direction.Axis.Y, 0.5) else it
        }
        target.addVelocity(velocity)
        (target.world as? ServerWorld)?.spawnParticles(BobsMobGearParticles.SONIC_LAUNCH_EMITTER, target.x, target.getBodyY(0.5), target.z, 0, velocity.x, velocity.y, velocity.z, 1.0)
        if (!attacker.isInCreativeMode)
            stack[BobsMobGearItems.SONIC_CHARGE] = 0
        (attacker as? PlayerEntity)?.itemCooldownManager?.set(this, COOLDOWN)
    }

    override fun canAttackWhileUsing(stack: ItemStack, user: LivingEntity): Boolean = user.weaponStack == stack && user.itemUseTime >= USE_TIME

    companion object {
        const val USE_TIME = 30
        const val BLAST_RANGE = 5.0
        const val COOLDOWN = 100

        fun createAttributeModifiers() = AttributeModifiersComponent(listOf(
            Entry(EntityAttributes.GENERIC_ATTACK_DAMAGE, EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 9.0, Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND),
            Entry(EntityAttributes.GENERIC_ATTACK_SPEED, EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -3.0, Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND),
            Entry(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, EntityAttributeModifier(BobsMobGear.id("base_attack_knockback"), 1.0, Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND),
        ), true)
    }
}
