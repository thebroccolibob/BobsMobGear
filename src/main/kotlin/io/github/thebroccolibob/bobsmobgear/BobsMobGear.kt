package io.github.thebroccolibob.bobsmobgear

import com.mojang.serialization.JsonOps
import io.github.thebroccolibob.bobsmobgear.data.TemplateRecipe
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryOps
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
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

		// Temporary debug code
		// TODO remove
		UseItemCallback.EVENT.register { player, world, hand ->
			val stack = player.getStackInHand(hand)
			if (!stack.isOf(Items.STICK)) return@register TypedActionResult.pass(stack)

			for (recipe in world.recipeManager.listAllOfType(TemplateRecipe))
				TemplateRecipe.CODEC.codec().encodeStart(RegistryOps.of(JsonOps.INSTANCE, world.registryManager), recipe.value).ifSuccess {
					player.sendMessage(
						Text.literal("[${if (world.isClient) "CLIENT" else "SERVER"}]")
							.append(Text.literal(it.toString()))
					)
				}

			TypedActionResult.success(stack)
		}
	}
}