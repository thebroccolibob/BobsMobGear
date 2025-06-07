package io.github.thebroccolibob.bobsmobgear.client.render.item

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.client.util.invoke
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack

object WardenFistItemRenderer : DynamicItemRenderer {
    val BASE_MODEL = BobsMobGear.id("item/warden_fist_base")
    val GLOW_MODEL = BobsMobGear.id("item/warden_fist_glow")
    val GUI_MODEL = BobsMobGear.id("item/warden_fist_gui")

    override fun render(
        stack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        matrices {
            translate(0.5, 0.5, 0.5)
            val itemRenderer = MinecraftClient.getInstance().itemRenderer
            if (mode != ModelTransformationMode.FIRST_PERSON_LEFT_HAND && mode != ModelTransformationMode.FIRST_PERSON_RIGHT_HAND && mode != ModelTransformationMode.THIRD_PERSON_LEFT_HAND && mode != ModelTransformationMode.THIRD_PERSON_RIGHT_HAND) {
                itemRenderer.renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, itemRenderer.models.modelManager.getModel(GUI_MODEL))
                pop()
                return
            }
            val leftHanded = mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND
//        val brightness = MinecraftClient.getInstance().renderTickCounter.getTickDelta(false)
            itemRenderer.renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, itemRenderer.models.modelManager.getModel(BASE_MODEL))
            itemRenderer.renderItem(stack, mode, leftHanded, matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay, itemRenderer.models.modelManager.getModel(GLOW_MODEL))
        }
    }

    fun register() {
        ModelLoadingPlugin.register {
            it.addModels(BASE_MODEL, GLOW_MODEL, GUI_MODEL)
        }
        BuiltinItemRendererRegistry.INSTANCE.register(BobsMobGearItems.WARDEN_FIST, this)
    }

}
