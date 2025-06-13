package io.github.thebroccolibob.bobsmobgear.client

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItemTags
import io.github.thebroccolibob.bobsmobgear.util.Translation
import io.github.thebroccolibob.bobsmobgear.util.isIn
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Util.createTranslationKey

object OffhandPriorityTooltip : ItemTooltipCallback {
    val TOOLTIP = Translation.unit(createTranslationKey("item.tooltip", BobsMobGear.id("offhand_priority"))) {
        formatted(Formatting.DARK_GRAY)
    }

    override fun getTooltip(
        stack: ItemStack,
        tooltipContext: Item.TooltipContext,
        tooltipType: TooltipType,
        lines: MutableList<Text>
    ) {
        if (stack isIn BobsMobGearItemTags.OFFHAND_PRIORITIZED)
            lines.add(TOOLTIP.text)
    }

    fun register() {
        earlyItemTooltipCallback.register(this)
    }
}