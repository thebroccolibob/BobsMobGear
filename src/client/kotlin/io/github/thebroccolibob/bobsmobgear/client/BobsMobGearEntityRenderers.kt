package io.github.thebroccolibob.bobsmobgear.client

import io.github.thebroccolibob.bobsmobgear.client.render.entity.EnderSpearEntityRenderer
import io.github.thebroccolibob.bobsmobgear.client.render.entity.WebShotEntityRenderer
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearEntities
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register

internal fun registerEntityRenderers() {
    register(BobsMobGearEntities.WEB_SHOT, ::WebShotEntityRenderer)
    register(BobsMobGearEntities.ENDER_SPEAR, ::EnderSpearEntityRenderer)
    register(BobsMobGearEntities.ENDER_EYE_SPEAR, ::EnderSpearEntityRenderer)
}
