package io.github.thebroccolibob.bobsmobgear.client.render.item

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.client.util.invoke
import io.github.thebroccolibob.bobsmobgear.item.WardenFistItem
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
import kotlin.math.PI
import kotlin.math.sin

object WardenFistItemRenderer : DynamicItemRenderer {
    val BASE_MODEL = BobsMobGear.id("item/warden_fist_base")
    val BASE_CHARGING_MODEL = BobsMobGear.id("item/warden_fist_base_charging")
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
            val client = MinecraftClient.getInstance()
            val itemRenderer = client.itemRenderer
            if (mode != ModelTransformationMode.FIRST_PERSON_LEFT_HAND && mode != ModelTransformationMode.FIRST_PERSON_RIGHT_HAND && mode != ModelTransformationMode.THIRD_PERSON_LEFT_HAND && mode != ModelTransformationMode.THIRD_PERSON_RIGHT_HAND) {
                itemRenderer.renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, itemRenderer.models.modelManager.getModel(GUI_MODEL))
                return pop()
            }
            val baseModel = itemRenderer.models.modelManager.getModel(if (client.player?.activeItem == stack) BASE_CHARGING_MODEL else BASE_MODEL)
            if (baseModel == null) return pop()

            val age = client.player!!.age + client.renderTickCounter.getTickDelta(false)
            val charge = stack.getOrDefault(BobsMobGearItems.SONIC_CHARGE, 0)
            val brightness = (16 * (charge / WardenFistItem.MAX_SONIC_CHARGE.toDouble()) * (0.5 * sin(age * PI / 50 * if (charge == WardenFistItem.MAX_SONIC_CHARGE) 2 else 1) + 0.5)).toInt().coerceAtMost(15)

            val leftHanded = mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND
            baseModel.transformation.getTransformation(mode).apply(leftHanded, matrices)
            itemRenderer.renderItem(stack, ModelTransformationMode.NONE, false, matrices, vertexConsumers, light, overlay, baseModel)
            itemRenderer.renderItem(stack, ModelTransformationMode.NONE, false, matrices, vertexConsumers, LightmapTextureManager.pack(brightness, 0), overlay, itemRenderer.models.modelManager.getModel(GLOW_MODEL))
        }
    }

    fun register() {
        ModelLoadingPlugin.register {
            it.addModels(BASE_MODEL, BASE_CHARGING_MODEL, GLOW_MODEL, GUI_MODEL)
        }
        BuiltinItemRendererRegistry.INSTANCE.register(BobsMobGearItems.WARDEN_FIST, this)
    }

}
