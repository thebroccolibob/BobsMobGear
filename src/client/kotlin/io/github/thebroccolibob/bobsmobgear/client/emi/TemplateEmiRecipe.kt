package io.github.thebroccolibob.bobsmobgear.client.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.util.countUnique
import net.minecraft.util.Identifier

class TemplateEmiRecipe(private val id: Identifier, private val recipe: TemplateRecipe) : EmiRecipe {
    private val inputs: List<EmiIngredient> = buildList {
        add(EmiStack.of(recipe.template.asItem()))
        add(EmiIngredient.of(recipe.base))
        addAll(recipe.ingredients.countUnique().map { (ingredient, amount) -> EmiIngredient.of(ingredient, amount.toLong()) })
        if (!recipe.fluid.isBlank)
            add(EmiStack.of(recipe.fluid.fluid, recipe.fluidAmount))
    }

    override fun getCategory(): EmiRecipeCategory = BobsMobGearEmiPlugin.TEMPLATE_CATEGORY

    override fun getId(): Identifier = id

    override fun getInputs(): List<EmiIngredient> = inputs

    override fun getOutputs(): List<EmiStack> = listOf(EmiStack.of(recipe.result))

    override fun getDisplayWidth(): Int = 76

    override fun getDisplayHeight(): Int = 18 * 2

    override fun addWidgets(widgets: WidgetHolder) {
        inputs.forEachIndexed { index, input ->
            widgets.addSlot(input, index * 18, 0)
        }
        outputs.forEachIndexed { index, stack ->
            widgets.addSlot(stack, index * 18, 18).recipeContext(this)
        }
    }
}