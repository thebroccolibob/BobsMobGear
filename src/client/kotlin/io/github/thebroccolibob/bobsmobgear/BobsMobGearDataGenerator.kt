package io.github.thebroccolibob.bobsmobgear

import io.github.thebroccolibob.bobsmobgear.datagen.*
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

object BobsMobGearDataGenerator : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		fabricDataGenerator.createPack().apply {
			addProvider(::RecipeGenerator)
			addProvider(::BlockTagGenerator)
			addProvider(::ItemTagGenerator)
			addProvider(::FluidTagGenerator)
			addProvider(::LootTableGenerator)

			addProvider(::ModelGenerator)
			addProvider(::SoundsGenerator)
			addProvider(::LangGenerator)
			addProvider(::ParticleDataGenerator)
		}
	}
}
