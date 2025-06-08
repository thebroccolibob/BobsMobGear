package io.github.thebroccolibob.bobsmobgear.client.render.particle

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearParticles
import net.minecraft.client.particle.NoRenderParticle
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType

class SonicLaunchEmitter(world: ClientWorld, x: Double, y: Double, z: Double, velocityX: Double, velocityY: Double, velocityZ: Double) :
    NoRenderParticle(world, x, y, z) {
        init {
            this.velocityX = velocityX
            this.velocityY = velocityY
            this.velocityZ = velocityZ
            maxAge = 1
        }

    override fun tick() {
        repeat(4) {
            val multiplier = 0.2 * it + 0.2
            world.addParticle(BobsMobGearParticles.SONIC_LAUNCH, x, y, z, multiplier * velocityX, multiplier * velocityY, multiplier * velocityZ)
        }
        markDead()
    }

    companion object Factory : ParticleFactory<SimpleParticleType> {
        override fun createParticle(
            parameters: SimpleParticleType,
            world: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double
        ): Particle = SonicLaunchEmitter(world, x, y, z, velocityX, velocityY, velocityZ)
    }
}
