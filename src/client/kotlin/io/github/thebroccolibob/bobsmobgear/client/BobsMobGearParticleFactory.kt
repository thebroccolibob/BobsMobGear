package io.github.thebroccolibob.bobsmobgear.client

import io.github.thebroccolibob.bobsmobgear.client.render.particle.SonicShockwaveParticle
import io.github.thebroccolibob.bobsmobgear.fluid.MetalFluid
import io.github.thebroccolibob.bobsmobgear.mixin.client.ContinuousFallingBlockLeakParticleInvoker
import io.github.thebroccolibob.bobsmobgear.mixin.client.DrippingBlockLeakParticleInvoker
import io.github.thebroccolibob.bobsmobgear.mixin.client.LandingBlockLeakParticleInvoker
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearFluids
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearParticles
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.PendingParticleFactory
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteBillboardParticle
import net.minecraft.client.world.ClientWorld
import net.minecraft.fluid.Fluid
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.math.ColorHelper

fun createDripFactory(fluid: MetalFluid, create: (ClientWorld, Double, Double, Double, Fluid) -> SpriteBillboardParticle?) =
    PendingParticleFactory { spriteProvider ->
        ParticleFactory<SimpleParticleType> { _, world, x, y, z, _, _, _ ->
            create(world, x, y, z, fluid)?.apply {
                setColor(
                    ColorHelper.Argb.getRed(fluid.tint) / 255f,
                    ColorHelper.Argb.getGreen(fluid.tint) / 255f,
                    ColorHelper.Argb.getBlue(fluid.tint) / 255f
                )
                setSprite(spriteProvider)
            }
        }
    }
fun createDripFactory(fluid: MetalFluid, next: ParticleEffect, create: (ClientWorld, Double, Double, Double, Fluid, ParticleEffect) -> SpriteBillboardParticle?) =
    PendingParticleFactory { spriteProvider ->
        ParticleFactory<SimpleParticleType> { _, world, x, y, z, _, _, _ ->
            create(world, x, y, z, fluid, next)?.apply {
                setColor(
                    ColorHelper.Argb.getRed(fluid.tint) / 255f,
                    ColorHelper.Argb.getGreen(fluid.tint) / 255f,
                    ColorHelper.Argb.getBlue(fluid.tint) / 255f
                )
                setSprite(spriteProvider)
            }
        }
    }

private fun registerDrips(dripParticles: BobsMobGearParticles.Drips, fluid: MetalFluid) {
    ParticleFactoryRegistry.getInstance().apply {
        register(dripParticles.dripping, createDripFactory(fluid, dripParticles.falling, DrippingBlockLeakParticleInvoker::newDripping))
        register(dripParticles.falling, createDripFactory(fluid, dripParticles.landing, ContinuousFallingBlockLeakParticleInvoker::newContinuousFalling))
        register(dripParticles.landing, createDripFactory(fluid, LandingBlockLeakParticleInvoker::newLanding))
    }
}

fun registerBobsMobGearParticleFactories() {
    registerDrips(BobsMobGearParticles.IRON_DRIPS, BobsMobGearFluids.IRON)
    registerDrips(BobsMobGearParticles.DIAMOND_DRIPS, BobsMobGearFluids.DIAMOND)
    registerDrips(BobsMobGearParticles.NETHERITE_DRIPS, BobsMobGearFluids.NETHERITE)
    ParticleFactoryRegistry.getInstance().apply {
        register(BobsMobGearParticles.SONIC_SHOCKWAVE, SonicShockwaveParticle)
    }
}
