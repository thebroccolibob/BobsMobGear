package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider
import net.minecraft.data.DataOutput.OutputType
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class EmiReplacementRecipeDefaultGenerator(
    dataOutput: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>,
) : FabricCodecDataProvider<List<Identifier>>(dataOutput, registriesFuture, OutputType.RESOURCE_PACK, "recipe/defaults", EmiRecipeDefaultGenerator.CODEC) {

    override fun configure(
        provider: BiConsumer<Identifier, List<Identifier>>,
        lookup: RegistryWrapper.WrapperLookup?
    ) {
        provider.accept(Identifier.of("emi", BobsMobGear.MOD_ID + "_replacements"), RECIPES)
    }

    override fun getName(): String = "EMI Recipe Defaults"

    companion object {
        val RECIPES = ReplacementRecipeGenerator.TOOL_TYPES.flatMap { it.getRecipeIds() }
    }
}
