package io.github.thebroccolibob.bobsmobgear.item

import com.google.common.collect.HashMultimap
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearParticles
import io.github.thebroccolibob.bobsmobgear.util.*
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class BoneHammerItem(material: ToolMaterial, settings: Settings) : ToolItem(material, settings.apply {
    attributeModifiers(SwordItem.createAttributeModifiers(material, 3, -3.2f))
}), HasSpecialAttack {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        user.attributes.addTemporaryModifiers(SPECIAL_ATTACK_MODIFIERS)
        return runSpecialAttack(user, hand, world)
    }

    override fun postHit(stack: ItemStack?, target: LivingEntity?, attacker: LivingEntity?): Boolean = true

    override fun postDamageEntity(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) {
        if (target.isDead || BobsMobGearItems.USING_SPECIAL_ATTACK in stack)
            (target.world as? ServerWorld)?.spawnParticles(BobsMobGearParticles.BONEK, target.x, target.getBodyY(0.67), target.z, 1, target.width / 2.0, target.height / 3.0, target.width / 2.0, 0.0)
    }

    override fun onAttackEnd(player: ServerPlayerEntity, targetCount: Int, stack: ItemStack) {
        if (BobsMobGearItems.USING_SPECIAL_ATTACK !in stack) {
            super.onAttackEnd(player, targetCount, stack)
            return
        }
        val center = Vec3d(player.x, player.getBodyY(0.5), player.z) + player.rotationVector.horizontal().normalize() * HIT_DISTANCE
        //            (player.world as? ServerWorld)?.syncWorldEvent(WorldEvents.SMASH_ATTACK, BlockPos(center.x.toInt(), (player.y - 1).toInt(), center.z.toInt()), 250)
        for (target in player.world.getOtherEntities(player, Box.of(center, 2 * MAX_DISTANCE, player.height.toDouble(), 2 * MAX_DISTANCE))) {
            val difference = (target.pos - center).horizontal()
            val closeness = 1 - difference.length() / MAX_DISTANCE
            if (closeness < 0) continue
            target.velocity += (difference * (MAX_HORIZONTAL_VELOCITY * closeness)).add(0.0, closeness * MAX_VERTICAL_VELOCITY, 0.0)
        }
        player.itemCooldownManager.set(stack.item, COOLDOWN)
        super.onAttackEnd(player, targetCount, stack)
        player.attributes.removeModifiers(SPECIAL_ATTACK_MODIFIERS)
    }

    companion object {
        const val HIT_DISTANCE = 1.0
        const val MAX_DISTANCE = 3.0
        const val MAX_HORIZONTAL_VELOCITY = 2.0
        const val MAX_VERTICAL_VELOCITY = 1.0
        const val COOLDOWN = 5 * 20

        private val SPECIAL_ATTACK_MODIFIERS: HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> = multimapOf(
            EntityAttributes.GENERIC_ATTACK_SPEED to EntityAttributeModifier(BobsMobGear.id("bone_hammer_special_speed"), -0.5, Operation.ADD_MULTIPLIED_BASE),
        )
    }
}