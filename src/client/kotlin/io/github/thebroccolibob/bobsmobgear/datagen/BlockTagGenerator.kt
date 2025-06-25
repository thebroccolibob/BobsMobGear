package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.block.Blocks
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.BlockTags
import java.util.concurrent.CompletableFuture

class BlockTagGenerator(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider.BlockTagProvider(output, registriesFuture) {
    override fun configure(wrapperLookup: RegistryWrapper.WrapperLookup) {
        getOrCreateTagBuilder(BobsMobGearBlocks.SMITHING_SURFACE).apply {
            add(Blocks.SMITHING_TABLE)
            forceAddTag(BlockTags.ANVIL)
        }
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).apply {
            add(*BobsMobGearBlocks.TEMPLATES)
        }
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(
            BobsMobGearBlocks.FORGE,
            BobsMobGearBlocks.FORGE_HEATER,
        )
    }
}
