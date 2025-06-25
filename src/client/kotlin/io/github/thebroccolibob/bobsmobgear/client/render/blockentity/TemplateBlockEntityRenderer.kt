package io.github.thebroccolibob.bobsmobgear.client.render.blockentity

import io.github.thebroccolibob.bobsmobgear.block.TemplateBlock
import io.github.thebroccolibob.bobsmobgear.block.entity.TemplateBlockEntity
import io.github.thebroccolibob.bobsmobgear.client.util.invoke
import io.github.thebroccolibob.bobsmobgear.fluid.VirtualFluid
import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipeInput
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper.lerp
import net.minecraft.util.math.RotationAxis
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.roundToInt

@Environment(EnvType.CLIENT)
class TemplateBlockEntityRenderer(ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<TemplateBlockEntity> {
    private val itemRenderer = ctx.itemRenderer

    override fun render(
        entity: TemplateBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        renderRecipe(
            entity.getRecipeInput(),
            entity.world,
            matrices,
            vertexConsumers,
            itemRenderer,
            entity.cachedState.get(TemplateBlock.FACING),
            entity.fluidStorage.capacity,
            entity.pos,
            light,
            overlay
        )
    }


    companion object {
        const val MARGIN = 2 / 16f
        const val FLUID_STEPS = 12
        const val OFFSET = 1 / 256f
        const val BASE_SCALE = 1 - 2 * MARGIN
        const val TEMPLATE_WIDTH = 2 / 16f
        const val INGREDIENT_SCALE = 0.75f
        const val INGREDIENT_MARGIN = 0.3f

        fun renderRecipe(
            recipeInput: TemplateRecipeInput,
            world: World?,
            matrices: MatrixStack,
            vertexConsumers: VertexConsumerProvider,
            itemRenderer: ItemRenderer,
            direction: Direction = Direction.NORTH,
            fluidCapacity: Long = recipeInput.fluidAmount ?: 0,
            pos: BlockPos? = null,
            light: Int = LightmapTextureManager.MAX_LIGHT_COORDINATE,
            overlay: Int = OverlayTexture.DEFAULT_UV
        ) {
            matrices {
                translate(0.5f, TEMPLATE_WIDTH, 0.5f)
                multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(direction.asRotation()))
                multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f))

                matrices {
                    scale(BASE_SCALE, BASE_SCALE, BASE_SCALE)

                    matrices {
                        translate(0f, 0f, -0.5f / 16 + 0.001f) // Prevents some sort of weird z-fighting issue
                        itemRenderer.renderItem(recipeInput.base, ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, world, 0)
                    }

                    recipeInput.ingredients?.filter { !it.isEmpty }?.run {
                        forEachIndexed { index, stack ->
                            matrices {
                                val xz = (1 - INGREDIENT_MARGIN) * (index + 1f) / (size + 1) + INGREDIENT_MARGIN - 0.5f
                                translate(-xz, xz, -index * OFFSET)
                                scale(INGREDIENT_SCALE, INGREDIENT_SCALE, INGREDIENT_SCALE)
                                itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, world, 0)
                            }
                        }
                    }
                }

                if (recipeInput.fluid != null && recipeInput.fluidAmount != null)
                    renderFluid(recipeInput.fluid!!, recipeInput.fluidAmount!!, fluidCapacity, world, pos, matrices, vertexConsumers, light)
            }
        }

        private fun renderFluid(
            fluidVariant: FluidVariant,
            fluidAmount: Long,
            fluidCapacity: Long,
            world: World?,
            pos: BlockPos?,
            matrices: MatrixStack,
            vertexConsumers: VertexConsumerProvider,
            light: Int,
        ) {
            val fluid = fluidVariant.takeUnless { it.isBlank }?.fluid ?: return
            val renderer = FluidRenderHandlerRegistry.INSTANCE.get(fluid) ?: return
            val sprite = renderer.getFluidSprites(world, pos, fluid.defaultState)?.get(0) ?: return
            val fluidProgress = (fluidAmount.toFloat() * FLUID_STEPS / fluidCapacity).roundToInt() / FLUID_STEPS.toFloat()

            matrices.translate(0f, 0f, -OFFSET)

            val vertexConsumer = vertexConsumers.getBuffer(RenderLayers.getFluidLayer(fluid.defaultState))
            val matrix = matrices.peek()
            val fluidColor = renderer.getFluidColor(world, pos, fluid.defaultState)
            val fluidLight = LightmapTextureManager.pack(
                max(LightmapTextureManager.getBlockLightCoordinates(light), (fluid as? VirtualFluid)?.lightLevel ?: fluid.defaultState.blockState.luminance),
                LightmapTextureManager.getSkyLightCoordinates(light)
            )

            fun vertex(x: Float, y: Float, u: Float, v: Float) {
                vertexConsumer
                    .vertex(matrix, x, y, 0f)
                    .color(fluidColor)
                    .texture(u, v)
                    .light(fluidLight)
                    .normal(matrix, 0f, 0f, -1f)
            }

            val radius = fluidProgress * (0.5f - MARGIN)

            val minU = lerp(0.5f - radius, sprite.minU, sprite.maxU)
            val maxU = lerp(0.5f + radius, sprite.minU, sprite.maxU)
            val minV = lerp(0.5f - radius, sprite.minV, sprite.maxV)
            val maxV = lerp(0.5f + radius, sprite.minV, sprite.maxV)

            vertex(-radius, -radius, maxU, maxV)
            vertex(-radius, radius, maxU, minV)
            vertex(radius, radius, minU, minV)
            vertex(radius, -radius, minU, maxV)
        }
    }

}
