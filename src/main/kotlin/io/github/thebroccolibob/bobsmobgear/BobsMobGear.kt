package io.github.thebroccolibob.bobsmobgear

import io.github.thebroccolibob.bobsmobgear.data.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearFluids
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearSounds
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object BobsMobGear : ModInitializer {
	const val MOD_ID = "bobsmobgear"

	fun id(path: String): Identifier = Identifier.of(MOD_ID, path)

    private val logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Registry.register(Registries.RECIPE_SERIALIZER, id("template"), TemplateRecipe)
		Registry.register(Registries.RECIPE_TYPE, id("template"), TemplateRecipe)

		BobsMobGearBlocks.register()
		BobsMobGearItems.register()
		BobsMobGearFluids.register()

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
