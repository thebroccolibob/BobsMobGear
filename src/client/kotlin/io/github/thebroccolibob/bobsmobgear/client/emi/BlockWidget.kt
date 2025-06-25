package io.github.thebroccolibob.bobsmobgear.client.emi

import dev.emi.emi.api.widget.Bounds
import dev.emi.emi.api.widget.Widget
import io.github.thebroccolibob.bobsmobgear.client.util.invoke
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.BlockRenderManager
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis

open class BlockWidget(private val state: BlockState, val x: Int, val y: Int, private val width: Int) : Widget() {
    override fun render(draw: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        draw.matrices {
            translate((x + width / 2).toFloat(), (y + width / 2).toFloat(), 0f)
            val scale = width * 0.6f
            scale(scale, -scale, scale)
            translate(0f, 0f, 1f)
            multiply(RotationAxis.POSITIVE_X.rotationDegrees(35f))
            multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45f))
            translate(-0.5f, -0.5f, -0.5f)
            renderBlock(draw.matrices, draw.vertexConsumers)
        }
//        draw.drawTexture(Identifier.of("none"), x, y, 0, 0, width, width) debug
    }

    open fun renderBlock(matrices: MatrixStack, vertexConsumers: VertexConsumerProvider) {
        blockRenderManger.renderBlockAsEntity(state, matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV)
    }

    override fun getBounds(): Bounds {
        return Bounds(x, y, width, width)
    }

    companion object {
        private val blockRenderManger: BlockRenderManager = MinecraftClient.getInstance().blockRenderManager
        private val blockEntityRenderDispatcher: BlockEntityRenderDispatcher = MinecraftClient.getInstance().blockEntityRenderDispatcher
    }
}