package io.github.thebroccolibob.bobsmobgear.data

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.recipe.input.RecipeInput

data class TemplateRecipeInput(
    val blockBelow: BlockState,
    val template: Block,
    val base: ItemStack,
    val ingredients: List<ItemStack>,
    val fluid: FluidVariant,
    val fluidAmount: Int,
) : RecipeInput {
    val ingredientMatcher = RecipeMatcher().apply {
        for (itemStack in ingredients)
            if (!itemStack.isEmpty)
                addInput(itemStack, 1)
    }

    override fun getStackInSlot(slot: Int): ItemStack = when (slot) {
        0 -> base
        else -> ingredients[slot + 1]
    }

    override fun getSize(): Int = ingredients.size + 1
}