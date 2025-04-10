package io.github.thebroccolibob.bobsmobgear.data

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.item.ItemStack
import net.minecraft.recipe.input.RecipeInput

data class TemplateRecipeInput(
    val base: ItemStack,
    val ingredients: List<ItemStack>,
    val fluid: FluidVariant,
) : RecipeInput {
    override fun getStackInSlot(slot: Int): ItemStack = when (slot) {
        0 -> base
        else -> ingredients[slot + 1]
    }

    override fun getSize(): Int = ingredients.size + 1
}