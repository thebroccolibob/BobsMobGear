package io.github.thebroccolibob.bobsmobgear.client.render.particle

import io.github.thebroccolibob.bobsmobgear.client.util.PendingParticleFactory
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.PendingParticleFactory
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.particle.SpriteBillboardParticle
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType

class CartoonParticle(clientWorld: ClientWorld, x: Double, y: Double, z: Double) :
    SpriteBillboardParticle(clientWorld, x, y, z) {
        init {
            maxAge = 30
        }

    override fun getSize(tickDelta: Float): Float {
        val fAge = age + tickDelta
        return when {
            fAge < 5f -> (fAge) / 5f
            fAge < 25f -> 1f
            fAge < 30f -> (30 - fAge) / 5f
            else -> 0f
        }
    }

    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_OPAQUE

    companion object Factory : PendingParticleFactory<SimpleParticleType> by PendingParticleFactory(::CartoonParticle)
}