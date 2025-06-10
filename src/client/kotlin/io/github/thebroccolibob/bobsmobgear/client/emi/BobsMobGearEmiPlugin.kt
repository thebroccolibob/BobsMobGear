package io.github.thebroccolibob.bobsmobgear.client.emi

import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiStack
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import net.minecraft.item.Item
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.input.RecipeInput
import net.minecraft.util.Identifier


object BobsMobGearEmiPlugin : EmiPlugin {

    private val TEXTURE = BobsMobGear.id("textures/gui/emi_simplified_textures.png")

    private fun emiStack(item: Item): EmiStack = EmiStack.of(item)

    val SWORD_TEMPLATE = emiStack(BobsMobGearItems.SWORD_TEMPLATE)
    val TEMPLATES = BobsMobGearItems.TEMPLATES.map { emiStack(it) }

    val TEMPLATE_CATEGORY: EmiRecipeCategory = EmiRecipeCategory(
        BobsMobGear.id("template"), SWORD_TEMPLATE, EmiTexture(
            TEXTURE, 0, 0, 16, 16))

    private fun <I: RecipeInput, R: Recipe<I>> EmiRegistry.registerRecipeType(type: RecipeType<R>, createEmiRecipe: (Identifier, R) -> EmiRecipe) {
        for (recipe in recipeManager.listAllOfType(type))
            addRecipe(createEmiRecipe(recipe.id, recipe.value))
    }

    override fun register(registry: EmiRegistry) {
        registry.addCategory(TEMPLATE_CATEGORY)
        for (template in TEMPLATES)
            registry.addWorkstation(TEMPLATE_CATEGORY, template)

        registry.registerRecipeType(TemplateRecipe, ::TemplateEmiRecipe)
    }
}