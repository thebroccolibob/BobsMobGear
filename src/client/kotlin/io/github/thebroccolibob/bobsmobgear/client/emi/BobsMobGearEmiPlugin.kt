package io.github.thebroccolibob.bobsmobgear.client.emi

import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiStack
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.client.util.SizedTexture
import io.github.thebroccolibob.bobsmobgear.client.util.region
import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.input.RecipeInput
import net.minecraft.util.Identifier


object BobsMobGearEmiPlugin : EmiPlugin {

    private val TEXTURE = SizedTexture(BobsMobGear.id("textures/gui/emi/icons.png"), 32, 16)

    val TEMPLATE_CATEGORY: EmiRecipeCategory = EmiRecipeCategory(
        BobsMobGear.id("template"), EmiStack.of(BobsMobGearItems.SWORD_TEMPLATE), TEXTURE.region(0, 0, 16, 16)
    )

    val FORGE_CATEGORY: EmiRecipeCategory = EmiRecipeCategory(
        BobsMobGear.id("template"), EmiStack.of(BobsMobGearItems.FORGE), TEXTURE.region(16, 0, 16, 16)
    )

    private fun <I: RecipeInput, R: Recipe<I>> EmiRegistry.registerRecipeType(type: RecipeType<R>, createEmiRecipe: (Identifier, R) -> EmiRecipe) {
        for (recipe in recipeManager.listAllOfType(type))
            addRecipe(createEmiRecipe(recipe.id, recipe.value))
    }

    override fun register(registry: EmiRegistry) {
        registry.addCategory(TEMPLATE_CATEGORY)

        registry.addCategory(FORGE_CATEGORY)
        registry.addWorkstation(FORGE_CATEGORY, EmiStack.of(BobsMobGearItems.FORGE))
        registry.addWorkstation(FORGE_CATEGORY, EmiStack.of(BobsMobGearItems.FORGE_HEATER))

        registry.registerRecipeType(TemplateRecipe, ::TemplateEmiRecipe)
    }
}