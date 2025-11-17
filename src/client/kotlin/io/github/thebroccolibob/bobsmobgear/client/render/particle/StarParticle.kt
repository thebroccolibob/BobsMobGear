package io.github.thebroccolibob.bobsmobgear.client.render.particle

import io.github.thebroccolibob.bobsmobgear.client.util.PendingParticleFactory
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.PendingParticleFactory
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.particle.SpriteBillboardParticle
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.math.MathHelper.TAU
import net.minecraft.util.math.MathHelper.cos
import kotlin.math.sin

class StarParticle(clientWorld: ClientWorld, val centerX: Double, y: Double, val centerZ: Double, val deltaX: Double, val deltaZ: Double) :
    SpriteBillboardParticle(clientWorld, centerX, y, centerZ) {

    private val thetaOffset = random.nextFloat() * TAU

    init {
        updatePos()
        prevPosX = x
        prevPosZ = z
        maxAge = 20
    }

    constructor(clientWorld: ClientWorld, x: Double, y: Double, z: Double, velocityX: Double, velocityY: Double, velocityZ: Double) :
        this(clientWorld, x, y, z, velocityX, velocityZ)

    fun updatePos(change: Int = 0) {
        val theta = age / 20f * TAU + thetaOffset + change
        setPos(centerX + deltaX * cos(theta), y, centerZ + deltaZ * sin(theta))
    }

    override fun getSize(tickDelta: Float): Float = 0.125f

    override fun tick() {
        super.tick()
        if (!isAlive) return
        updatePos()
    }

    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_OPAQUE

    companion object : PendingParticleFactory<SimpleParticleType> by PendingParticleFactory(::StarParticle)
}