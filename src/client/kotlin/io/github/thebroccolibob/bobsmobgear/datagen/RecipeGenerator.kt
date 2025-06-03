package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.recipe.ForgingRecipe
import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearFluids
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItemTags
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.set
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.advancement.AdvancementRequirements
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion
import net.minecraft.block.Block
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder
import net.minecraft.data.server.recipe.RecipeExporter
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.ItemTags
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import java.util.concurrent.CompletableFuture

class RecipeGenerator(output: FabricDataOutput, private val registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>) :
    FabricRecipeProvider(output, registriesFuture) {

    private val TOOL_TYPES = listOf(
        ToolType(
            2,
            Items.WOODEN_SWORD,
            Items.STONE_SWORD,
            Items.IRON_SWORD,
            Items.DIAMOND_SWORD,
            Items.NETHERITE_SWORD,
            BobsMobGearBlocks.SWORD_TEMPLATE
        ),
        ToolType(
            3,
            Items.WOODEN_PICKAXE,
            Items.STONE_PICKAXE,
            Items.IRON_PICKAXE,
            Items.DIAMOND_PICKAXE,
            Items.NETHERITE_PICKAXE,
            BobsMobGearBlocks.PICKAXE_TEMPLATE
        ),
        ToolType(
            3,
            Items.WOODEN_AXE,
            Items.STONE_AXE,
            Items.IRON_AXE,
            Items.DIAMOND_AXE,
            Items.NETHERITE_AXE,
            BobsMobGearBlocks.AXE_TEMPLATE
        ),
        ToolType(
            1,
            Items.WOODEN_SHOVEL,
            Items.STONE_SHOVEL,
            Items.IRON_SHOVEL,
            Items.DIAMOND_SHOVEL,
            Items.NETHERITE_SHOVEL,
            BobsMobGearBlocks.SHOVEL_TEMPLATE
        ),
        ToolType(
            2,
            Items.WOODEN_HOE,
            Items.STONE_HOE,
            Items.IRON_HOE,
            Items.DIAMOND_HOE,
            Items.NETHERITE_HOE,
            BobsMobGearBlocks.HOE_TEMPLATE
        ),
    )

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

        for ((material, wood, stone, iron, diamond, netherite, template) in TOOL_TYPES) {

            exporter.shapelessRecipe(RecipeCategory.TOOLS, template.asItem()) {
                input(BobsMobGearItems.EMPTY_TEMPLATE)
                input(wood)

                itemCriterion(BobsMobGearItems.EMPTY_TEMPLATE)
            }

            acceptTemplateRecipe(
                TemplateRecipe(
                    template,
                    null,
                    Ingredient.ofItems(wood),
                    ingredientList(*(
                        List(material) { Ingredient.fromTag(ItemTags.STONE_TOOL_MATERIALS) }
                            + Ingredient.ofItems(Items.STRING)
                    ).toTypedArray()),
                    FluidVariant.blank(),
                    0,
                    false,
                    ItemStack(stone)
                ),
                exporter
            )

            acceptTemplateRecipe(
                TemplateRecipe(
                    template,
                    smithingSurface,
                    Ingredient.ofItems(stone),
                    DefaultedList.of(),
                    FluidVariant.of(BobsMobGearFluids.IRON),
                    material * FluidConstants.INGOT,
                    true,
                    ItemStack(iron, 1).apply {
                        set(BobsMobGearItems.HEATED)
                    }
                ),
                exporter
            )

            acceptTemplateRecipe(
                TemplateRecipe(
                    template,
                    smithingSurface,
                    Ingredient.ofItems(iron),
                    DefaultedList.of(),
                    FluidVariant.of(BobsMobGearFluids.DIAMOND),
                    material * FluidConstants.INGOT,
                    true,
                    ItemStack(diamond).apply {
                        set(BobsMobGearItems.HEATED)
                    }
                ),
                exporter
            )

            acceptTemplateRecipe(
                TemplateRecipe(
                    template,
                    smithingSurface,
                    Ingredient.ofItems(diamond),
                    DefaultedList.of(),
                    FluidVariant.of(BobsMobGearFluids.NETHERITE),
                    1 * FluidConstants.INGOT,
                    true,
                    ItemStack(netherite).apply {
                        set(BobsMobGearItems.HEATED)
                    }
                ),
                exporter
            )
        }

        acceptForgingRecipe(
            ForgingRecipe(
                ingredientList(Ingredient.fromTag(BobsMobGearItemTags.FORGES_IRON_INGOT)),
                FluidVariant.of(BobsMobGearFluids.IRON),
                FluidConstants.INGOT,
                200,
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
                    true,
                    ItemStack(ingot).apply {
                        set(BobsMobGearItems.HEATED)
                    }
                ),
                exporter
            )
    }

    data class ToolType(
        val material: Int,
        val wood: Item,
        val stone: Item,
        val iron: Item,
        val diamond: Item,
        val netherite: Item,
        val template: Block,
    )

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

        private fun RecipeExporter.shapedRecipe(category: RecipeCategory, output: ItemConvertible, count: Int = 1, name: String? = null, init: ShapedRecipeJsonBuilder.() -> Unit) {
            ShapedRecipeJsonBuilder(category, output, count).apply(init).run {
                if (name == null)
                    offerTo(this@shapedRecipe)
                else
                    offerTo(this@shapedRecipe, name)
            }
        }

        private fun RecipeExporter.shapelessRecipe(category: RecipeCategory, output: ItemConvertible, count: Int = 1, name: String? = null, init: ShapelessRecipeJsonBuilder.() -> Unit) {
            ShapelessRecipeJsonBuilder(category, output, count).apply(init).run {
                if (name == null)
                    offerTo(this@shapelessRecipe)
                else
                    offerTo(this@shapelessRecipe, name)
            }
        }

        private fun CraftingRecipeJsonBuilder.itemCriterion(item: ItemConvertible) {
            criterion(hasItem(item), conditionsFromItem(item))
        }
    }
}
