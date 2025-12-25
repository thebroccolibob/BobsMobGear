package io.github.thebroccolibob.bobsmobgear.client.render.item

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.data.client.ModelIds
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.RotationAxis
import io.github.thebroccolibob.bobsmobgear.client.util.invoke
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearComponents.TONGS_HELD_ITEM
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItemTags
import io.github.thebroccolibob.bobsmobgear.util.isIn

class TongsItemRenderer private constructor(tongs: Item) : DynamicItemRenderer {
    private val tongsModel = ModelIds.getItemSubModelId(tongs, "_model")

    init {
        ModelLoadingPlugin.register { ctx ->
            ctx.addModels(tongsModel)
        }
    }

    override fun render(
        stack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val gui = mode == ModelTransformationMode.GUI

        matrices {
            translate(0.5, 0.5, 0.5)

            itemRenderer.renderItem(
                stack,
                ModelTransformationMode.NONE,
                mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND,
                matrices,
                vertexConsumers,
                light,
                overlay,
                itemRenderer.models.modelManager.getModel(tongsModel)
            )

            stack[TONGS_HELD_ITEM]?.takeUnless { it.isEmpty }?.stack?.let {

                if (gui) {
                    translate(-0.25, 0.25, 1 / 16.0)
                    scale(0.5f, 0.5f, 0.5f)
                } else {
//                    translate(-0.25, 0.25, -0.25)
//                    multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f))
//                    multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45f))

                    translate(-0.25, 0.25, 0.0)
                    multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90f))
                    multiply(RotationAxis.POSITIVE_X.rotationDegrees(-45f))
                    if (it isIn BobsMobGearItemTags.TONG_HOLDABLE_WEAPONS) {
                        multiply(RotationAxis.POSITIVE_Z.rotationDegrees(135f))
                        scale(-1f, 1f, -1f)
                    } else
                        scale(-0.5f, 0.5f, -0.5f)
                }

                itemRenderer.renderItem(
                    it,
//                    if (gui) ModelTransformationMode.NONE else ModelTransformationMode.THIRD_PERSON_RIGHT_HAND,
                    if (gui) ModelTransformationMode.GUI else ModelTransformationMode.FIXED,
                    light,
                    overlay,
                    matrices,
                    vertexConsumers,
                    MinecraftClient.getInstance().world,
                    0
                )
            }
        }
    }

    companion object {
        private val itemRenderer by lazy {
            MinecraftClient.getInstance().itemRenderer
        }

        @JvmStatic
        fun register(tongs: Item) = TongsItemRenderer(tongs).also {
            BuiltinItemRendererRegistry.INSTANCE.register(tongs, it)
        }
    }
}