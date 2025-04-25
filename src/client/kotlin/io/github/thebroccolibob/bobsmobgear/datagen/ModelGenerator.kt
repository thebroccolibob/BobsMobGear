package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.block.TemplateBlock
import io.github.thebroccolibob.bobsmobgear.client.util.BlockStateVariant
import io.github.thebroccolibob.bobsmobgear.client.util.variantYRotation
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.block.Block
import net.minecraft.data.client.*
import java.util.*

class ModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
        blockStateModelGenerator.registerTemplate(BobsMobGearBlocks.SWORD_TEMPLATE)
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
        itemModelGenerator.registerTemplate(BobsMobGearBlocks.SWORD_TEMPLATE)
    }

    companion object {
        val WOOD_TEMPLATE_MODEL = Model(Optional.of(BobsMobGear.id("block/template_wood")), Optional.empty(), TextureKey.TOP)
        val METAL_TEMPLATE_MODEL = Model(Optional.of(BobsMobGear.id("block/template_metal")), Optional.empty(), TextureKey.TOP)

        val WOOD_TEMPLATE_FACTORY: TexturedModel.Factory = TexturedModel.makeFactory({ TextureMap.of(TextureKey.TOP, ModelIds.getBlockSubModelId(it, "_wood")) }, WOOD_TEMPLATE_MODEL)
        val METAL_TEMPLATE_FACTORY: TexturedModel.Factory = TexturedModel.makeFactory({ TextureMap.of(TextureKey.TOP, ModelIds.getBlockSubModelId(it, "_metal")) }, METAL_TEMPLATE_MODEL)

        fun ItemModelGenerator.registerTemplate(template: Block) {
            Models.GENERATED.upload(ModelIds.getItemModelId(template.asItem()), TextureMap.layer0(TextureMap.getSubId(template, "_wood")), writer)
        }

        fun BlockStateModelGenerator.registerStates(block: Block, variantMap: BlockStateVariantMap) {
            blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(variantMap))
        }

        fun BlockStateModelGenerator.registerTemplate(template: Block) {
            val woodModel = WOOD_TEMPLATE_FACTORY.upload(template, "_wood", modelCollector)
            val metalModel = METAL_TEMPLATE_FACTORY.upload(template, "_metal", modelCollector)
            registerStates(template,
                BlockStateVariantMap.create(TemplateBlock.METAL, TemplateBlock.FACING).register { metal, facing ->
                    BlockStateVariant(
                        model = if (metal) metalModel else woodModel,
                        y = variantYRotation(facing),
                    )
                }
            )
        }
    }
}
