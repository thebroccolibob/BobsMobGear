package io.github.thebroccolibob.bobsmobgear.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack

fun interface ItemTickCallback {
    fun tick(entity: Entity, stack: ItemStack)

    companion object {
        @JvmField
        val EVENT: Event<ItemTickCallback> = EventFactory.createArrayBacked(ItemTickCallback::class.java) { listeners ->
            ItemTickCallback { entity, stack ->
                for (listener in listeners)
                    listener.tick(entity, stack)
            }
        }
    }
}
