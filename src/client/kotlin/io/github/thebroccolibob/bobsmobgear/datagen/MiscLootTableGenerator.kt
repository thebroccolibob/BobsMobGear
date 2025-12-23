package io.github.thebroccolibob.bobsmobgear.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.loot.LootTable
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryWrapper
import io.github.thebroccolibob.bobsmobgear.datagen.util.lootTableBuilder
import io.github.thebroccolibob.bobsmobgear.datagen.util.pool
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearLoot
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class MiscLootTableGenerator(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>,
) : SimpleFabricLootTableProvider(output, registryLookup, LootContextTypes.SHEARING) {
    override fun accept(lootTableBiConsumer: BiConsumer<RegistryKey<LootTable>, LootTable.Builder>) {
        lootTableBiConsumer.accept(BobsMobGearLoot.SHEAR_WARDEN, lootTableBuilder {
            pool {
                with(ItemEntry.builder(BobsMobGearItems.SCULK_SYMBIOTE))
            }
        })
    }
}