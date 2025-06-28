package io.github.thebroccolibob.bobsmobgear.client.render.particle

import io.github.thebroccolibob.bobsmobgear.client.util.PendingParticleFactory
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.PendingParticleFactory
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.particle.SpriteBillboardParticle
import net.minecraft.client.render.Camera
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType
import org.joml.Quaternionf
import kotlin.math.PI
import kotlin.math.sqrt

class SonicShockwaveParticle(world: ClientWorld, x: Double, y: Double, z: Double) : SpriteBillboardParticle(world, x, y, z) {
    init {
        maxAge = 60
    }

    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT

    override fun getSize(tickDelta: Float): Float = 0.7f * sqrt(age + tickDelta) + 0.5f

    override fun getBrightness(tint: Float): Int = LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE

    override fun buildGeometry(vertexConsumer: VertexConsumer, camera: Camera, tickDelta: Float) {
        val dAge = age + tickDelta
        alpha = ((60 - dAge) / 40f).coerceIn(0f, 1f)
        val quaternionf = Quaternionf()
        quaternionf.rotateX(PI.toFloat() / 2)
        method_60373(vertexConsumer, camera, quaternionf, tickDelta)
        quaternionf.rotateX(-PI.toFloat())
        method_60373(vertexConsumer, camera, quaternionf, tickDelta)
    }

    companion object Factory: PendingParticleFactory<SimpleParticleType> by PendingParticleFactory(::SonicShockwaveParticle)
}
