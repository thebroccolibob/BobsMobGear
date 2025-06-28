package io.github.thebroccolibob.bobsmobgear.client.render.particle

import io.github.thebroccolibob.bobsmobgear.client.util.ParticleFactory
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearParticles
import net.minecraft.client.particle.NoRenderParticle
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
        repeat(8) {
            world.addParticle(BobsMobGearParticles.SONIC_LAUNCH, x + 2 * it * velocityX, y + 2 * it * velocityY, z + 2 * it * velocityZ, velocityX, velocityY, velocityZ)
        }
        markDead()
    }

    companion object Factory : ParticleFactory<SimpleParticleType> by ParticleFactory(::SonicLaunchEmitter)
}
