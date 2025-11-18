package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearFluids
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider
import com.mojang.serialization.Codec
import net.minecraft.data.DataOutput.OutputType
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class EmiRecipeDefaultGenerator(
    dataOutput: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>,
) : FabricCodecDataProvider<List<Identifier>>(dataOutput, registriesFuture, OutputType.RESOURCE_PACK, "recipe/defaults", CODEC) {

    override fun configure(
        provider: BiConsumer<Identifier, List<Identifier>>,
        lookup: RegistryWrapper.WrapperLookup?
    ) {
        provider.accept(Identifier.of("emi", BobsMobGear.MOD_ID), RECIPES)
    }

    override fun getName(): String = "EMI Recipe Defaults"

    companion object {
        val CODEC: Codec<List<Identifier>> = Identifier.CODEC.listOf().fieldOf("added").codec()

        val RECIPES =
            RecipeGenerator.TOOL_TYPES.flatMap { it.getRecipeIds() } +
            (if (BobsMobGearCompat.DATAGEN_REQUIREMENTS) ReplacementRecipeGenerator.TOOL_TYPES.flatMap { it.getRecipeIds() } else listOf()) +
            listOf(BobsMobGearFluids.IRON, BobsMobGearFluids.DIAMOND, BobsMobGearFluids.BLACK_STEEL).map {
                Registries.FLUID.getId(it).withPrefixedPath("forging/")
            } +
            BobsMobGearBlocks.TEMPLATES.map(Registries.BLOCK::getId) +
            listOf(
                BobsMobGear.id("template/netherite_ingot"),
                BobsMobGear.id("forging/netherite_alloying"),
            )
    }
}
