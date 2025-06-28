package io.github.thebroccolibob.bobsmobgear.client

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.Translation
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object HeatedTooltip : ItemTooltipCallback {
    val TOOLTIP = Translation.unit("item.bobsmobgear.heated") {
        formatted(Formatting.GOLD)
    }

    override fun getTooltip(stack: ItemStack, tooltipContext: Item.TooltipContext, tooltipType: TooltipType, lines: MutableList<Text>) {
        if (BobsMobGearItems.HEATED in stack)
            lines.add(TOOLTIP.text())
    }

    fun register() {
        ItemTooltipCallback.EVENT.register(this)
    }
}