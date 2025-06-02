package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class BlockLootTableGenerator(
    dataOutput: FabricDataOutput,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricBlockLootTableProvider(dataOutput, registryLookup) {
    override fun generate() {
        addDrop(BobsMobGearBlocks.EMPTY_TEMPLATE)
        addDrop(BobsMobGearBlocks.SWORD_TEMPLATE)
        addDrop(BobsMobGearBlocks.PICKAXE_TEMPLATE)
        addDrop(BobsMobGearBlocks.AXE_TEMPLATE)
        addDrop(BobsMobGearBlocks.SHOVEL_TEMPLATE)
        addDrop(BobsMobGearBlocks.HOE_TEMPLATE)
        addDrop(BobsMobGearBlocks.FORGE)
        addDrop(BobsMobGearBlocks.FORGE_HEATER)
    }
}
