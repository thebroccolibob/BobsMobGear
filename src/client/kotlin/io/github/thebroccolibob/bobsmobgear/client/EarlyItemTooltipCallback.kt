package io.github.thebroccolibob.bobsmobgear.client

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

@JvmField
val earlyItemTooltipCallback: Event<ItemTooltipCallback> =
    EventFactory.createArrayBacked(ItemTooltipCallback::class.java) { callbacks ->
        ItemTooltipCallback { stack, context, type, lines ->
            for (callback in callbacks) {
                callback.getTooltip(stack, context, type, lines)
            }
        }
    }