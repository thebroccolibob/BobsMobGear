package io.github.thebroccolibob.bobsmobgear.client

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearComponents
import io.github.thebroccolibob.bobsmobgear.util.Translation

object VanishingTooltip : ItemTooltipCallback {
    val TOOLTIP = Translation.unit("item.bobsmobgear.vanishing") {
        formatted(Formatting.RED)
    }

    override fun getTooltip(stack: ItemStack, tooltipContext: Item.TooltipContext, tooltipType: TooltipType, lines: MutableList<Text>) {
        if (BobsMobGearComponents.VANISHING in stack)
            lines.add(TOOLTIP.text)
    }

    fun register() {
        ItemTooltipCallback.EVENT.register(this)
    }
}