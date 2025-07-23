package io.github.thebroccolibob.bobsmobgear.client.emi

import dev.emi.emi.api.recipe.BasicEmiRecipe
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat
import io.github.thebroccolibob.bobsmobgear.recipe.ForgingRecipe
import io.github.thebroccolibob.bobsmobgear.util.Translation
import io.github.thebroccolibob.bobsmobgear.util.groupConsecutive
import net.minecraft.util.Identifier
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class ForgingEmiRecipe(id: Identifier, private val recipe: ForgingRecipe)
    : BasicEmiRecipe(BobsMobGearEmiPlugin.FORGING_CATEGORY, id, 100, 18 * ((recipe.ingredients.size + 1) / 2) + 23) {

    private val output: EmiStack = EmiStack.of(recipe.result.fluid, recipe.resultAmount / BobsMobGearCompat.FLUID_FACTOR)

    init {
        inputs.addAll(recipe.ingredients.groupConsecutive { i, ingredient -> EmiIngredient.of(ingredient, i.toLong()) })
        outputs.add(output)
    }

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addFillingArrow(47, 5, recipe.forgingTime * 1000 / 20).tooltipText(listOf(
            FORGING_TIME.text(DECIMAL_FORMAT.format(recipe.forgingTime / 20.0))
        ))
        inputs.forEachIndexed { index, ingredient ->
            widgets.addSlot(ingredient, (if (inputs.size % 2 != 0 && index == inputs.size - 1) 9 else 18 * (index % 2)) + 4, 18 * (index / 2) + 4)
        }

        widgets.addTexture(EmiTexture.EMPTY_FLAME, 15, 24)
        widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 15, 24, 4000, false, true, true)

        widgets.addSlot(output, 78, 4).recipeContext(this)
    }

    companion object {
        val FORGING_TIME = Translation.arg("emi.cooking.time")

        val DECIMAL_FORMAT: DecimalFormat = DecimalFormat("0.0").apply {
            decimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.ROOT)
        }
    }
}