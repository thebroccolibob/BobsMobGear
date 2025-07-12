package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearParticles
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearSounds
import io.github.thebroccolibob.bobsmobgear.util.*
import net.minecraft.block.BlockState
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class BoneHammerItem(private val cooldown: Int, material: ToolMaterial, settings: Settings) : ToolItem(material, settings.apply {
    attributeModifiers(SwordItem.createAttributeModifiers(material, 3, -3.2f))
}), HasSpecialAttack {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        (world as? ServerWorld)?.spawnParticles(BobsMobGearParticles.ATTACK_SPARK, user.x, user.getBodyY(0.5), user.z, 24, 0.0, 0.0, 0.0, 0.3)
        user.setCurrentHand(hand)
        return TypedActionResult.consume(user[hand])
    }

    override fun usageTick(world: World, user: LivingEntity, stack: ItemStack?, remainingUseTicks: Int) {
        if (world.isClient && user.itemUseTime == USE_TIME)
            user.playSound(BobsMobGearSounds.WEAPON_ATTACK_READY)
        super.usageTick(world, user, stack, remainingUseTicks)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (user !is PlayerEntity || user.itemUseTime < USE_TIME) return
        user.clearActiveItem()
        runSpecialAttack(user, user.activeHand, world)
    }

    override fun getUseAction(stack: ItemStack?): UseAction = UseAction.SPEAR

    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity?): Int = 72000

    override fun postHit(stack: ItemStack?, target: LivingEntity?, attacker: LivingEntity?): Boolean = true

    override fun postDamageEntity(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) {
        if (target.isDead || BobsMobGearItems.USING_SPECIAL_ATTACK in stack)
            (target.world as? ServerWorld)?.spawnParticles(BobsMobGearParticles.BONEK, target.x, target.getBodyY(0.67), target.z, 1, target.width / 2.0, target.height / 3.0, target.width / 2.0, 0.0)
    }

    override fun canMine(state: BlockState?, world: World?, pos: BlockPos?, miner: PlayerEntity): Boolean = !miner.isCreative

    override fun onAttackEnd(player: ServerPlayerEntity, targetCount: Int, stack: ItemStack) {
        if (BobsMobGearItems.USING_SPECIAL_ATTACK in stack) {
            val center = Vec3d(player.x, player.getBodyY(0.5), player.z) + player.rotationVector.horizontal().normalize() * HIT_DISTANCE
            //            (player.world as? ServerWorld)?.syncWorldEvent(WorldEvents.SMASH_ATTACK, BlockPos(center.x.toInt(), (player.y - 1).toInt(), center.z.toInt()), 250)
            for (target in player.world.getOtherEntities(player, Box.of(center, 2 * MAX_DISTANCE, player.height.toDouble() + 2, 2 * MAX_DISTANCE)) { it is LivingEntity }) {
                val difference = (target.pos - center).horizontal()
                val closeness = 1 - difference.length() / MAX_DISTANCE
                if (closeness < 0) continue
                target.velocity += (difference.normalize() * (MAX_HORIZONTAL_VELOCITY * closeness)).add(0.0, closeness * MAX_VERTICAL_VELOCITY, 0.0)
                target.velocityModified = true
            }
            player.itemCooldownManager.set(stack.item, cooldown)
        }
        super.onAttackEnd(player, targetCount, stack)
    }

    companion object {
        const val HIT_DISTANCE = 1.0
        const val MAX_DISTANCE = 5.0
        const val MAX_HORIZONTAL_VELOCITY = 2.0
        const val MAX_VERTICAL_VELOCITY = 0.5
        const val USE_TIME = 30
    }
}