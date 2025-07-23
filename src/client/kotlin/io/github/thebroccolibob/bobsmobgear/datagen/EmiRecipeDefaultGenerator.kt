package io.github.thebroccolibob.bobsmobgear.datagen

import com.mojang.serialization.Codec
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearFluids
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider
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
            RecipeGenerator.TOOL_TYPES.flatMap { type -> type.run { listOf(stone, iron, diamond, netherite, blackSteel) }.filterNotNull().map {
                val id = Registries.ITEM.getId(it)
                BobsMobGear.id("template/${if (id.namespace == Identifier.DEFAULT_NAMESPACE) id.path else "${id.namespace}/${id.path}"}")
            } } +
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