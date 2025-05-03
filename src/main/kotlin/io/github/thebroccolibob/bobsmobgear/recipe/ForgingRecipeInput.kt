package io.github.thebroccolibob.bobsmobgear.recipe

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.item.ItemStack
import net.minecraft.recipe.input.RecipeInput
import net.minecraft.util.collection.DefaultedList

data class ForgingRecipeInput(
    val stacks: DefaultedList<ItemStack>,
    val fluids: Map<FluidVariant, Long>,
) : RecipeInput {
    override fun getStackInSlot(slot: Int): ItemStack = stacks[slot]

    override fun getSize(): Int = stacks.size
}