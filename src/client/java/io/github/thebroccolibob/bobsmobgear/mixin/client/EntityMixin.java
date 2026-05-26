package io.github.thebroccolibob.bobsmobgear.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;

import io.github.thebroccolibob.bobsmobgear.client.DetectedEntity;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract boolean equals(Object o);

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @ModifyReturnValue(
            method = "getTeamColorValue",
            at = @At("RETURN")
    )
    private int detectedColor(int original) {
        return this.equals(DetectedEntity.getEntity()) ? DetectedEntity.OUTLINE_COLOR : original;
    }
}
