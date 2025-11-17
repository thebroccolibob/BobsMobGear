package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat
import io.github.thebroccolibob.bobsmobgear.datagen.util.JsonObject
import io.github.thebroccolibob.bobsmobgear.datagen.util.jsonArrayOf
import io.github.thebroccolibob.bobsmobgear.item.FluidPotItem
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearFluids
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.minecraft.data.DataOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.registry.Registries
import java.util.concurrent.CompletableFuture

// yeah I got lazy
class CreateRecipeGenerator(output: FabricDataOutput) : DataProvider {
    private val pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "recipe/create")

    private val emptyPot = Registries.ITEM.getId(BobsMobGearItems.EMPTY_POT).toString()

    private val createCondition = jsonArrayOf(
        JsonObject {
            addProperty("condition", "fabric:all_mods_loaded")
            add("values", jsonArrayOf(
                BobsMobGearCompat.CREATE
            ))
        }
    )

    override fun run(writer: DataWriter): CompletableFuture<*> = BobsMobGearItems.FILLED_POTS.flatMap { pot ->
        val itemId = Registries.ITEM.getId(pot).toString()
        val fluid = (pot as FluidPotItem).fluid
        val fluidId = Registries.FLUID.getId(fluid)

        listOf(
            DataProvider.writeToPath(
                writer,
                JsonObject {
                    add("fabric:load_conditions", createCondition)
                    addProperty("type", "create:filling")
                    add("ingredients", jsonArrayOf(
                        JsonObject {
                            addProperty("item", emptyPot)
                        },
                        JsonObject {
                            addProperty("amount", FluidConstants.INGOT / 81)
                            when (fluid) {
                                BobsMobGearFluids.IRON -> BobsMobGearFluids.MOLTEN_IRON_TAG
                                BobsMobGearFluids.DIAMOND -> BobsMobGearFluids.MOLTEN_DIAMOND_TAG
                                BobsMobGearFluids.NETHERITE -> BobsMobGearFluids.MOLTEN_NETHERITE_TAG
                                else -> null
                            }?.let {
                                addProperty("type", "fluid_tag")
                                addProperty("fluid_tag", it.id.toString())
                            } ?: run {
                                addProperty("type", "fluid_stack")
                                addProperty("fluid", fluidId.toString())
                            }
                        },
                    ))
                    add("results", jsonArrayOf(
                        JsonObject {
                            addProperty("id", itemId)
                        },
                    ))
                },
                pathResolver.resolveJson(BobsMobGear.id("filling/${fluidId.path}"))
            ),
            DataProvider.writeToPath(
                writer,
                JsonObject {
                    add("fabric:load_conditions", createCondition)
                    addProperty("type", "create:emptying")
                    add("ingredients", jsonArrayOf(
                        JsonObject {
                            addProperty("item", itemId)
                        },
                    ))
                    add("results", jsonArrayOf(
                        JsonObject {
                            addProperty("id", emptyPot)
                        },
                        JsonObject {
                            addProperty("amount", FluidConstants.INGOT / 81)
                            addProperty("id", fluidId.toString())
                        },
                    ))
                },
                pathResolver.resolveJson(BobsMobGear.id("emptying/${fluidId.path}"))
            ),
        )
    }.toTypedArray().let { CompletableFuture.allOf(*it) }

    override fun getName(): String = "Create Filling/Emptying Recipes"
}