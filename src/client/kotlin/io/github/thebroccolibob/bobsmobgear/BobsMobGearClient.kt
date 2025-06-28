package io.github.thebroccolibob.bobsmobgear

import io.github.thebroccolibob.bobsmobgear.client.*
import io.github.thebroccolibob.bobsmobgear.client.duck.TriggersAttack
import io.github.thebroccolibob.bobsmobgear.client.render.blockentity.TemplateBlockEntityRenderer
import io.github.thebroccolibob.bobsmobgear.client.render.gui.SonicChargeHudRenderer
import io.github.thebroccolibob.bobsmobgear.client.render.item.TongsItemRenderer
import io.github.thebroccolibob.bobsmobgear.client.render.item.WardenFistItemRenderer
import io.github.thebroccolibob.bobsmobgear.event.ClientSpecialAttackCallback
import io.github.thebroccolibob.bobsmobgear.item.HasSpecialAttack
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearFluids
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.util.ActionResult

object BobsMobGearClient : ClientModInitializer {
	val LIQUID_METAL_TEXTURE = BobsMobGear.id("block/liquid_metal");

	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ModelPredicateProviderRegistry.register(BobsMobGear.id("using")) { stack, _, entity, _ ->
			if (entity != null && entity.isUsingItem && entity.activeItem == stack) 1f else 0f
		}

		BlockEntityRendererFactories.register(BobsMobGearBlocks.TEMPLATE_BLOCK_ENTITY, ::TemplateBlockEntityRenderer)

		for (fluid in BobsMobGearFluids.LIQUID_METALS)
			FluidRenderHandlerRegistry.INSTANCE.register(fluid, SimpleFluidRenderHandler(
				LIQUID_METAL_TEXTURE,
				LIQUID_METAL_TEXTURE,
				fluid.tint
			))

		registerParticleFactories()
		registerEntityRenderers()
		TongsItemRenderer.register()
		WardenFistItemRenderer.register()
		HeatedTooltip.register()
		UsePriorityTooltip.register()
		SonicChargeTooltip.register()
		SonicChargeHudRenderer.register()

		ClientSpecialAttackCallback.register { stack, user, _, _ ->
			if (user != MinecraftClient.getInstance().player) return@register ActionResult.PASS
			val item = stack.item
			if (item !is HasSpecialAttack) return@register ActionResult.PASS
			val client = MinecraftClient.getInstance()
			(client as TriggersAttack).`bobsmobgear$startAttack`()
			ActionResult.CONSUME
		}
	}
}
