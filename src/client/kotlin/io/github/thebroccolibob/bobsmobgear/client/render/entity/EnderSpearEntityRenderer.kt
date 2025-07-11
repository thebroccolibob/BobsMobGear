package io.github.thebroccolibob.bobsmobgear.client.render.entity

import io.github.thebroccolibob.bobsmobgear.client.util.invoke
import io.github.thebroccolibob.bobsmobgear.entity.AbstractEnderSpearEntity
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis

class EnderSpearEntityRenderer(ctx: EntityRendererFactory.Context) : EntityRenderer<AbstractEnderSpearEntity>(ctx) {
    private val itemRenderer = ctx.itemRenderer

    override fun render(
        entity: AbstractEnderSpearEntity,
        yaw: Float,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider?,
        light: Int
    ) {
        matrices {
            translate(0f, 0.075f, 0f)
            multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getYaw(tickDelta)))
            multiply(RotationAxis.NEGATIVE_X.rotationDegrees(entity.getPitch(tickDelta) - 90))
            translate(0f, -1.2f, 0f)
            multiply(RotationAxis.POSITIVE_X.rotationDegrees(10f))
            itemRenderer.renderItem(entity.itemStack, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.world, 0)
        }
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light)
    }

    @Suppress("DEPRECATION")
    override fun getTexture(entity: AbstractEnderSpearEntity?): Identifier = SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE
}