package io.github.thebroccolibob.bobsmobgear.effect

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.particle.ParticleEffect

class BrokenEffect(category: StatusEffectCategory, color: Int, particleEffect: ParticleEffect) :
    StatusEffect(category, color, particleEffect) {
        init {
            addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, ATTRIBUTE_ID, -0.15, Operation.ADD_MULTIPLIED_TOTAL)
            addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, ATTRIBUTE_ID, -4.0, Operation.ADD_VALUE)
            addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, ATTRIBUTE_ID, -0.5, Operation.ADD_VALUE)
        }

    companion object {
        val ATTRIBUTE_ID = BobsMobGear.id("effect.broken")
    }
}