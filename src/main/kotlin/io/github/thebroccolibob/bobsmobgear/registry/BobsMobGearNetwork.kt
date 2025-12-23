package io.github.thebroccolibob.bobsmobgear.registry

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import io.github.thebroccolibob.bobsmobgear.network.DetectedEntityPayload

internal fun registerBobsMobGearPayloads() {
    with (PayloadTypeRegistry.playS2C()) {
        register(DetectedEntityPayload.ID, DetectedEntityPayload.CODEC)
    }
}