package io.github.thebroccolibob.bobsmobgear.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.thebroccolibob.bobsmobgear.HeatedLogicKt;
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static java.lang.Math.max;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
	@WrapOperation(
			method = "renderBakedItemQuads",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;quad(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/model/BakedQuad;FFFFII)V")
	)
	private void init(VertexConsumer instance, MatrixStack.Entry matrixEntry, BakedQuad quad, float red, float green, float blue, float alpha, int light, int overlay, Operation<Void> original, @Local(argsOnly = true) ItemStack stack) {
        if (!stack.contains(BobsMobGearItems.HEATED)) {
			original.call(instance, matrixEntry, quad, red, green, blue, alpha, light, overlay);
			return;
		}

		var color = new Vector3f(red, green, blue).mul(HeatedLogicKt.HEATED_COLOR_MATRIX);

		// TODO move logic into HeatedLogic.kt?

		var block = LightmapTextureManager.getBlockLightCoordinates(light);
		var sky = LightmapTextureManager.getBlockLightCoordinates(light);
		var modifiedLight = LightmapTextureManager.pack(max(block, 12), sky);

		original.call(instance, matrixEntry, quad, color.x, color.y, color.z, alpha, modifiedLight, overlay);
    }
}