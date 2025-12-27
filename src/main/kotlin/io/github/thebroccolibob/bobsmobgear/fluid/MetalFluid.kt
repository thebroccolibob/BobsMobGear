package io.github.thebroccolibob.bobsmobgear.fluid

import net.minecraft.block.BlockState
import net.minecraft.fluid.FluidState
import net.minecraft.particle.ParticleEffect
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import java.util.*

open class MetalFluid(
    val tint: Int,
    private val particle: ParticleEffect,
) : VirtualFluid() {
    override val lightLevel get() = 15

    override fun toBlockState(state: FluidState?): BlockState = BobsMobGearBlocks.LIQUID_METAL.defaultState

    override fun getParticle(): ParticleEffect = particle

    override fun getBucketFillSound(): Optional<SoundEvent> = Optional.of(SoundEvents.ITEM_BUCKET_FILL_LAVA) // TODO custom sound event
}