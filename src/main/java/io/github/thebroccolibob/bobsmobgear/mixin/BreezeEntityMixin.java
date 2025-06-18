package io.github.thebroccolibob.bobsmobgear.mixin;

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearEntities;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BreezeEntity.class)
public class BreezeEntityMixin {
    @Inject(
            method = "getProjectileDeflection",
            at = @At("HEAD"),
            cancellable = true
    )
    private void noDeflectWebshot(ProjectileEntity projectile, CallbackInfoReturnable<ProjectileDeflection> cir) {
        if (projectile.getType() == BobsMobGearEntities.WEB_SHOT)
            cir.setReturnValue(ProjectileDeflection.NONE);
    }
}
