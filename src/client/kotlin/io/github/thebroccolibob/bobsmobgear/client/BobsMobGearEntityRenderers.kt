package io.github.thebroccolibob.bobsmobgear.client

import io.github.thebroccolibob.bobsmobgear.client.render.entity.WebShotEntityRenderer
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearEntities
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

internal fun registerEntityRenderers() {
    EntityRendererRegistry.register(BobsMobGearEntities.WEB_SHOT, ::WebShotEntityRenderer)
}
