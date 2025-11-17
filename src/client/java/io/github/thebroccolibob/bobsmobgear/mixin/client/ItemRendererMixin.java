package io.github.thebroccolibob.bobsmobgear.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearComponents;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static java.lang.Math.max;
import static net.minecraft.client.render.LightmapTextureManager.*;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @ModifyVariable(
            method = "renderBakedItemModel",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private int heatedLight(int value, @Local(argsOnly = true) ItemStack stack) {
        if (!stack.contains(BobsMobGearComponents.HEATED)) return value;
        var block = getBlockLightCoordinates(value);
        var sky = getSkyLightCoordinates(value);
        return pack(max(block, 12), sky);
    }
}
