package io.github.thebroccolibob.bobsmobgear.client.emi

import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.BasicEmiRecipe
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.item.FluidPotItem
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.minecraft.fluid.Fluid
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class ForgeFillingEmiRecipe(id: Identifier, input: ItemStack, fluid: Fluid, amount: Long, output: ItemStack) :
    BasicEmiRecipe(BobsMobGearEmiPlugin.FORGE_FILLING_CATEGORY, id, 94, 26) {

    val input: EmiStack = EmiStack.of(input)
    val fluid: EmiStack = EmiStack.of(fluid, amount)
    val output: EmiStack = EmiStack.of(output)

    init {
        inputs.add(this.input)
        inputs.add(this.fluid)
        outputs.add(this.output)
    }

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addTexture(TEXTURE, 25, 3)
        widgets.addSlot(input, 4, 4)
        widgets.addSlot(fluid, 36, 4)
        widgets.addSlot(output, 68, 0).large(true).recipeContext(this)
    }

    companion object {
        val TEXTURE = EmiTexture(BobsMobGear.id("textures/gui/emi/recipe/forge_filling.png"), 0, 0, 40, 20, 40, 20, 40, 20)

        fun of(pot: FluidPotItem) = ForgeFillingEmiRecipe(
            Registries.ITEM.getId(pot).withPrefixedPath("/forge_filling/"),
            BobsMobGearItems.EMPTY_POT.defaultStack,
            pot.fluid,
            FluidConstants.INGOT,
            pot.defaultStack
        )

        fun addRecipes(registry: EmiRegistry) {
            for (pot in BobsMobGearItems.FILLED_POTS)
                registry.addRecipe(of(pot as FluidPotItem))
        }
    }
}