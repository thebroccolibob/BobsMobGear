package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.block.Block
import net.minecraft.data.client.*
import java.util.*

class ModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
        blockStateModelGenerator.registerSingleton(BobsMobGearBlocks.SWORD_TEMPLATE, TEMPLATE_FACTORY)
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
        itemModelGenerator.registerTemplate(BobsMobGearBlocks.SWORD_TEMPLATE)
    }

    companion object {
        val TEMPLATE_MODEL = Model(Optional.of(BobsMobGear.id("block/template")), Optional.empty(), TextureKey.TOP)

        val TEMPLATE_FACTORY: TexturedModel.Factory = TexturedModel.makeFactory({ TextureMap.of(TextureKey.TOP, ModelIds.getBlockModelId(it)) }, TEMPLATE_MODEL)

        fun ItemModelGenerator.registerTemplate(template: Block) {
            Models.GENERATED.upload(ModelIds.getItemModelId(template.asItem()), TextureMap.layer0(template), writer)
        }
    }
}
