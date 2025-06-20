package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock
import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock.Connection
import io.github.thebroccolibob.bobsmobgear.block.TemplateBlock
import io.github.thebroccolibob.bobsmobgear.client.util.BlockStateVariant
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.block.Block
import net.minecraft.data.client.*
import net.minecraft.data.client.BlockStateModelGenerator.createBooleanModelMap
import net.minecraft.data.client.BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import java.util.*

class ModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {

    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator): Unit = with(blockStateModelGenerator) {
        registerTemplate(BobsMobGearBlocks.EMPTY_TEMPLATE)
        registerTemplate(BobsMobGearBlocks.SWORD_TEMPLATE)
        registerTemplate(BobsMobGearBlocks.PICKAXE_TEMPLATE)
        registerTemplate(BobsMobGearBlocks.AXE_TEMPLATE)
        registerTemplate(BobsMobGearBlocks.SHOVEL_TEMPLATE)
        registerTemplate(BobsMobGearBlocks.HOE_TEMPLATE)
        registerForge(BobsMobGearBlocks.FORGE)
        registerForge(BobsMobGearBlocks.FORGE_HEATER, BobsMobGearBlocks.FORGE)
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerator): Unit = with(itemModelGenerator) {
        registerGenerated(BobsMobGearItems.EMPTY_POT)
        registerGenerated(BobsMobGearItems.IRON_POT)
        registerGenerated(BobsMobGearItems.DIAMOND_POT)
        registerGenerated(BobsMobGearItems.NETHERITE_POT)

        registerGenerated(BobsMobGearItems.WORN_HARDENED_FLESH)
        registerGenerated(BobsMobGearItems.WORN_STURDY_BONE)
        registerGenerated(BobsMobGearItems.WORN_SPIDER_FANG)
        registerGenerated(BobsMobGearItems.WORN_CREEPER_CORE)

        register(BobsMobGearItems.SMITHING_HAMMER, Models.HANDHELD)
        Models.GENERATED.upload(ModelIds.getItemSubModelId(BobsMobGearItems.SMITHING_TONGS, "_model"), TextureMap.layer0(BobsMobGearItems.SMITHING_TONGS), writer)
    }

    companion object {
        val WOOD_TEMPLATE_MODEL = Model(Optional.of(BobsMobGear.id("block/template_wood")), Optional.empty(), TextureKey.TOP)
        val METAL_TEMPLATE_MODEL = Model(Optional.of(BobsMobGear.id("block/template_metal")), Optional.empty(), TextureKey.TOP)

        val WOOD_TEMPLATE_FACTORY: TexturedModel.Factory = TexturedModel.makeFactory({ TextureMap.of(TextureKey.TOP, ModelIds.getBlockSubModelId(it, "_wood")) }, WOOD_TEMPLATE_MODEL)
        val METAL_TEMPLATE_FACTORY: TexturedModel.Factory = TexturedModel.makeFactory({ TextureMap.of(TextureKey.TOP, ModelIds.getBlockSubModelId(it, "_metal")) }, METAL_TEMPLATE_MODEL)

        val BUILTIN_ENTITY_MODEL = Model(Optional.of(Identifier.ofVanilla("builtin/entity")), Optional.empty())

        fun ItemModelGenerator.registerGenerated(item: Item) {
            register(item, Models.GENERATED)
        }

        fun BlockStateModelGenerator.registerStates(block: Block, variantMap: BlockStateVariantMap) {
            blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(variantMap))
        }

        fun BlockStateModelGenerator.registerTemplate(template: Block) {
            blockStateCollector.accept(VariantsBlockStateSupplier.create(template).apply {
                coordinate(createBooleanModelMap(TemplateBlock.METAL,
                    METAL_TEMPLATE_FACTORY.upload(template, "_metal", modelCollector),
                    WOOD_TEMPLATE_FACTORY.upload(template, "_wood", modelCollector)
                ))
                coordinate(createNorthDefaultHorizontalRotationStates())
            })
            Models.GENERATED.upload(ModelIds.getItemModelId(template.asItem()), TextureMap.layer0(TextureMap.getSubId(template, "_wood")), modelCollector)
        }

        fun BlockStateModelGenerator.registerForge(block: Block, bottomSides: Block = block) {
            val textures = object {
                private fun main(suffix: String) = ModelIds.getBlockSubModelId(block, "_$suffix")
                private fun bottomSides(suffix: String) = ModelIds.getBlockSubModelId(bottomSides, "_$suffix")

                val front = main("front")
                val frontLit = main("front_lit")
                val frontLeft = main("front_left")
                val frontRight = main("front_right")
                val frontLeftLit = main("front_left_lit")
                val frontRightLit = main("front_right_lit")

                val side = bottomSides("side")
                val sideLeft = bottomSides("side_left")
                val sideRight = bottomSides("side_right")

                val top = main("top")
                val topFrontLeft = main("top_front_left")
                val topFrontRight = main("top_front_right")
                val topBackLeft = main("top_back_left")
                val topBackRight = main("top_back_right")

                val bottom = bottomSides("bottom")
                val bottomFrontLeft = bottomSides("bottom_front_left")
                val bottomFrontRight = bottomSides("bottom_front_right")
                val bottomBackLeft = bottomSides("bottom_back_left")
                val bottomBackRight = bottomSides("bottom_back_right")

                val inside = bottomSides("inside")
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

            val models = Connection.entries.associateWith { connection ->
                listOf(true, false).associateWith { lit ->
                    if (connection == Connection.NONE)
                        Models.ORIENTABLE_WITH_BOTTOM.upload(block, if (lit) "_lit" else "", TextureMap().apply {
                            put(TextureKey.TOP, textures.top)
                            put(TextureKey.FRONT, if (lit) textures.frontLit else textures.front)
                            put(TextureKey.SIDE, textures.side)
                            put(TextureKey.BOTTOM, textures.bottom)
                        }, modelCollector)
                    else
                        Models.CUBE.upload(
                            block, "_${connection.id}${if (lit) "_lit" else ""}", when (connection) {
                                Connection.FRONT_LEFT -> cube(
                                    up = textures.topFrontLeft,
                                    down = textures.bottomFrontLeft,
                                    east = textures.sideRight,
                                    north = if (lit) textures.frontLeftLit else textures.frontLeft
                                )

                                Connection.FRONT_RIGHT -> cube(
                                    up = textures.topFrontRight,
                                    down = textures.bottomFrontRight,
                                    west = textures.sideLeft,
                                    north = if (lit) textures.frontRightLit else textures.frontRight
                                )

                                Connection.BACK_LEFT -> cube(
                                    up = textures.topBackLeft,
                                    down = textures.bottomBackLeft,
                                    east = textures.sideLeft,
                                    south = textures.sideRight
                                )

                                Connection.BACK_RIGHT -> cube(
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

            blockStateCollector.accept(VariantsBlockStateSupplier.create(block)
                .coordinate(BlockStateVariantMap.create(AbstractForgeBlock.CONNECTION, AbstractForgeBlock.LIT).register { connection, lit ->
                    BlockStateVariant(
                        model = models[connection]!![lit]!!,
                    )
                })
                .coordinate(createNorthDefaultHorizontalRotationStates()))
        }
    }
}
