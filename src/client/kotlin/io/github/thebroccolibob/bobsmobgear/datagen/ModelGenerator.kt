package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.block.ForgeBlock
import io.github.thebroccolibob.bobsmobgear.block.TemplateBlock
import io.github.thebroccolibob.bobsmobgear.client.util.BlockStateVariant
import io.github.thebroccolibob.bobsmobgear.client.util.variantYRotation
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.block.Block
import net.minecraft.data.client.*
import net.minecraft.data.client.TextureMap.getSubId
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import java.util.*

class ModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
    private fun BlockStateModelGenerator.registerForge(block: Block) {
        val textures = object {
            private fun of(suffix: String) = ModelIds.getBlockSubModelId(block, "_$suffix")

            val frontLeft = of("front_left")
            val frontRight = of("front_right")
            val frontLeftLit = of("front_left_lit")
            val frontRightLit = of("front_right_lit")
            val sideLeft = of("side_left")
            val sideRight = of("side_right")
            val topFrontLeft = of("top_front_left")
            val topFrontRight = of("top_front_right")
            val topBackLeft = of("top_back_left")
            val topBackRight = of("top_back_right")
            val bottomFrontLeft = of("bottom_front_left")
            val bottomFrontRight = of("bottom_front_right")
            val bottomBackLeft = of("bottom_back_left")
            val bottomBackRight = of("bottom_back_right")
            val inside = of("inside")
            val side = of("side")
        }

        fun cube(
            up: Identifier = textures.inside,
            down: Identifier = textures.inside,
            north: Identifier = textures.inside,
            south: Identifier = textures.inside,
            east: Identifier = textures.inside,
            west: Identifier = textures.inside,
        ) = TextureMap().apply {
            put(TextureKey.PARTICLE, textures.side)
            put(TextureKey.UP, up)
            put(TextureKey.DOWN, down)
            put(TextureKey.NORTH, north)
            put(TextureKey.SOUTH, south)
            put(TextureKey.EAST, east)
            put(TextureKey.WEST, west)
        }

        val models = ForgeBlock.Connection.entries.associateWith { connection ->
            listOf(true, false).associateWith { lit ->
                if (connection == ForgeBlock.Connection.NONE)
                    Models.ORIENTABLE.upload(block, if (lit) "_lit" else "", if (lit) TextureMap().apply {
                        put(TextureKey.SIDE, getSubId(block, "_side"))
                        put(TextureKey.FRONT, getSubId(block, "_front_lit"))
                        put(TextureKey.TOP, getSubId(block, "_top"))
                        put(TextureKey.BOTTOM, getSubId(block, "_bottom"));
                    } else TextureMap.sideFrontTopBottom(block), modelCollector)
                else
                    Models.CUBE.upload(
                        block, "_${connection.id}${if (lit) "_lit" else ""}", when (connection) {
                            ForgeBlock.Connection.FRONT_LEFT -> cube(
                                up = textures.topFrontLeft,
                                down = textures.bottomFrontLeft,
                                east = textures.sideRight,
                                north = if (lit) textures.frontLeftLit else textures.frontLeft
                            )

                            ForgeBlock.Connection.FRONT_RIGHT -> cube(
                                up = textures.topFrontRight,
                                down = textures.bottomFrontRight,
                                west = textures.sideLeft,
                                north = if (lit) textures.frontRightLit else textures.frontRight
                            )

                            ForgeBlock.Connection.BACK_LEFT -> cube(
                                up = textures.topBackLeft,
                                down = textures.bottomBackLeft,
                                east = textures.sideLeft,
                                south = textures.sideRight
                            )

                            ForgeBlock.Connection.BACK_RIGHT -> cube(
                                up = textures.topBackRight,
                                down = textures.bottomBackRight,
                                west = textures.sideRight,
                                south = textures.sideLeft
                            )

                            else -> throw AssertionError()
                        }, modelCollector
                    )
            }
        }

        registerStates(block,
            BlockStateVariantMap.create(ForgeBlock.CONNECTION, ForgeBlock.FACING, ForgeBlock.LIT).register { connection, facing, lit ->
                BlockStateVariant(
                    model = models[connection]!![lit]!!,
                    y = variantYRotation(facing),
                )
            }
        )
    }

    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
        blockStateModelGenerator.registerTemplate(BobsMobGearBlocks.SWORD_TEMPLATE)
        blockStateModelGenerator.registerForge(BobsMobGearBlocks.FORGE)
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
        itemModelGenerator.registerTemplate(BobsMobGearBlocks.SWORD_TEMPLATE)
        itemModelGenerator.registerGenerated(BobsMobGearItems.EMPTY_POT)
        itemModelGenerator.registerGenerated(BobsMobGearItems.IRON_POT)
        itemModelGenerator.registerGenerated(BobsMobGearItems.DIAMOND_POT)
        itemModelGenerator.registerGenerated(BobsMobGearItems.NETHERITE_POT)
    }

    companion object {
        val WOOD_TEMPLATE_MODEL = Model(Optional.of(BobsMobGear.id("block/template_wood")), Optional.empty(), TextureKey.TOP)
        val METAL_TEMPLATE_MODEL = Model(Optional.of(BobsMobGear.id("block/template_metal")), Optional.empty(), TextureKey.TOP)

        val WOOD_TEMPLATE_FACTORY: TexturedModel.Factory = TexturedModel.makeFactory({ TextureMap.of(TextureKey.TOP, ModelIds.getBlockSubModelId(it, "_wood")) }, WOOD_TEMPLATE_MODEL)
        val METAL_TEMPLATE_FACTORY: TexturedModel.Factory = TexturedModel.makeFactory({ TextureMap.of(TextureKey.TOP, ModelIds.getBlockSubModelId(it, "_metal")) }, METAL_TEMPLATE_MODEL)

        fun ItemModelGenerator.registerTemplate(template: Block) {
            Models.GENERATED.upload(ModelIds.getItemModelId(template.asItem()), TextureMap.layer0(TextureMap.getSubId(template, "_wood")), writer)
        }

        fun ItemModelGenerator.registerGenerated(item: Item) {
            register(item, Models.GENERATED)
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
