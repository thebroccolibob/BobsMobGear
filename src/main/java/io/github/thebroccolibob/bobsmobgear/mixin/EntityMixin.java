package io.github.thebroccolibob.bobsmobgear.mixin;

import io.github.thebroccolibob.bobsmobgear.PlayerVibrationHandler;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract World getWorld();

    @Inject(
            method = "updateEventHandler",
            at = @At("HEAD")
    )
    private void callHolders(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback, CallbackInfo ci) {
        if (this instanceof PlayerVibrationHandler.Holder holder && getWorld() instanceof ServerWorld serverWorld)
            callback.accept(holder.bobsmobgear$getVibrationHandler().getGameEventListener(), serverWorld);
    }
}
