package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.util.get
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.LivingEntity.getSlotForHand
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import kotlin.math.min

class SmithingHammerItem(settings: Settings) : Item(settings) {
    override fun useOnEntity(stack: ItemStack, user: PlayerEntity, entity: LivingEntity, hand: Hand): ActionResult {
        val equipment = EquipmentSlot.entries
            .shuffled()
            .firstNotNullOfOrNull { slot ->
                entity[slot].takeUnless { it.isEmpty || !it.isDamaged }
            } ?: return ActionResult.FAIL

        if (user.world.isClient) return ActionResult.SUCCESS

        val repairedAmount = min(equipment.damage, stack.maxDamage - stack.damage)

        equipment.damage -= repairedAmount
        stack.damage(repairedAmount, user, getSlotForHand(hand))

        user.world.playSoundFromEntity(null, entity, SoundEvents.BLOCK_SMITHING_TABLE_USE, user.soundCategory, 1f, 1f) // TODO custom sound event
        user.addCritParticles(entity)

        return ActionResult.SUCCESS
    }

    // Technically a weapon
    override fun postHit(stack: ItemStack?, target: LivingEntity?, attacker: LivingEntity?): Boolean = true

    override fun postDamageEntity(stack: ItemStack, target: LivingEntity?, attacker: LivingEntity) {
        stack.damage(2, attacker, EquipmentSlot.MAINHAND)
    }

}