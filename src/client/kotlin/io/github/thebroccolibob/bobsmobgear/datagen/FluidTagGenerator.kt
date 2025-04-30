package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearFluids
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class FluidTagGenerator(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider.FluidTagProvider(output, registriesFuture) {
    override fun configure(wrapperLookup: RegistryWrapper.WrapperLookup) {
        getOrCreateTagBuilder(BobsMobGearFluids.MOLTEN_IRON_TAG)
            .add(BobsMobGearFluids.IRON)
        getOrCreateTagBuilder(BobsMobGearFluids.MOLTEN_DIAMOND_TAG)
            .add(BobsMobGearFluids.DIAMOND)
        getOrCreateTagBuilder(BobsMobGearFluids.MOLTEN_NETHERITE_TAG)
            .add(BobsMobGearFluids.NETHERITE)
    }
}