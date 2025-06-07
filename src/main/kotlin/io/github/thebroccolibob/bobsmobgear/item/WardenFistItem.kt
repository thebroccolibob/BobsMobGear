package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.AttributeModifiersComponent.Entry
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.Item

class WardenFistItem(settings: Settings) : Item(settings) {
    companion object {
        fun createAttributeModifiers() = AttributeModifiersComponent(listOf(
            Entry(EntityAttributes.GENERIC_ATTACK_DAMAGE, EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 9.0, Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND),
            Entry(EntityAttributes.GENERIC_ATTACK_SPEED, EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -3.0, Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND),
            Entry(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, EntityAttributeModifier(BobsMobGear.id("base_attack_knockback"), 1.0, Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND),
        ), true)
    }
}
