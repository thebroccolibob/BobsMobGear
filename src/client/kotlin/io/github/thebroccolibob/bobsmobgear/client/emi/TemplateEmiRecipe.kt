package io.github.thebroccolibob.bobsmobgear.client.emi

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat
import io.github.thebroccolibob.bobsmobgear.client.util.SizedTexture
import io.github.thebroccolibob.bobsmobgear.client.util.region
import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearComponents
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItemTags
import io.github.thebroccolibob.bobsmobgear.util.groupConsecutive
import io.github.thebroccolibob.bobsmobgear.util.isOf
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import kotlin.jvm.optionals.getOrNull

class TemplateEmiRecipe(private val id: Identifier, private val recipe: TemplateRecipe) : EmiRecipe {
    private val hasRow1 get() = !recipe.fluid.isBlank || recipe.requiresHammer

    private val blockBelow = recipe.blockBelow.getOrNull()?.let { blockTag ->
        blockTag.tagKey.getOrNull()?.let { tagKey ->
            TagKey.of(RegistryKeys.ITEM, tagKey.id)
                ?.takeIf { Registries.ITEM.tagCreatingWrapper.getOptional(it).isPresent }
                ?.let { EmiIngredient.of(it) }
            ?: EmiIngredient.of(tagKey)
        } ?: EmiIngredient.of(blockTag.map { entry -> EmiStack.of(entry.value()) })
    }

    private val ingredients = recipe.ingredients.takeUnless { it.isEmpty() }?.groupConsecutive { count, ingredient ->
        EmiIngredient.of(ingredient, count.toLong())
    }

    private val catalysts = buildList {
        blockBelow?.let(::add)
        if (recipe.requiresHammer)
            add(HAMMER_INGREDIENT)
    }

    private val inputs: List<EmiIngredient> = buildList {
        add(EmiStack.of(recipe.template))
        if (!recipe.base.isEmpty)
            add(EmiIngredient.of(recipe.base))
        ingredients?.let(::addAll)
        if (!recipe.fluid.isBlank)
            add(EmiStack.of(recipe.fluid.fluid, recipe.fluidAmount / BobsMobGearCompat.FLUID_FACTOR))
    }

    private val result = EmiStack.of(recipe.result.let {
        if (it isOf Items.POTATO) it else
        it.copy().apply { remove(BobsMobGearComponents.HEATED) }
    })

    private val outputs = listOf(result)

    override fun getCategory(): EmiRecipeCategory = BobsMobGearEmiPlugin.TEMPLATE_CATEGORY
    override fun getId(): Identifier = id
    override fun getInputs(): List<EmiIngredient> = inputs
    override fun getCatalysts(): List<EmiIngredient> = catalysts
    override fun getOutputs(): List<EmiStack> = outputs
    override fun getDisplayWidth(): Int = 138

    override fun getDisplayHeight(): Int = 47 +
            (if (hasRow1) 22 else 0) +
            (if (ingredients != null) 22 else 0) +
            (if (blockBelow != null) 25 else 0)

    override fun addWidgets(widgets: WidgetHolder) {
        val baseY = 4 +
                (if (ingredients != null) 22 else 0) +
                (if (hasRow1) 22 else 0)

        widgets.addSlot(EmiStack.of(recipe.template), 59, baseY + 22)

        if (blockBelow != null) {
            widgets.addTexture(ANVIL, 45, baseY + 43)
            widgets.addSlot(blockBelow, 59, baseY + 44).catalyst(true)
        }

        var step = 0

        if (!recipe.base.isEmpty) {
            widgets.addTexture(ARROW, 36, baseY + 1)
            widgets.addSlot(EmiIngredient.of(recipe.base), 10, baseY)
            widgets.addTexture(NUMBERS[step++], 0, baseY)
        }
        widgets.add(TemplateBlockWidget(recipe, 48, baseY - 20, 40))

        if (ingredients != null) {
            val y = 4 + if (hasRow1) 22 else 0
            val x = 68 - 9 * ingredients.size

            ingredients.forEachIndexed { index, ingredient ->
                widgets.addSlot(ingredient, x + 18 * index, y)
            }
            widgets.addTexture(NUMBERS[step++], x - 10, y)
        }

        if (!recipe.fluid.isBlank) {
            widgets.addSlot(EmiStack.of(recipe.fluid.fluid, recipe.fluidAmount / BobsMobGearCompat.FLUID_FACTOR), 41, 4)
            widgets.addTexture(NUMBERS[step++], 31, 4)
        }

        if (recipe.requiresHammer) {
            widgets.addSlot(HAMMER_INGREDIENT, 77, 4).catalyst(true)
            widgets.addTexture(NUMBERS[step], 67, 4)
            widgets.addTexture(HAMMER, 63, 10)
        }

        widgets.addSlot(result, 104, baseY - 4).large(true).recipeContext(this)
    }

    companion object {
        val TEXTURE = SizedTexture(BobsMobGear.id("textures/gui/emi/recipe/template.png"), 64, 41)

        val NUMBERS = (0..3).map { TEXTURE.region(it * 9, 0, 9, 5) }
        val HAMMER = TEXTURE.region(36, 0, 13, 5)
        val ARROW = TEXTURE.region(0, 5, 64, 15)
        val ANVIL = TEXTURE.region(0, 20, 44, 21)

        val HAMMER_INGREDIENT: EmiIngredient = EmiIngredient.of(BobsMobGearItemTags.HAMMERS).apply {
            for (stack in emiStacks)
                stack.setRemainder(EmiStack.of(stack.itemStack.copy().apply { damage = 1 }))
        }
    }
}