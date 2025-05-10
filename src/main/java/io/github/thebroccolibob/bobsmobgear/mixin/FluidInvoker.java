package io.github.thebroccolibob.bobsmobgear.mixin;

import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Fluid.class)
public interface FluidInvoker {
    @Invoker
    ParticleEffect invokeGetParticle();
}
