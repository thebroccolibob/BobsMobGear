package io.github.thebroccolibob.bobsmobgear

import io.github.thebroccolibob.bobsmobgear.client.registerBobsMobGearParticleFactories
import io.github.thebroccolibob.bobsmobgear.client.render.blockentity.TemplateBlockEntityRenderer
import io.github.thebroccolibob.bobsmobgear.client.render.item.TongsItemRenderer
import io.github.thebroccolibob.bobsmobgear.client.render.item.WardenFistItemRenderer
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearFluids
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.Translation
import net.bettercombat.api.client.BetterCombatClientEvents
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.util.Formatting

object BobsMobGearClient : ClientModInitializer {
	val HEATED_TOOLTIP = Translation.unit("item.bobsmobgear.heated") {
		formatted(Formatting.GOLD)
	}

	val LIQUID_METAL_TEXTURE = BobsMobGear.id("block/liquid_metal");

	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ModelPredicateProviderRegistry.register(BobsMobGear.id("blocking")) { stack, _, entity, _ ->
			if (entity?.activeItem == stack) 1f else 0f
		}

		BlockEntityRendererFactories.register(BobsMobGearBlocks.TEMPLATE_BLOCK_ENTITY, ::TemplateBlockEntityRenderer)

		ItemTooltipCallback.EVENT.register { stack, _, _, lines ->
			if (BobsMobGearItems.HEATED in stack)
				lines.add(HEATED_TOOLTIP.text().formatted())
		}

		for (fluid in BobsMobGearFluids.LIQUID_METALS)
			FluidRenderHandlerRegistry.INSTANCE.register(fluid, SimpleFluidRenderHandler(
				LIQUID_METAL_TEXTURE,
				LIQUID_METAL_TEXTURE,
				fluid.tint
			))

		registerBobsMobGearParticleFactories()
		TongsItemRenderer.register()
		WardenFistItemRenderer.register()

		BetterCombatClientEvents.ATTACK_HIT.register { player, hand, _, _ ->
		}
	}
}
