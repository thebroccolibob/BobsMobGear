package io.github.thebroccolibob.bobsmobgear.mixin.bettercombat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.thebroccolibob.bobsmobgear.item.UsingAttackable;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerAttackHelper.class)
public class PlayerAttackHelperMixin {
    @ModifyReturnValue(
            method = "shouldAttackWithOffHand",
            at = @At("RETURN")
    )
    private static boolean attackWithUsedItem(boolean original, @Local(argsOnly = true) PlayerEntity player) {
        return original || (player.isUsingItem() && player.getActiveHand() == Hand.OFF_HAND && player.getActiveItem().getItem() instanceof UsingAttackable);
    }
}
