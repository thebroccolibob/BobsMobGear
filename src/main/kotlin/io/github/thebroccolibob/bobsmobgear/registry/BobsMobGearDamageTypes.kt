package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys

object BobsMobGearDamageTypes {
    private fun of(path: String): RegistryKey<DamageType> = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, BobsMobGear.id(path))

    val TELEFRAG = of("telefrag")
}