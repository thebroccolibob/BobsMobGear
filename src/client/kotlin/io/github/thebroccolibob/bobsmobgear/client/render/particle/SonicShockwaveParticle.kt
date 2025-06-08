package io.github.thebroccolibob.bobsmobgear.client.render.particle

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.PendingParticleFactory
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.particle.SpriteBillboardParticle
import net.minecraft.client.render.Camera
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.math.MathHelper.lerp
import org.joml.Vector3f
import kotlin.math.sqrt

class SonicShockwaveParticle(world: ClientWorld, x: Double, y: Double, z: Double) : SpriteBillboardParticle(world, x, y, z) {
    init {
        maxAge = 60
        alpha = 0.5f
    }

    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT

    override fun getSize(tickDelta: Float): Float = 0.5f * sqrt(age + tickDelta) + 0.5f

    override fun buildGeometry(vertexConsumer: VertexConsumer, camera: Camera, tickDelta: Float) {
        val renderX = (lerp(tickDelta.toDouble(), this.prevPosX, this.x) - camera.pos.x).toFloat()
        val renderY = (lerp(tickDelta.toDouble(), this.prevPosY, this.y) - camera.pos.y).toFloat() + 0.05f
        val renderZ = (lerp(tickDelta.toDouble(), this.prevPosZ, this.z) - camera.pos.z).toFloat()

        val size = getSize(tickDelta)

        val vertices = arrayOf(
            Vector3f(-1.0f, 0.0f, -1.0f),
            Vector3f(-1.0f, 0.0f, 1.0f),
            Vector3f(1.0f, 0.0f, 1.0f),
            Vector3f(1.0f, 0.0f, -1.0f)
        ).onEach {
            it.mul(size)
            it.add(renderX, renderY, renderZ)
        }

        val light = LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE

        vertices.forEachIndexed { index, vec ->
            with (vertexConsumer.vertex(vec.x, vec.y, vec.z)) {
                when (index) {
                    0 -> texture(maxU, maxV)
                    1 -> texture(maxU, minV)
                    2 -> texture(minU, minV)
                    3 -> texture(minU, maxV)
                }
                val dAge = age + tickDelta
                val aAlpha = if (dAge > 30) (60 - dAge).coerceAtLeast(0f) / 30f else 1f
                color(red, green, blue, alpha * aAlpha)
                light(light)
            }
        }
    }

    companion object Factory: PendingParticleFactory<SimpleParticleType> {
        override fun create(provider: FabricSpriteProvider) = ParticleFactory<SimpleParticleType> { _, world, x, y, z, _, _, _ ->
            SonicShockwaveParticle(world, x, y, z).apply {
                setSprite(provider)
            }
        }
    }
}
