package io.github.thebroccolibob.bobsmobgear

import io.github.thebroccolibob.bobsmobgear.client.render.blockentity.TemplateBlockEntityRenderer
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories

object BobsMobGearClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockEntityRendererFactories.register(BobsMobGearBlocks.TEMPLATE_BLOCK_ENTITY, ::TemplateBlockEntityRenderer)
	}
}
