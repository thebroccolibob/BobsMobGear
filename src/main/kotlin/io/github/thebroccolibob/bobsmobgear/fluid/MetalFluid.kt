package io.github.thebroccolibob.bobsmobgear.fluid

import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes

open class MetalFluid(
    val tint: Int
) : VirtualFluid() {
    override fun getParticle(): ParticleEffect? {
        return ParticleTypes.DRIPPING_LAVA
    }
}