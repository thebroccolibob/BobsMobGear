package io.github.thebroccolibob.bobsmobgear.mixin.bettercombat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItemTags;
import net.bettercombat.logic.WeaponAttributesFallback;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WeaponAttributesFallback.class, remap = false)
public class WeaponAttributesFallbackMixin {
    @SuppressWarnings({"deprecation", "LocalMayBeArgsOnly"})
    @ModifyExpressionValue(
            method = "initialize",
            at = @At(value = "INVOKE", target = "Lnet/bettercombat/utils/PatternMatching;matches(Ljava/lang/String;Ljava/lang/String;)Z")
    )
    private static boolean ignoreTag(boolean original, @Local Item item) {
        return original || item.getRegistryEntry().isIn(BobsMobGearItemTags.NOT_WEAPON);
    }
}
