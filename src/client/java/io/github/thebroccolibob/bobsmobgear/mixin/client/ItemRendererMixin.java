package io.github.thebroccolibob.bobsmobgear.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.thebroccolibob.bobsmobgear.HeatedLogicKt;
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static java.lang.Math.max;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
	@ModifyArgs(
			method = "renderBakedItemQuads",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;quad(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/model/BakedQuad;FFFFII)V")
	)
	private void init(Args args, @Local(argsOnly = true) ItemStack stack) {
        if (!stack.contains(BobsMobGearItems.HEATED)) return;

		var color = new Vector3f(args.get(2), args.get(3), args.get(4)).mul(HeatedLogicKt.HEATED_COLOR_MATRIX);

		// TODO move logic into HeatedLogic.kt?
        args.set(2, color.x);
        args.set(3, color.y);
        args.set(4, color.z);

		int light = args.get(6);
		var block = LightmapTextureManager.getBlockLightCoordinates(light);
		var sky = LightmapTextureManager.getBlockLightCoordinates(light);
		args.set(6, LightmapTextureManager.pack(max(block, 12), sky));
    }
}