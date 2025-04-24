package io.github.thebroccolibob.bobsmobgear

import io.github.thebroccolibob.bobsmobgear.client.render.blockentity.TemplateBlockEntityRenderer
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.Translation
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.util.Formatting

object BobsMobGearClient : ClientModInitializer {
	val HEATED_TOOLTIP = Translation.unit("item.bobsmobgear.heated") {
		formatted(Formatting.GOLD)
	}

	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockEntityRendererFactories.register(BobsMobGearBlocks.TEMPLATE_BLOCK_ENTITY, ::TemplateBlockEntityRenderer)

		ItemTooltipCallback.EVENT.register { stack, _, _, lines ->
			if (BobsMobGearItems.HEATED in stack)
				lines.add(HEATED_TOOLTIP.text().formatted())
		}
	}
}
