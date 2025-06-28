package io.github.thebroccolibob.bobsmobgear.client.render.particle

import io.github.thebroccolibob.bobsmobgear.client.util.PendingParticleFactory
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.PendingParticleFactory
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.particle.SpriteBillboardParticle
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.LightType

class AttackSparkParticle(clientWorld: ClientWorld, x: Double, y: Double, z: Double, velocityX: Double, velocityY: Double, velocityZ: Double) :
    SpriteBillboardParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ) {
    init {
        alpha = 0.7f
        maxAge += 10
        velocityMultiplier = 0.9f + 0.05f * random.nextFloat()
        scale = 0.0625f + 0.0625f * random.nextFloat()
    }

    override fun getBrightness(tint: Float): Int {
        val blockPos = BlockPos.ofFloored(x, y, z)
        val chunk = ChunkPos(blockPos)
        return LightmapTextureManager.pack(15, if (world.isChunkLoaded(chunk.x, chunk.z)) world.getLightLevel(LightType.SKY, blockPos) else 0)
    }

    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_LIT

    companion object Factory : PendingParticleFactory<SimpleParticleType> by PendingParticleFactory(::AttackSparkParticle)
}