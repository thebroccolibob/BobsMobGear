package io.github.thebroccolibob.bobsmobgear.fluid

import net.minecraft.fluid.Fluids
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes

open class MetalFluid(
    val tint: Int
) : VirtualFluid() {
    init {
        defaultState = Fluids.LAVA.defaultState
    }

    override fun getParticle(): ParticleEffect? {
        return ParticleTypes.DRIPPING_LAVA
    }
}