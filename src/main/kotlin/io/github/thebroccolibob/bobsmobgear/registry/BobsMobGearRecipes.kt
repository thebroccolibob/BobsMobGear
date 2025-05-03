package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.recipe.ForgeAlloyingRecipe
import io.github.thebroccolibob.bobsmobgear.recipe.ForgeMeltingRecipe
import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

private fun <T, S> register(path: String, serializerAndType: S) where S: RecipeSerializer<T>, S: RecipeType<T> {
    Registry.register(Registries.RECIPE_SERIALIZER, BobsMobGear.id(path), serializerAndType)
    Registry.register(Registries.RECIPE_TYPE, BobsMobGear.id(path), serializerAndType)
}

fun registerBobsMobGearRecipes() {
    register("template", TemplateRecipe)
    register("forge_melting", ForgeMeltingRecipe)
    register("forge_alloying", ForgeAlloyingRecipe)
}