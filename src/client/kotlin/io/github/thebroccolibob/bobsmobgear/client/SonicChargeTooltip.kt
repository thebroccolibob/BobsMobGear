package io.github.thebroccolibob.bobsmobgear.client

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.Translation
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Util.createTranslationKey

object SonicChargeTooltip : ItemTooltipCallback {
    val TOOLTIP = Translation.arg(createTranslationKey("item.tooltip", BobsMobGear.id("sonic_charge"))) {
        formatted(Formatting.DARK_AQUA)
    }

    override fun getTooltip(
        stack: ItemStack,
        tooltipContext: Item.TooltipContext?,
        tooltipType: TooltipType?,
        lines: MutableList<Text>
    ) {
        stack[BobsMobGearItems.MAX_SONIC_CHARGE]?.let {
            lines.add(TOOLTIP.text(stack[BobsMobGearItems.SONIC_CHARGE] ?: 0, it))
        }
    }

    fun register() {
        earlyItemTooltipCallback.register(this)
    }
}