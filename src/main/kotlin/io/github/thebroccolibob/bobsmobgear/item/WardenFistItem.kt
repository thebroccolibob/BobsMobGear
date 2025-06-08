package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearParticles
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.minus
import io.github.thebroccolibob.bobsmobgear.util.times
import net.minecraft.block.BlockState
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.AttributeModifiersComponent.Entry
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World

class WardenFistItem(settings: Settings) : Item(settings), UsingAttackable {

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        // TODO sonic charge check
        val stack = user[hand]
        user.setCurrentHand(hand)
        user.playSound(SoundEvents.ENTITY_WARDEN_SONIC_CHARGE)
        return TypedActionResult.consume(stack)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
       if (world.isClient || user.itemUseTime < USE_TIME || (user as? PlayerEntity)?.itemCooldownManager?.isCoolingDown(this) == true) return
        world.playSoundFromEntity(null, user, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, user.soundCategory, 1f, 1f)
        (world as? ServerWorld)?.spawnParticles(BobsMobGearParticles.SONIC_SHOCKWAVE, user.x, user.getBodyY(0.33), user.z, 0, 0.0, 0.0, 0.0, 0.0)
        for (entity in world.getOtherEntities(user, Box.of(user.pos, 2 * BLAST_RANGE, 2 * BLAST_RANGE, 2 * BLAST_RANGE))) {
            if (user.squaredDistanceTo(entity) > BLAST_RANGE * BLAST_RANGE) continue
            entity.damage(world.damageSources.sonicBoom(user), 5f)
            if (entity is ProjectileEntity)
                entity.setVelocity((entity.pos - user.pos).normalize() * entity.velocity.length().coerceAtLeast(1.2))
            else
                entity.addVelocity(((entity.pos - user.pos).multiply(1.0, 0.0, 1.0).normalize() * 1.2).add(0.0, 0.5, 0.0))
            entity.velocityModified = true
        }
        (user as? PlayerEntity)?.itemCooldownManager?.set(this, COOLDOWN)
    }

    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity?): Int = 72000

    override fun getUseAction(stack: ItemStack?): UseAction = UseAction.BOW

    override fun canMine(state: BlockState?, world: World?, pos: BlockPos?, miner: PlayerEntity?): Boolean = false

    override fun postHit(stack: ItemStack?, target: LivingEntity?, attacker: LivingEntity?): Boolean = true

    override fun postDamageEntity(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) {
        if (attacker.activeItem != stack) return
        attacker.world.playSoundFromEntity(null, attacker, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, attacker.soundCategory, 1f, 1f)
        val velocity = ((target.pos - attacker.pos).multiply(1.0, 0.0, 1.0).normalize() * 2.5).add(0.0, 0.5, 0.0)
        target.addVelocity(velocity)
        (target.world as? ServerWorld)?.spawnParticles(BobsMobGearParticles.SONIC_LAUNCH_EMITTER, target.x, target.getBodyY(0.5), target.z, 0, velocity.x / 4, velocity.y / 4, velocity.z / 4, 1.0)
        (attacker as? PlayerEntity)?.itemCooldownManager?.set(this, COOLDOWN)
    }

    override fun canAttackWhileUsing(stack: ItemStack, user: LivingEntity): Boolean = user.itemUseTime >= USE_TIME

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
