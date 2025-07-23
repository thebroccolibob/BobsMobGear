package io.github.thebroccolibob.bobsmobgear.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.thebroccolibob.bobsmobgear.HeatedLogicKt;
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearComponents;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper.Argb;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemColors.class)
public class ItemColorsMixin {
	@ModifyReturnValue(
			method = "getColor",
			at = @At("RETURN")
	)
	private int heatedTint(int original, @Local(argsOnly = true) ItemStack stack) {
        if (original != -1 || !stack.contains(BobsMobGearComponents.HEATED)) return original;

		var a = Argb.getAlpha(original);
		var r = Argb.getRed(original);
		var g = Argb.getGreen(original);
		var b = Argb.getBlue(original);

		var color = new Vector3f(r / 255f, g / 255f, b / 255f).mul(HeatedLogicKt.HEATED_COLOR_MATRIX);

		// TODO move logic into HeatedLogic.kt?

		return Argb.getArgb(a, (int) (255 * color.x), (int) (255 * color.y), (int) (255 * color.z));
	}
}