package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.particle.ParticleType
import net.minecraft.particle.SimpleParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object BobsMobGearParticles {
    private fun <T: ParticleType<*>> register(path: String, type: T): T =
        Registry.register(Registries.PARTICLE_TYPE, BobsMobGear.id(path), type)

    private fun register(path: String, alwaysSpawn: Boolean = false): SimpleParticleType =
        register(path, FabricParticleTypes.simple(alwaysSpawn))

    private fun registerDrips(name: String) = Drips(
        register("dripping_$name"),
        register("falling_$name"),
        register("landing_$name"),
    )

    val IRON_DRIPS = registerDrips("iron")
    val DIAMOND_DRIPS = registerDrips("diamond")
    val NETHERITE_DRIPS = registerDrips("netherite")

    val SONIC_SHOCKWAVE = register("sonic_shockwave")
    val SONIC_LAUNCH = register("sonic_launch")
    val SONIC_LAUNCH_EMITTER = register("sonic_launch_emitter")

    val BONEK = register("bonek")
    val ATTACK_SPARK = register("attack_spark")

    fun register() {}

    class Drips(
        val dripping: SimpleParticleType,
        val falling: SimpleParticleType,
        val landing: SimpleParticleType,
    )
}
