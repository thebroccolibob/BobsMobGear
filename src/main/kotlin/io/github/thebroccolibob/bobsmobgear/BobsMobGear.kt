package io.github.thebroccolibob.bobsmobgear

import io.github.thebroccolibob.bobsmobgear.data.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.event.ItemTickCallback
import io.github.thebroccolibob.bobsmobgear.mixin.AbstractCauldronBlockInvoker
import io.github.thebroccolibob.bobsmobgear.mixin.LeveledCauldronBlockInvoker
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearSounds
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.isOf
import io.github.thebroccolibob.bobsmobgear.util.isWhole
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Blocks
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
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

		BobsMobGearSounds.register()

		ResourceManagerHelper.registerBuiltinResourcePack(
			id("vanilla_recipe_disable"),
			FabricLoader.getInstance().getModContainer(MOD_ID).get(),
			Text.literal("Vanilla Recipe Disable"),
			ResourcePackActivationType.ALWAYS_ENABLED
		)

		UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
			if (player.isSpectator) return@register ActionResult.PASS

			val stack = player[hand]
			val state = world[hitResult.blockPos]
			if (BobsMobGearItems.HEATED !in stack
				|| !(state isOf Blocks.WATER_CAULDRON)
				|| (hitResult.side != Direction.UP && (hitResult.pos.x.isWhole() || hitResult.pos.z.isWhole())))
				return@register ActionResult.PASS

			stack.remove(BobsMobGearItems.HEATED)
			world.playSound(player, hitResult.blockPos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, player.soundCategory)
			(world as? ServerWorld)?.run {
				val particlePos = hitResult.blockPos.toCenterPos().add(0.0, 0.4, 0.0)
				spawnParticles(ParticleTypes.CLOUD, particlePos.x, particlePos.y, particlePos.z, 4, 0.25, 0.125, 0.25, 0.0)
			}
			(state.block as? LeveledCauldronBlockInvoker)?.invokeOnFireCollision(state, world, hitResult.blockPos)

			ActionResult.SUCCESS
		}

		ItemTickCallback.EVENT.register { entity, stack, ->
			if (BobsMobGearItems.HEATED !in stack) return@register

			val state = entity.blockStateAtPos
			val inCauldron = (state isOf Blocks.WATER_CAULDRON || state isOf Blocks.POWDER_SNOW_CAULDRON)
					&& (Blocks.WATER_CAULDRON as AbstractCauldronBlockInvoker).invokeIsEntityTouchingFluid(state, entity.blockPos, entity)

			if (entity.isInsideWaterOrBubbleColumn || inCauldron || entity.inPowderSnow) {
				stack.remove(BobsMobGearItems.HEATED)
				entity.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 1f, 1f)
				(entity.world as? ServerWorld)?.run {
					val particlePos = Vec3d(entity.x, entity.getBodyY(0.5), entity.z)
					spawnParticles(ParticleTypes.CLOUD, particlePos.x, particlePos.y, particlePos.z, 4, 0.25, 0.125, 0.25, 0.0)
				}
				if (inCauldron)
					(state.block as? LeveledCauldronBlockInvoker)?.invokeOnFireCollision(state, entity.world, entity.blockPos)

				return@register
			}

			if (entity is LivingEntity)
				if (entity.isWet) {
					if (entity.age.mod(20) == 0)
						entity.damage(entity.world.damageSources.onFire(), 1f) // TODO custom damage source?
				} else
					entity.setOnFireForTicks(20)
		}
	}
}
