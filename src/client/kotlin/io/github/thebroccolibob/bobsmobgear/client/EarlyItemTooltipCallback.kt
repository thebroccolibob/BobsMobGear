package io.github.thebroccolibob.bobsmobgear.client

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

object EarlyItemTooltipCallback { // There's gotta be a better way than making an object that holds one field
    @JvmField
    val EVENT: Event<ItemTooltipCallback> =
        EventFactory.createArrayBacked(ItemTooltipCallback::class.java) { callbacks ->
            ItemTooltipCallback { stack, context, type, lines ->
                for (callback in callbacks) {
                    callback.getTooltip(stack, context, type, lines)
                }
            }
        }
}