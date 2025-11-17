package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearParticles
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider
import com.mojang.serialization.Codec
import net.minecraft.data.DataOutput
import net.minecraft.particle.ParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

typealias ParticleProvider = BiConsumer<Identifier, List<Identifier>>

class ParticleDataGenerator(
    dataOutput: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>,
) : FabricCodecDataProvider<List<Identifier>>(dataOutput, registriesFuture, DataOutput.OutputType.RESOURCE_PACK, "particles", CODEC) {
    override fun configure(
        provider: ParticleProvider,
        lookup: RegistryWrapper.WrapperLookup
    ) {
        provider.registerDrips(BobsMobGearParticles.IRON_DRIPS)
        provider.registerDrips(BobsMobGearParticles.DIAMOND_DRIPS)
        provider.registerDrips(BobsMobGearParticles.NETHERITE_DRIPS)
        provider.registerDrips(BobsMobGearParticles.BLACK_STEEL_DRIPS)

        provider.register(BobsMobGearParticles.SONIC_SHOCKWAVE, BobsMobGear.id("sonic_boom"))
        provider.register(BobsMobGearParticles.SONIC_LAUNCH, 7, 15, Identifier.ofVanilla("sonic_boom_"))
        provider.register(BobsMobGearParticles.BONEK, BobsMobGear.id("bonek"))
        provider.register(BobsMobGearParticles.ATTACK_SPARK, 4, BobsMobGear.id("attack_spark_"))
        provider.register(BobsMobGearParticles.STAR, BobsMobGear.id("star"))
    }

    override fun getName(): String = "Particle Texture Data"

    companion object {
        val CODEC: Codec<List<Identifier>> = Identifier.CODEC.listOf().fieldOf("textures").codec()

        private fun ParticleProvider.register(particle: ParticleType<*>, textures: List<Identifier>) {
            accept(Registries.PARTICLE_TYPE.getId(particle)!!, textures)
        }

        private fun ParticleProvider.register(particle: ParticleType<*>, min: Int, max: Int, name: Identifier) {
            register(particle, (min..max).map { name.withSuffixedPath(it.toString()) })
        }

        private fun ParticleProvider.register(particle: ParticleType<*>, count: Int, name: Identifier) {
            register(particle, 0, count - 1, name)
        }

        private fun ParticleProvider.register(particle: ParticleType<*>, vararg textures: Identifier) {
            register(particle, textures.toList())
        }

        @JvmStatic
        fun ParticleProvider.registerDrips(drips: BobsMobGearParticles.Drips) {
            register(drips.dripping, Identifier.ofVanilla("drip_hang"))
            register(drips.falling, Identifier.ofVanilla("drip_fall"))
            register(drips.landing, Identifier.ofVanilla("drip_land"))
        }
    }
}
