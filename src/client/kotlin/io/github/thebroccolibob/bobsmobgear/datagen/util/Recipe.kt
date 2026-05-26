package io.github.thebroccolibob.bobsmobgear.datagen.util

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.advancement.AdvancementRequirements
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion
import net.minecraft.block.Block
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder
import net.minecraft.data.server.recipe.RecipeExporter
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromItem
import net.minecraft.data.server.recipe.RecipeProvider.hasItem
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.entry.RegistryEntryList
import net.minecraft.registry.tag.ItemTags
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat
import io.github.thebroccolibob.bobsmobgear.recipe.ForgingRecipe
import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearComponents
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearFluids
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.set

fun acceptTemplateRecipe(recipeId: Identifier, recipe: TemplateRecipe, exporter: RecipeExporter) {
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

fun acceptTemplateRecipe(name: String, recipe: TemplateRecipe, exporter: RecipeExporter) {
    acceptTemplateRecipe(BobsMobGear.id(name).withPrefixedPath("template/"), recipe, exporter)
}

fun acceptTemplateRecipe(recipe: TemplateRecipe, exporter: RecipeExporter) {
    acceptTemplateRecipe(Registries.ITEM.getId(recipe.result.item).run {
        if (namespace == BobsMobGear.MOD_ID || namespace == Identifier.DEFAULT_NAMESPACE) path else "$namespace/$path"
    }, recipe, exporter)
}

fun acceptForgingRecipe(recipeId: Identifier, recipe: ForgingRecipe, exporter: RecipeExporter) {
    exporter.accept(recipeId, recipe, null)
}

fun acceptForgingRecipe(recipe: ForgingRecipe, exporter: RecipeExporter) {
    acceptForgingRecipe(Registries.FLUID.getId(recipe.result.fluid).withPrefixedPath("forging/"), recipe, exporter)
}

fun ingredientList(vararg ingredients: Ingredient): DefaultedList<Ingredient> =
    DefaultedList.copyOf(Ingredient.EMPTY, *ingredients)

fun ingredientList(first: ItemConvertible, vararg items: ItemConvertible): DefaultedList<Ingredient> =
    ingredientList(Ingredient.ofItems(first), *items.map { Ingredient.ofItems(it) }.toTypedArray())

fun RecipeExporter.shapedRecipe(category: RecipeCategory, output: ItemConvertible, count: Int = 1, name: String? = null, init: ShapedRecipeJsonBuilder.() -> Unit) {
    ShapedRecipeJsonBuilder(category, output, count).apply(init).run {
        if (name == null)
            offerTo(this@shapedRecipe)
        else
            offerTo(this@shapedRecipe, name)
    }
}

fun RecipeExporter.shapelessRecipe(category: RecipeCategory, output: ItemConvertible, count: Int = 1, name: String? = null, init: ShapelessRecipeJsonBuilder.() -> Unit) {
    ShapelessRecipeJsonBuilder(category, output, count).apply(init).run {
        if (name == null)
            offerTo(this@shapelessRecipe)
        else
            offerTo(this@shapelessRecipe, name)
    }
}

fun CraftingRecipeJsonBuilder.itemCriterion(item: ItemConvertible) {
    criterion(hasItem(item), conditionsFromItem(item))
}

data class ToolType(
    val material: Int,
    val base: Item,
    val stone: Item?,
    val iron: Item,
    val diamond: Item,
    val netherite: Item,
    val template: Block,
    val modId: String? = null,
    /**
     * Only for iron
     */
    val extraIngredient: ItemConvertible? = null,
    val blackSteel: Item? = null,
) {
    fun register(exporter: RecipeExporter, smithingSurface: RegistryEntryList<Block>, withConditions: (RecipeExporter, ResourceCondition) -> RecipeExporter) {
        val conditionedExporter = modId?.let { withConditions(exporter, ResourceConditions.allModsLoaded(modId)) } ?: exporter

        conditionedExporter.shapelessRecipe(RecipeCategory.TOOLS, template.asItem()) {
            input(BobsMobGearItems.EMPTY_TEMPLATE)
            input(base)

            itemCriterion(BobsMobGearItems.EMPTY_TEMPLATE)
        }

        if (stone != null)
            acceptTemplateRecipe(
                TemplateRecipe(
                    template,
                    null,
                    Ingredient.ofItems(base),
                    ingredientList(*(
                            List(material) { Ingredient.fromTag(ItemTags.STONE_TOOL_MATERIALS) }
                                    + Ingredient.ofItems(Items.STRING)
                            ).toTypedArray()),
                    FluidVariant.blank(),
                    0,
                    false,
                    ItemStack(stone)
                ),
                conditionedExporter
            )

        acceptTemplateRecipe(
            TemplateRecipe(
                template,
                smithingSurface,
                Ingredient.ofItems(stone ?: base),
                extraIngredient?.let { ingredientList(Ingredient.ofItems(it)) } ?: DefaultedList.of(),
                FluidVariant.of(BobsMobGearFluids.IRON),
                material * FluidConstants.INGOT,
                true,
                ItemStack(iron, 1).apply {
                    set(BobsMobGearComponents.HEATED)
                }
            ),
            conditionedExporter
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
                    set(BobsMobGearComponents.HEATED)
                }
            ),
            conditionedExporter
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
                    set(BobsMobGearComponents.HEATED)
                }
            ),
            conditionedExporter
        )

        if (blackSteel != null)
            acceptTemplateRecipe(
                TemplateRecipe(
                    template,
                    smithingSurface,
                    Ingredient.ofItems(iron),
                    DefaultedList.of(),
                    FluidVariant.of(BobsMobGearFluids.BLACK_STEEL),
                    material * FluidConstants.INGOT,
                    true,
                    ItemStack(blackSteel).apply {
                        set(BobsMobGearComponents.HEATED)
                    }
                ),
                withConditions(exporter, ResourceConditions.allModsLoaded(BobsMobGearCompat.CATACLYSM))
            )
    }

    fun getRecipeIds() = listOfNotNull(stone, iron, diamond, netherite, blackSteel).map {
        val id = Registries.ITEM.getId(it)
        BobsMobGear.id("template/${if (id.namespace == Identifier.DEFAULT_NAMESPACE) id.path else "${id.namespace}/${id.path}"}")
    }
}
