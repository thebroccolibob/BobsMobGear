package io.github.thebroccolibob.bobsmobgear.mixin.client.bettercombat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.thebroccolibob.bobsmobgear.client.duck.TriggersAttack;
import io.github.thebroccolibob.bobsmobgear.item.UsingAttackable;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @see net.bettercombat.mixin.client.MinecraftClientInject
 */
@Mixin(value = MinecraftClient.class, priority = 1500)
public class MinecraftClientInjectMixin implements TriggersAttack {
    @Shadow @Nullable public ClientPlayerEntity player;
    /**
     * @see net.bettercombat.mixin.client.MinecraftClientInject#startUpswing(WeaponAttributes)
     */
    @Shadow
    private void startUpswing(WeaponAttributes attributes) {
        throw new AssertionError();
    }

    /**
     * @see net.bettercombat.mixin.client.MinecraftClientInject#startUpswing(WeaponAttributes)
     */
    @ModifyExpressionValue(
            method = "startUpswing",
            remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z")
    )
    private boolean attackWhileUsing(boolean original) {
        if (!original) return false;
        @SuppressWarnings("DataFlowIssue")
        var activeItem = player.getActiveItem();
        return !(activeItem.getItem() instanceof UsingAttackable usingAttackable && usingAttackable.canAttackWhileUsing(activeItem, player));
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void bobsmobgear$startAttack() {
        startUpswing(WeaponRegistry.getAttributes(player.getMainHandStack()));
    }

//    @WrapWithCondition(
//            method = "startUpswing",
//            remap = false,
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;stopUsingItem()V")
//    )
//    private boolean preventStopUsingItem(ClientPlayerEntity instance) {
//        return !(instance.getActiveItem().getItem() instanceof UsingAttackable);
//    }
}
