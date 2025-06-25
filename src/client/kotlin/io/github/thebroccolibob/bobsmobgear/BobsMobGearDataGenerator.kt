package io.github.thebroccolibob.bobsmobgear

import io.github.thebroccolibob.bobsmobgear.datagen.*
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.registry.RegistryBuilder
import net.minecraft.registry.RegistryKeys

object BobsMobGearDataGenerator : DataGeneratorEntrypoint {

	override fun buildRegistry(registryBuilder: RegistryBuilder) {
		registryBuilder.addRegistry(RegistryKeys.ENCHANTMENT, EnchantmentGenerator)
	}

	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		fabricDataGenerator.createPack().apply {
			addProvider(::RecipeGenerator)
			addProvider(::BlockTagGenerator)
			addProvider(::ItemTagGenerator)
			addProvider(::FluidTagGenerator)
			addProvider(::BlockLootTableGenerator)
			addProvider(::EnchantmentGenerator)
			addProvider(::EnchantmentTagGenerator)
			addProvider(::GameEventTagGenerator)

			addProvider(::ModelGenerator)
			addProvider(::SoundsGenerator)
			addProvider(::LangGenerator)
			addProvider(::ParticleDataGenerator)
		}
	}
}
