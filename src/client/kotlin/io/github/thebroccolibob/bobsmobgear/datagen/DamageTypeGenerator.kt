package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearDamageTypes
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.minecraft.entity.damage.DamageEffects
import net.minecraft.entity.damage.DamageScaling
import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.*
import java.util.concurrent.CompletableFuture

class DamageTypeGenerator(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricDynamicRegistryProvider(output, registriesFuture) {
    override fun getName(): String = "Damage Types"

    override fun configure(registries: RegistryWrapper.WrapperLookup, entries: Entries) {
        val damageTypes = registries.getWrapperOrThrow(RegistryKeys.DAMAGE_TYPE)
        fun add(key: RegistryKey<DamageType>) {
            entries.add(damageTypes.getOrThrow(key))
        }
        add(BobsMobGearDamageTypes.PROJECTILE_TELEFRAG)
        add(BobsMobGearDamageTypes.BASE_TELEFRAG)
        add(BobsMobGearDamageTypes.SELF_TELEFRAG)
    }

    companion object : RegistryBuilder.BootstrapFunction<DamageType> {
        private fun Registerable<DamageType>.register(
            key: RegistryKey<DamageType>,
            exhaustion: Float,
            damageTypeName: String = key.value.path,
            scaling: DamageScaling = DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            effects: DamageEffects = DamageEffects.HURT
        ) {
            register(key, DamageType("${key.value.namespace}.$damageTypeName", scaling, exhaustion, effects))
        }

        override fun run(registerable: Registerable<DamageType>) {
            registerable.register(BobsMobGearDamageTypes.PROJECTILE_TELEFRAG, 0.1f, "telefrag", effects = DamageEffects.BURNING)
            registerable.register(BobsMobGearDamageTypes.BASE_TELEFRAG, 0.1f, "telefrag", effects = DamageEffects.BURNING)
            registerable.register(BobsMobGearDamageTypes.SELF_TELEFRAG, 0.1f, effects = DamageEffects.BURNING)
        }
    }
}