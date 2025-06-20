package io.github.thebroccolibob.bobsmobgear.client.render.particle

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.PendingParticleFactory
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.particle.SpriteBillboardParticle
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.render.Camera
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType
import org.joml.Quaternionf
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

class SonicLaunchParticle(world: ClientWorld, x: Double, y: Double, z: Double, private val rotationX: Float, private val rotationY: Float, private val spriteProvider: SpriteProvider) :
    SpriteBillboardParticle(world, x, y, z) {
    init {
        maxAge = 8
        val f = random.nextFloat() * 0.6f + 0.4f
        red = f
        green = f
        blue = f
//        alpha = 0.5f
        setSpriteForAge(spriteProvider)
    }

    constructor(world: ClientWorld, x: Double, y: Double, z: Double, directionX: Double, directionY: Double, directionZ: Double, spriteProvider: SpriteProvider) : this(
        world, x, y, z, -atan2(directionY, sqrt(directionX * directionX + directionZ * directionZ)).toFloat(), atan2(directionX, directionZ).toFloat(), spriteProvider
    )

    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT

    override fun getSize(tickDelta: Float): Float = 2f

    override fun getBrightness(tint: Float): Int = LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE

    override fun buildGeometry(vertexConsumer: VertexConsumer, camera: Camera, tickDelta: Float) {
//        val dAge = age + tickDelta
//        this.alpha = 0.5f * if (dAge > 20) (30 - dAge).coerceAtLeast(0f) / 10f else 1f
        val quaternionf = Quaternionf()
        quaternionf.rotateY(rotationY)
        quaternionf.rotateX(rotationX)
        method_60373(vertexConsumer, camera, quaternionf, tickDelta)
        quaternionf.rotateX(-PI.toFloat())
        method_60373(vertexConsumer, camera, quaternionf, tickDelta)
    }

    override fun tick() {
        super.tick()
        setSpriteForAge(spriteProvider)
    }

    companion object Factory: PendingParticleFactory<SimpleParticleType> {
        override fun create(provider: FabricSpriteProvider) = ParticleFactory<SimpleParticleType> { _, world, x, y, z, vx, vy, vz ->
            SonicLaunchParticle(world, x, y, z, vx, vy, vz, provider)
        }
    }
}
