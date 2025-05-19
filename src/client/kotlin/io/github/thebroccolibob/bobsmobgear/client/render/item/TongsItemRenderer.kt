package io.github.thebroccolibob.bobsmobgear.client.render.item

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.client.util.invoke
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems.TONGS_HELD_ITEM
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.math.RotationAxis

object TongsItemRenderer : DynamicItemRenderer {
    private val TONGS_MODEL = BobsMobGear.id("item/smithing_tongs_model")

    private val itemRenderer by lazy {
        MinecraftClient.getInstance().itemRenderer
    }

    fun register() {
        ModelLoadingPlugin.register { ctx ->
            ctx.addModels(TONGS_MODEL)
        }
        BuiltinItemRendererRegistry.INSTANCE.register(BobsMobGearItems.SMITHING_TONGS, this)
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
                itemRenderer.models.modelManager.getModel(TONGS_MODEL)
            )

            stack[TONGS_HELD_ITEM]?.takeIf { !it.isEmpty }?.let {

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
                    multiply(RotationAxis.POSITIVE_Z.rotationDegrees(135f))
                    scale(-1f, 1f, -1f)
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
}