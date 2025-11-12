package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.particle.ParticleType
import net.minecraft.particle.SimpleParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object BobsMobGearParticles {
    @JvmStatic
    fun <T: ParticleType<*>> register(id: Identifier, type: T): T =
        Registry.register(Registries.PARTICLE_TYPE, id, type)

    private fun <T: ParticleType<*>> register(path: String, type: T) = register(BobsMobGear.id(path), type)

    @JvmStatic
    fun register(id: Identifier, alwaysSpawn: Boolean = false): SimpleParticleType =
        register(id, FabricParticleTypes.simple(alwaysSpawn))

    private fun register(path: String, alwaysSpawn: Boolean = false) = register(BobsMobGear.id(path), alwaysSpawn)

    @JvmStatic
    fun registerDrips(id: Identifier) = Drips(
        register(id.withPrefixedPath("dripping_")),
        register(id.withPrefixedPath("falling_")),
        register(id.withPrefixedPath("landing_")),
    )

    private fun registerDrips(path: String) = registerDrips(BobsMobGear.id(path))

    val IRON_DRIPS = registerDrips("iron")
    val DIAMOND_DRIPS = registerDrips("diamond")
    val NETHERITE_DRIPS = registerDrips("netherite")
    val BLACK_STEEL_DRIPS = registerDrips("cataclysm_black_steel")

    val SONIC_SHOCKWAVE = register("sonic_shockwave")
    val SONIC_LAUNCH = register("sonic_launch")
    val SONIC_LAUNCH_EMITTER = register("sonic_launch_emitter")

    val BONEK = register("bonek")
    val ATTACK_SPARK = register("attack_spark")
    @JvmField
    val STAR = register("star")

    fun register() {}

    class Drips(
        val dripping: SimpleParticleType,
        val falling: SimpleParticleType,
        val landing: SimpleParticleType,
    )
}
