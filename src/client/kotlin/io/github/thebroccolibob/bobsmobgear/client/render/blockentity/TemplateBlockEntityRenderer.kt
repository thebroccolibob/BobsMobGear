package io.github.thebroccolibob.bobsmobgear.client.render.blockentity

import io.github.thebroccolibob.bobsmobgear.block.entity.TemplateBlockEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis

@Environment(EnvType.CLIENT)
class TemplateBlockEntityRenderer(ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<TemplateBlockEntity> {
    private val itemRenderer = ctx.itemRenderer
    private val renderManager = ctx.renderManager

    override fun render(
        entity: TemplateBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        matrices.push()
        matrices.translate(0.5f, TEMPLATE_HEIGHT, 0.5f)
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f))
        matrices.scale(BASE_SCALE, BASE_SCALE, BASE_SCALE)

        matrices.push()
        matrices.translate(0f, 0f, -0.5f / 16)
        itemRenderer.renderItem(entity.baseStack, ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, entity.world, 0)
        matrices.pop()

        entity.ingredientsInventory.filter { !it.isEmpty }.run {
            forEachIndexed { index, stack ->
                matrices.push()
                val xz = (1 - INGREDIENT_MARGIN) * (index + 1f) / (size + 1) + INGREDIENT_MARGIN - 0.5f
                matrices.translate(-xz, xz, -index * INGREDIENT_OFFSET)
                matrices.scale(INGREDIENT_SCALE, INGREDIENT_SCALE, INGREDIENT_SCALE)
                itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, entity.world, 0)
                matrices.pop()
            }
        }

        entity.fluidStorage.variant.takeUnless { it.isBlank }?.let {
//            TODO()
        }

        matrices.pop()
    }

    companion object {
        const val BASE_SCALE = 12 / 16f
        const val TEMPLATE_HEIGHT = 2 / 16f
        const val INGREDIENT_SCALE = 0.75f
        const val INGREDIENT_OFFSET = 0.01f;
        const val INGREDIENT_MARGIN = 0.3f;
    }
}
