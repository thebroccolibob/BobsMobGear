package io.github.thebroccolibob.bobsmobgear.client.util

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.PendingParticleFactory
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteBillboardParticle
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType

fun <T: Particle> ParticleFactory(createParticle: (ClientWorld, Double, Double, Double, Double, Double, Double) -> T): ParticleFactory<SimpleParticleType> =
    ParticleFactory<SimpleParticleType> { _, world, x, y, z, vx, vy, vz ->
        createParticle(world, x, y, z, vx, vy, vz)
    }

fun <T: Particle> PendingParticleFactory(createParticle: (ClientWorld, Double, Double, Double, Double, Double, Double, SpriteProvider) -> T) =
    PendingParticleFactory<SimpleParticleType> { provider ->
        ParticleFactory { _, world, x, y, z, vx, vy, vz ->
            createParticle(world, x, y, z, vx, vy, vz, provider)
        }
    }

fun <T: SpriteBillboardParticle> PendingParticleFactory(createParticle: (ClientWorld, Double, Double, Double, Double, Double, Double) -> T) =
    PendingParticleFactory<SimpleParticleType> { provider ->
        ParticleFactory { _, world, x, y, z, vx, vy, vz ->
            createParticle(world, x, y, z, vx, vy, vz).apply {
                setSprite(provider)
            }
        }
    }

fun <T: Particle> ParticleFactory(createParticle: (ClientWorld, Double, Double, Double) -> T) =
    ParticleFactory<SimpleParticleType> { _, world, x, y, z, _, _, _ ->
        createParticle(world, x, y, z)
    }

fun <T: Particle> PendingParticleFactory(createParticle: (ClientWorld, Double, Double, Double, SpriteProvider) -> T) =
    PendingParticleFactory<SimpleParticleType> { provider ->
        ParticleFactory { _, world, x, y, z, _, _, _ ->
            createParticle(world, x, y, z, provider)
        }
    }

fun <T: SpriteBillboardParticle> PendingParticleFactory(createParticle: (ClientWorld, Double, Double, Double) -> T) =
    PendingParticleFactory<SimpleParticleType> { provider ->
        ParticleFactory { _, world, x, y, z, _, _, _ ->
            createParticle(world, x, y, z).apply {
                setSprite(provider)
            }
        }
    }
