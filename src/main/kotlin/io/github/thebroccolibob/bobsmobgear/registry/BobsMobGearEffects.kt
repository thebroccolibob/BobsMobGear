package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.effect.BrokenEffect
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.entry.RegistryEntry

object BobsMobGearEffects {
    fun register(path: String, effect: StatusEffect): RegistryEntry.Reference<StatusEffect> =
        Registry.registerReference(Registries.STATUS_EFFECT, BobsMobGear.id(path), effect)

    fun register(path: String, category: StatusEffectCategory, color: Int, init: StatusEffect.() -> Unit = {}) =
        register(path, object : StatusEffect(category, color) {}.apply(init))

    val BRUISED = register("bruised", StatusEffectCategory.HARMFUL, 0xFFE747)
    @JvmField
    val BROKEN = register("broken", BrokenEffect(StatusEffectCategory.HARMFUL, 0xFFE747, BobsMobGearParticles.STAR))

    fun register() {}
}