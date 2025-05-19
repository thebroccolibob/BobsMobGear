package io.github.thebroccolibob.bobsmobgear.mixin.client;

import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockLeakParticle.Landing.class)
public interface LandingBlockLeakParticleInvoker {
    @Invoker("<init>")
    static BlockLeakParticle.Landing newLanding(ClientWorld world, double x, double y, double z, Fluid fluid) {
        throw new AssertionError();
    }
}
