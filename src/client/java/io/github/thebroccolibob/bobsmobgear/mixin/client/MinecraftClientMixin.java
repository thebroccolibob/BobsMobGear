package io.github.thebroccolibob.bobsmobgear.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.thebroccolibob.bobsmobgear.item.UsingAttackable;
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItemTags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow protected abstract boolean doAttack();

    @Shadow @Nullable public ClientPlayerEntity player;

    @Unique
    private static final Hand[] bobsmobgear$HANDS_REVERSED = {Hand.OFF_HAND, Hand.MAIN_HAND};

    @ModifyExpressionValue(
            method = "handleInputEvents",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;attackKey:Lnet/minecraft/client/option/KeyBinding;", ordinal = 0)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 0)
    )
    private boolean attackWhileUsing(boolean original, @Local(ordinal = 0) LocalBooleanRef attacked) {
        if (!original) return false;
        //noinspection DataFlowIssue
        if (!player.isUsingItem()) return true;
        var stack = player.getActiveItem();
        if (!(stack.getItem() instanceof UsingAttackable usingAttackable)) return true;
        if (!usingAttackable.canAttackWhileUsing(stack, player)) return true;

        doAttack();
        attacked.set(true);
        return true;
    }

    @SuppressWarnings("DataFlowIssue")
    @ModifyExpressionValue(
            method = "doItemUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Hand;values()[Lnet/minecraft/util/Hand;")
    )
    private Hand[] prioritizeOffhand(Hand[] original) {
        var mainHandStack = player.getMainHandStack();
        return mainHandStack.isIn(BobsMobGearItemTags.LOWER_USE_PRIORITY) && !player.getOffHandStack().isOf(mainHandStack.getItem())
            ? bobsmobgear$HANDS_REVERSED : original;
    }
}
