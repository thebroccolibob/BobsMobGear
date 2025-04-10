package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.data.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.advancement.AdvancementRequirements
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion
import net.minecraft.block.Blocks
import net.minecraft.data.server.recipe.RecipeExporter
import net.minecraft.fluid.Fluids
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import java.util.concurrent.CompletableFuture

class RecipeGenerator(output: FabricDataOutput, registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>) :
    FabricRecipeProvider(output, registriesFuture) {

    override fun generate(exporter: RecipeExporter) {
        acceptTemplateRecipe(
            "test",
            TemplateRecipe(
                Blocks.STONE,
                BobsMobGearBlocks.SMITHING_SURFACE,
                Ingredient.ofStacks(Items.STONE_SWORD.defaultStack),
                DefaultedList.copyOf(
                    Ingredient.EMPTY,
                    Ingredient.ofStacks(Items.COBBLESTONE.defaultStack),
                    Ingredient.ofStacks(Items.STRING.defaultStack)
                ),
                FluidVariant.of(Fluids.LAVA),
                3,
                false,
                Items.IRON_SWORD.defaultStack,
            ),
            exporter
        )

        acceptTemplateRecipe(
            "test2",
            TemplateRecipe(
                Blocks.IRON_BLOCK,
                null,
                Ingredient.ofStacks(Items.STICK.defaultStack),
                DefaultedList.of(),
                FluidVariant.blank(),
                0,
                false,
                Items.DIAMOND_SWORD.defaultStack
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
    }
}