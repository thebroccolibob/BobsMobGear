package io.github.thebroccolibob.bobsmobgear.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.data.server.recipe.RecipeExporter
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.collection.DefaultedList
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat
import io.github.thebroccolibob.bobsmobgear.datagen.util.*
import io.github.thebroccolibob.bobsmobgear.recipe.ForgingRecipe
import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.registry.*
import io.github.thebroccolibob.bobsmobgear.util.set
import java.util.concurrent.CompletableFuture
import vectorwing.farmersdelight.common.registry.ModItems as FarmersDelightItems

class RecipeGenerator(output: FabricDataOutput, private val registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>) :
    FabricRecipeProvider(output, registriesFuture) {

    override fun generate(exporter: RecipeExporter) {
        exporter.shapedRecipe(RecipeCategory.TOOLS, BobsMobGearItems.SMITHING_HAMMER) {
            pattern("#")
            pattern("%")
            input('#', ConventionalItemTags.IRON_INGOTS)
            input('%', Items.STICK)

            itemCriterion(Items.IRON_INGOT)
        }

        // TODO Tongs recipe

        exporter.shapedRecipe(RecipeCategory.TOOLS, BobsMobGearItems.EMPTY_TEMPLATE) {
            pattern("###")
            pattern("# #")
            pattern("###")
            input('#', Items.STICK)

            itemCriterion(Items.STICK)
        }

        // the recipe generator only runs once registriesFuture is resolved
        val smithingSurface = registriesFuture.get().getWrapperOrThrow(RegistryKeys.BLOCK).getOrThrow(BobsMobGearBlocks.SMITHING_SURFACE)

        for (toolType in TOOL_TYPES)
            toolType.register(exporter, smithingSurface, ::withConditions)

        acceptForgingRecipe(
            ForgingRecipe(
                ingredientList(Ingredient.fromTag(BobsMobGearItemTags.FORGES_IRON_INGOT)),
                FluidVariant.of(BobsMobGearFluids.IRON),
                FluidConstants.INGOT,
                200,
                weakHeat = true,
            ),
            exporter
        )

        acceptForgingRecipe(
            ForgingRecipe(
                ingredientList(Ingredient.fromTag(BobsMobGearItemTags.FORGES_DIAMOND)),
                FluidVariant.of(BobsMobGearFluids.DIAMOND),
                FluidConstants.INGOT,
                200,
            ),
            exporter
        )

        acceptForgingRecipe(
            BobsMobGear.id("forging/netherite_alloying"),
            ForgingRecipe(
                ingredientList(
                    Ingredient.fromTag(BobsMobGearItemTags.FORGES_GOLD_INGOT),
                    Ingredient.fromTag(BobsMobGearItemTags.FORGES_NETHERITE_SCRAP)
                ),
                FluidVariant.of(BobsMobGearFluids.NETHERITE),
                FluidConstants.INGOT / 4,
                200
            ),
            exporter
        )

        acceptForgingRecipe(
            ForgingRecipe(
                ingredientList(Ingredient.fromTag(BobsMobGearItemTags.FORGES_NETHERITE_INGOT)),
                FluidVariant.of(BobsMobGearFluids.NETHERITE),
                FluidConstants.INGOT,
                200,
            ),
            exporter
        )

        acceptForgingRecipe(
            ForgingRecipe(
                ingredientList(Ingredient.fromTag(BobsMobGearItemTags.FORGES_BLACK_STEEL_INGOT)),
                FluidVariant.of(BobsMobGearFluids.BLACK_STEEL),
                FluidConstants.INGOT,
                200,
            ),
            withConditions(exporter, ResourceConditions.allModsLoaded(BobsMobGearCompat.CATACLYSM))
        )

        acceptTemplateRecipe(
            BobsMobGear.id("template/hot_potato"),
            TemplateRecipe(
                BobsMobGearBlocks.EMPTY_TEMPLATE,
                smithingSurface,
                Ingredient.ofItems(Items.POTATO),
                DefaultedList.of(),
                FluidVariant.of(Fluids.LAVA),
                FluidConstants.BUCKET,
                true,
                Items.POTATO.defaultStack.apply {
                    set(BobsMobGearComponents.HEATED)
                }
            ),
            exporter,
        )

        if (BobsMobGearCompat.FARMERS_DELIGHT_INSTALLED)
            acceptTemplateRecipe(
                TemplateRecipe(
                    BobsMobGearBlocks.EMPTY_TEMPLATE,
                    smithingSurface,
                    Ingredient.ofItems(FarmersDelightItems.BACON.get()),
                    DefaultedList.of(),
                    FluidVariant.of(BobsMobGearFluids.NETHERITE),
                    FluidConstants.INGOT,
                    true,
                    BobsMobGearItems.UNLIMITED_BACON.defaultStack
                ),
                withConditions(exporter, ResourceConditions.allModsLoaded(BobsMobGearCompat.FARMERS_DELIGHT))
            )

        for ((fluid, ingot) in listOf(
            BobsMobGearFluids.IRON to Items.IRON_INGOT,
            BobsMobGearFluids.DIAMOND to Items.DIAMOND,
            BobsMobGearFluids.NETHERITE to Items.NETHERITE_INGOT,
        ))
            acceptTemplateRecipe(
                TemplateRecipe(
                    BobsMobGearBlocks.EMPTY_TEMPLATE,
                    smithingSurface,
                    Ingredient.EMPTY,
                    ingredientList(),
                    FluidVariant.of(fluid),
                    FluidConstants.INGOT,
                    false,
                    ItemStack(ingot).apply {
                        set(BobsMobGearComponents.HEATED)
                    },
                    delay = 40
                ),
                exporter
            )
    }

    companion object {
        val TOOL_TYPES = listOf<ToolType>(
        )

    }
}
