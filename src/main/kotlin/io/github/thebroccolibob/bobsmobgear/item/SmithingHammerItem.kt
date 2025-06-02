package io.github.thebroccolibob.bobsmobgear.item

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class SmithingHammerItem(settings: Settings) : Item(settings) {
    // Technically a weapon
    override fun postHit(stack: ItemStack?, target: LivingEntity?, attacker: LivingEntity?): Boolean = true

    override fun postDamageEntity(stack: ItemStack, target: LivingEntity?, attacker: LivingEntity) {
        stack.damage(2, attacker, EquipmentSlot.MAINHAND)
    }

    override fun getEnchantability(): Int = 1
}