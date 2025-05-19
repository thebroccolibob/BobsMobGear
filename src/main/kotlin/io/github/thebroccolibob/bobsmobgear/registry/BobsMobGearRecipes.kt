package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.recipe.ForgingRecipe
import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

private fun <T, S> register(serializerAndType: S) where S: RecipeSerializer<T>, S: RecipeType<T> {
    Registry.register(Registries.RECIPE_SERIALIZER, serializerAndType.toString(), serializerAndType)
    Registry.register(Registries.RECIPE_TYPE, serializerAndType.toString(), serializerAndType)
}

fun registerBobsMobGearRecipes() {
    register(TemplateRecipe)
    register(ForgingRecipe)
}