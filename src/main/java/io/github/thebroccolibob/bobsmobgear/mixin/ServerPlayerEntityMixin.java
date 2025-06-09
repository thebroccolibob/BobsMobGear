package io.github.thebroccolibob.bobsmobgear.mixin;

import com.mojang.authlib.GameProfile;
import io.github.thebroccolibob.bobsmobgear.PlayerVibrationHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements PlayerVibrationHandler.Holder {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Unique
    private final PlayerVibrationHandler bobsmobgear$vibrationHandler = new PlayerVibrationHandler(this);

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void tickVibration(CallbackInfo ci) {
        bobsmobgear$vibrationHandler.tick();
    }

    @Override
    public @NotNull PlayerVibrationHandler bobsmobgear$getVibrationHandler() {
        return bobsmobgear$vibrationHandler;
    }
}
