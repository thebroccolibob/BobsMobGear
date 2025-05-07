package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.recipe.ForgingRecipe
import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearFluids
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.set
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.advancement.AdvancementRequirements
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion
import net.minecraft.data.server.recipe.RecipeExporter
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.ItemTags
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import java.util.concurrent.CompletableFuture

class RecipeGenerator(output: FabricDataOutput, registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>) :
    FabricRecipeProvider(output, registriesFuture) {

    override fun generate(exporter: RecipeExporter) {
        acceptTemplateRecipe(
            TemplateRecipe(
                BobsMobGearBlocks.SWORD_TEMPLATE,
                null,
                Ingredient.ofItems(Items.WOODEN_SWORD),
                ingredientList(
                    Ingredient.fromTag(ItemTags.STONE_TOOL_MATERIALS),
                    Ingredient.fromTag(ItemTags.STONE_TOOL_MATERIALS),
                    Ingredient.ofItems(Items.STRING)
                ),
                FluidVariant.blank(),
                0,
                false,
                ItemStack(Items.STONE_SWORD)
            ),
            exporter
        )

        acceptTemplateRecipe(
            TemplateRecipe(
                BobsMobGearBlocks.SWORD_TEMPLATE,
                BobsMobGearBlocks.SMITHING_SURFACE,
                Ingredient.ofItems(Items.STONE_SWORD),
                DefaultedList.of(),
                FluidVariant.of(BobsMobGearFluids.IRON),
                2 * FluidConstants.INGOT,
                true,
                ItemStack(Items.IRON_SWORD, 1).apply {
                    set(BobsMobGearItems.HEATED)
                }
            ),
            exporter
        )

        acceptTemplateRecipe(
            TemplateRecipe(
                BobsMobGearBlocks.SWORD_TEMPLATE,
                BobsMobGearBlocks.SMITHING_SURFACE,
                Ingredient.ofItems(Items.IRON_SWORD),
                DefaultedList.of(),
                FluidVariant.of(BobsMobGearFluids.DIAMOND),
                2 * FluidConstants.INGOT,
                true,
                ItemStack(Items.DIAMOND_SWORD).apply {
                    set(BobsMobGearItems.HEATED)
                }
            ),
            exporter
        )

        acceptTemplateRecipe(
            TemplateRecipe(
                BobsMobGearBlocks.SWORD_TEMPLATE,
                BobsMobGearBlocks.SMITHING_SURFACE,
                Ingredient.ofItems(Items.DIAMOND_SWORD),
                DefaultedList.of(),
                FluidVariant.of(BobsMobGearFluids.NETHERITE),
                1 * FluidConstants.INGOT,
                true,
                ItemStack(Items.NETHERITE_SWORD).apply {
                    set(BobsMobGearItems.HEATED)
                }
            ),
            exporter
        )

        acceptForgingRecipe(
            ForgingRecipe(
                ingredientList(Ingredient.fromTag(BobsMobGearItems.FORGES_IRON_INGOT)),
                FluidVariant.of(BobsMobGearFluids.IRON),
                FluidConstants.INGOT,
                200,
            ),
            exporter
        )

        acceptForgingRecipe(
            ForgingRecipe(
                ingredientList(Ingredient.fromTag(BobsMobGearItems.FORGES_DIAMOND)),
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
                    Ingredient.fromTag(BobsMobGearItems.FORGES_GOLD_INGOT),
                    Ingredient.fromTag(BobsMobGearItems.FORGES_NETHERITE_SCRAP)
                ),
                FluidVariant.of(BobsMobGearFluids.NETHERITE),
                FluidConstants.INGOT / 4,
                200
            ),
            exporter
        )

        acceptForgingRecipe(
            ForgingRecipe(
                ingredientList(Ingredient.fromTag(BobsMobGearItems.FORGES_NETHERITE_INGOT)),
                FluidVariant.of(BobsMobGearFluids.NETHERITE),
                FluidConstants.INGOT,
                200,
            ),
            exporter
        )
    }

    companion object {
        private fun acceptTemplateRecipe(recipeId: Identifier, recipe: TemplateRecipe, exporter: RecipeExporter) {
            exporter.accept(
                recipeId,
                recipe,
                exporter.advancementBuilder.apply {
                    criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                    criterion(hasItem(recipe.template.asItem()), conditionsFromItem(recipe.template.asItem()))
                    rewards(AdvancementRewards.Builder.recipe(recipeId))
                    criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
                }.build(recipeId.withPrefixedPath("recipes/"))
            )
        }

        private fun acceptTemplateRecipe(name: String, recipe: TemplateRecipe, exporter: RecipeExporter) {
            acceptTemplateRecipe(BobsMobGear.id(name).withPrefixedPath("template/"), recipe, exporter)
        }

        private fun acceptTemplateRecipe(recipe: TemplateRecipe, exporter: RecipeExporter) {
            acceptTemplateRecipe(Registries.ITEM.getId(recipe.result.item).path, recipe, exporter)
        }

        private fun acceptForgingRecipe(recipeId: Identifier, recipe: ForgingRecipe, exporter: RecipeExporter) {
            exporter.accept(recipeId, recipe, null)
        }

        private fun acceptForgingRecipe(recipe: ForgingRecipe, exporter: RecipeExporter) {
            acceptForgingRecipe(Registries.FLUID.getId(recipe.result.fluid).withPrefixedPath("forging/"), recipe, exporter)
        }

        private fun ingredientList(vararg ingredients: Ingredient): DefaultedList<Ingredient> =
            DefaultedList.copyOf(Ingredient.EMPTY, *ingredients)
    }
}
