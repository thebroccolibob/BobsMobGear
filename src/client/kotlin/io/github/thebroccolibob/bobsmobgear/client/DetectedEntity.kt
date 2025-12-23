package io.github.thebroccolibob.bobsmobgear.client

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import io.github.thebroccolibob.bobsmobgear.network.DetectedEntityPayload

object DetectedEntity : ClientTickEvents.EndWorldTick {
    private var visibleTicks = 0
    @JvmStatic
    var entity: Entity? = null
        set(value) {
            field = value
            visibleTicks = MAX_VISIBLE_TICKS
        }

    const val MAX_VISIBLE_TICKS = 20 * 2
    const val OUTLINE_COLOR = 0x29DFEB

    override fun onEndTick(world: ClientWorld) {
        if (visibleTicks <= 0) return
        visibleTicks--
        if (visibleTicks <= 0)
            entity = null
    }

    fun register() {
        ClientTickEvents.END_WORLD_TICK.register(this)
        ClientPlayNetworking.registerGlobalReceiver(DetectedEntityPayload.ID) { payload, context ->
            entity = payload.getEntity(context.player().world)
        }
    }
}