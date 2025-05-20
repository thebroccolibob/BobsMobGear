package io.github.thebroccolibob.bobsmobgear.fluid

import net.minecraft.particle.ParticleEffect
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import java.util.*

open class MetalFluid(
    val tint: Int,
    private val particle: ParticleEffect,
) : VirtualFluid() {
    override val lightLevel get() = 15

    override fun getParticle(): ParticleEffect = particle

    override fun getBucketFillSound(): Optional<SoundEvent> = Optional.of(SoundEvents.ITEM_BUCKET_FILL_LAVA) // TODO custom sound event
}