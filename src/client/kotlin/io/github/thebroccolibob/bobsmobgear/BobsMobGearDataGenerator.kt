package io.github.thebroccolibob.bobsmobgear

import io.github.thebroccolibob.bobsmobgear.datagen.*
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.registry.RegistryBuilder
import net.minecraft.registry.RegistryKeys

object BobsMobGearDataGenerator : DataGeneratorEntrypoint {

	override fun buildRegistry(registryBuilder: RegistryBuilder) {
		registryBuilder.addRegistry(RegistryKeys.ENCHANTMENT, EnchantmentGenerator)
		registryBuilder.addRegistry(RegistryKeys.DAMAGE_TYPE, DamageTypeGenerator)
	}

	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		with (fabricDataGenerator.createPack()) {
			addProvider(::RecipeGenerator)
			addProvider(::BlockTagGenerator)
			addProvider(::ItemTagGenerator)
			addProvider(::FluidTagGenerator)
			addProvider(::BlockLootTableGenerator)
			addProvider(::EnchantmentGenerator)
			addProvider(::EnchantmentTagGenerator)
			addProvider(::GameEventTagGenerator)
			addProvider(::DamageTypeGenerator)
			addProvider(::DamageTypeTagGenerator)
			addProvider(::CreateRecipeGenerator)

			addProvider(::ModelGenerator)
			addProvider(::SoundsGenerator)
			addProvider(::LangGenerator)
			addProvider(::ParticleDataGenerator)
			addProvider(::EmiRecipeDefaultGenerator)
		}

        with (fabricDataGenerator.createBuiltinResourcePack(BobsMobGear.id("recipe_replacements"))) {
            addProvider(::ReplacementRecipeGenerator)
        }
	}
}
