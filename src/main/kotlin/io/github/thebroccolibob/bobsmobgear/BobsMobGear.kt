package io.github.thebroccolibob.bobsmobgear

import io.github.thebroccolibob.bobsmobgear.registry.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object BobsMobGear : ModInitializer {
	const val MOD_ID = "bobsmobgear"

	fun id(path: String): Identifier = Identifier.of(MOD_ID, path)

    val logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		BobsMobGearBlocks.register()
		BobsMobGearItems.register()
		BobsMobGearEntities.register()
		BobsMobGearParticles.register()
		BobsMobGearFluids.register()
		BobsMobGearEnchantments.register()
		BobsMobGearGameEvents.register()
		registerBobsMobGearRecipes()

		BobsMobGearSounds.register()

		ResourceManagerHelper.registerBuiltinResourcePack(
			id("vanilla_recipe_disable"),
			FabricLoader.getInstance().getModContainer(MOD_ID).get(),
			Text.literal("Vanilla Recipe Disable"),
			ResourcePackActivationType.ALWAYS_ENABLED
		)

		registerHeatedLogic()
	}
}
