package io.github.thebroccolibob.bobsmobgear.client.emi

import io.github.thebroccolibob.bobsmobgear.client.render.blockentity.TemplateBlockEntityRenderer
import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipe
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack

class TemplateBlockWidget(recipe: TemplateRecipe, x: Int, y: Int, width: Int) : BlockWidget(recipe.template.defaultState, x, y, width) {
    private val input = recipe.getTypicalInput()

    override fun renderBlock(matrices: MatrixStack, vertexConsumers: VertexConsumerProvider) {
        super.renderBlock(matrices, vertexConsumers)
        TemplateBlockEntityRenderer.renderRecipe(input, null, matrices, vertexConsumers, MinecraftClient.getInstance().itemRenderer)
    }
}