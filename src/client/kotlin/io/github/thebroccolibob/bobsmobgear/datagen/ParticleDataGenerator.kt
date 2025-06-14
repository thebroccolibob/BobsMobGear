package io.github.thebroccolibob.bobsmobgear.datagen

import com.mojang.serialization.Codec
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearParticles
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider
import net.minecraft.data.DataOutput
import net.minecraft.particle.ParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class ParticleDataGenerator(
    dataOutput: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>,
) : FabricCodecDataProvider<List<Identifier>>(dataOutput, registriesFuture, DataOutput.OutputType.RESOURCE_PACK, "particles", CODEC) {
    override fun configure(
        provider: BiConsumer<Identifier, List<Identifier>>,
        lookup: RegistryWrapper.WrapperLookup
    ) {
        fun register(particle: ParticleType<*>, textures: List<Identifier>) {
            provider.accept(Registries.PARTICLE_TYPE.getId(particle)!!, textures)
        }

        fun register(particle: ParticleType<*>, vararg textures: Identifier) {
            register(particle, textures.toList())
        }

        fun registerDrips(drips: BobsMobGearParticles.Drips) {
            register(drips.dripping, Identifier.ofVanilla("drip_hang"))
            register(drips.falling, Identifier.ofVanilla("drip_fall"))
            register(drips.landing, Identifier.ofVanilla("drip_land"))
        }

        registerDrips(BobsMobGearParticles.IRON_DRIPS)
        registerDrips(BobsMobGearParticles.DIAMOND_DRIPS)
        registerDrips(BobsMobGearParticles.NETHERITE_DRIPS)

        register(BobsMobGearParticles.SONIC_SHOCKWAVE, BobsMobGear.id("sonic_boom"))
        register(BobsMobGearParticles.SONIC_LAUNCH, (7..15).map { Identifier.ofVanilla("sonic_boom_$it") })
        register(BobsMobGearParticles.BONEK, BobsMobGear.id("bonek"))
    }

    override fun getName(): String = "Particle Texture Data"

    companion object {
        val CODEC: Codec<List<Identifier>> = Identifier.CODEC.listOf().fieldOf("textures").codec()
    }
}
