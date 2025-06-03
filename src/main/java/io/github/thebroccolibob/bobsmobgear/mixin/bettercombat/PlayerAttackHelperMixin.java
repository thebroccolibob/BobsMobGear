package io.github.thebroccolibob.bobsmobgear.mixin.bettercombat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItemTags;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerAttackHelper.class)
public class PlayerAttackHelperMixin {
    @ModifyReturnValue(
            method = "isDualWielding(Lnet/minecraft/entity/player/PlayerEntity;)Z",
            at = @At("RETURN")
    )
    private static boolean checkNonOffhandWeapons(boolean original, @Local(argsOnly = true) PlayerEntity player) {
        return (player.getMainHandStack().isIn(BobsMobGearItemTags.NON_OFFHAND_WEAPON)
                || !player.getOffHandStack().isIn(BobsMobGearItemTags.NON_OFFHAND_WEAPON)
        ) && original;
    }
}
