package io.github.thebroccolibob.bobsmobgear.data

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.recipe.input.RecipeInput

// null indicates values should be skipped when checking matches
data class TemplateRecipeInput(
    val blockBelow: BlockState,
    val template: Block,
    val base: ItemStack,
    val ingredients: List<ItemStack>?,
    val fluid: FluidVariant?,
    val fluidAmount: Long?,
    val ingredientsPartial: Boolean = false,
) : RecipeInput {
    override fun getStackInSlot(slot: Int): ItemStack = when (slot) {
        0 -> base
        else -> ingredients?.get(slot - 1) ?: ItemStack.EMPTY
    }

    override fun getSize(): Int = (ingredients?.size ?: 0) + 1
}
