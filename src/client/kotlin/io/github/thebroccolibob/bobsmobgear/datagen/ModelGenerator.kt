package io.github.thebroccolibob.bobsmobgear.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.block.Block
import net.minecraft.data.client.*
import net.minecraft.data.client.BlockStateModelGenerator.createBooleanModelMap
import net.minecraft.data.client.BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.BobsMobGearClient
import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock
import io.github.thebroccolibob.bobsmobgear.block.AbstractForgeBlock.Connection
import io.github.thebroccolibob.bobsmobgear.block.TemplateBlock
import io.github.thebroccolibob.bobsmobgear.client.util.*
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import vectorwing.farmersdelight.common.registry.ModItems as FarmersDelightItems

class ModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {

    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator): Unit = with(blockStateModelGenerator) {
        for (template in BobsMobGearBlocks.TEMPLATES)
            registerCutoutTemplate(template)
        registerForge(BobsMobGearBlocks.FORGE)
        registerForge(BobsMobGearBlocks.FORGE_HEATER, BobsMobGearBlocks.FORGE)
        registerSingleton(BobsMobGearBlocks.LIQUID_METAL, TexturedModel.PARTICLE)
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerator): Unit = with(itemModelGenerator) {
        registerGenerated(BobsMobGearItems.EMPTY_POT)
        registerGenerated(BobsMobGearItems.IRON_POT)
        registerGenerated(BobsMobGearItems.DIAMOND_POT)
        registerGenerated(BobsMobGearItems.NETHERITE_POT)
        registerGenerated(BobsMobGearItems.BLACK_STEEL_POT)

        registerGenerated(BobsMobGearItems.WORN_HARDENED_FLESH)
        registerGenerated(BobsMobGearItems.WORN_STURDY_BONE)
        registerGenerated(BobsMobGearItems.WORN_SPIDER_FANG)
        registerGenerated(BobsMobGearItems.WORN_CREEPER_CORE)
        registerGenerated(BobsMobGearItems.WORN_SEETHING_PEARL)
        registerGenerated(BobsMobGearItems.WORN_SEETHING_EYE)
        registerGenerated(BobsMobGearItems.SCULK_SYMBIOTE)

        register(BobsMobGearItems.IRON_BONE_HAMMER, Models.HANDHELD)
        register(BobsMobGearItems.IRON_SPIDER_DAGGER, Models.HANDHELD)
        registerSpear(BobsMobGearItems.IRON_ENDER_SPEAR)
        registerSpear(BobsMobGearItems.IRON_ENDER_EYE_SPEAR)
        register(BobsMobGearItems.WARDEN_FIST, "_gui", Models.GENERATED)
        register(BobsMobGearItems.IRON_BOOM_BATON, Models.HANDHELD)
        register(BobsMobGearItems.UNLIMITED_BACON, FarmersDelightItems.COOKED_BACON.get(), Models.GENERATED)

        register(BobsMobGearItems.SMITHING_HAMMER, SMITHING_HAMMER_MODEL)
        register(BobsMobGearItems.SMITHING_TONGS, SMITHING_TONGS_BUILTIN_MODEL)
        register(BobsMobGearItems.SMITHING_TONGS, Models.GENERATED, modelSuffix = "_model")
    }

    companion object {
        @JvmStatic val WOOD_TEMPLATE_MODEL = Model(BobsMobGear.id("block/template_wood"), TextureKey.TOP)
        @JvmStatic val METAL_TEMPLATE_MODEL = Model(BobsMobGear.id("block/template_metal"), TextureKey.TOP)
        @JvmStatic val CUTOUT_TEMPLATE_MODEL = Model(BobsMobGear.id("block/cutout_template"), TextureKey.TOP)
        @JvmStatic val CUTOUT_TEMPLATE_ITEM_MODEL = Model(BobsMobGear.id("item/cutout_template"), TextureKey.LAYER1)

        @JvmStatic val WOOD_TEMPLATE_FACTORY: TexturedModel.Factory = texturedModelFactory(WOOD_TEMPLATE_MODEL) { TextureMap.of(TextureKey.TOP, ModelIds.getBlockSubModelId(it, "_wood")) }
        @JvmStatic val METAL_TEMPLATE_FACTORY: TexturedModel.Factory = texturedModelFactory(METAL_TEMPLATE_MODEL) { TextureMap.of(TextureKey.TOP, ModelIds.getBlockSubModelId(it, "_metal")) }
        @JvmStatic val CUTOUT_TEMPLATE_FACTORY: TexturedModel.Factory = texturedModelFactory(CUTOUT_TEMPLATE_MODEL) { TextureMap.of(TextureKey.TOP, ModelIds.getBlockModelId(it)) }

        @JvmStatic val TEMPLATE_SPEAR_HELD = Model(BobsMobGear.id("item/template_spear_held"), TextureKey.LAYER0)
        @JvmStatic val TEMPLATE_SPEAR_THROWING = Model(BobsMobGear.id("item/template_spear_throwing"), TextureKey.LAYER0)

        @JvmStatic val SMITHING_HAMMER_MODEL = Model(BobsMobGear.id("item/template_smithing_hammer"), TextureKey.LAYER0)

        @JvmStatic val SMITHING_TONGS_BUILTIN_MODEL = Model(BobsMobGear.id("item/template_smithing_tongs"))

        @JvmStatic val IS_HELD: Pair<Identifier, Float> = Identifier.of("pommel", "is_held") to 1f

        fun ItemModelGenerator.registerGenerated(item: Item) {
            register(item, Models.GENERATED)
        }

        fun BlockStateModelGenerator.registerStates(block: Block, variantMap: BlockStateVariantMap) {
            blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(variantMap))
        }

        @JvmStatic
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

        @JvmStatic
        fun BlockStateModelGenerator.registerCutoutTemplate(template: Block) {
            registerNorthDefaultHorizontalRotated(template, CUTOUT_TEMPLATE_FACTORY)
            CUTOUT_TEMPLATE_ITEM_MODEL.upload(ModelIds.getItemModelId(template.asItem()), TextureMap.of(TextureKey.LAYER1, TextureMap.getId(template)), modelCollector)
        }

        @JvmStatic
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

            blockStateCollector.accept(VariantsBlockStateSupplier.create(block).apply {
                coordinate(BlockStateVariantMap.create(AbstractForgeBlock.CONNECTION, AbstractForgeBlock.LIT).register { connection, lit ->
                    BlockStateVariant(model =
                        if (connection == Connection.NONE)
                            Models.ORIENTABLE_WITH_BOTTOM.upload(
                                block,
                                if (lit) "_lit" else "",
                                TextureMap().apply {
                                    put(TextureKey.TOP, textures.top)
                                    put(TextureKey.FRONT, if (lit) textures.frontLit else textures.front)
                                    put(TextureKey.SIDE, textures.side)
                                    put(TextureKey.BOTTOM, textures.bottom)
                                },
                                modelCollector
                            )
                        else
                            Models.CUBE.upload(
                                block,
                                "_${connection.id}${if (lit) "_lit" else ""}",
                                when (connection) {
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
                    )
                })
                coordinate(createNorthDefaultHorizontalRotationStates())
            })
        }

        @JvmStatic
        fun ItemModelGenerator.registerSpear(item: Item) {
            register(item, Models.GENERATED,
                ModelOverride(register(item, TEMPLATE_SPEAR_HELD, modelSuffix = "_held"),
                    IS_HELD,
                ),
                ModelOverride(register(item, TEMPLATE_SPEAR_THROWING, modelSuffix = "_throwing"),
                    IS_HELD,
                    BobsMobGearClient.USING_PREDICATE to 1f
                ),
                textureSuffix = "_gui"
            )
        }
    }
}
