package io.github.thebroccolibob.bobsmobgear.registry

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes
import net.minecraft.fluid.Fluid
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Util.createTranslationKey
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat
import io.github.thebroccolibob.bobsmobgear.fluid.MetalFluid
import java.util.*
import kotlin.jvm.optionals.getOrNull

object BobsMobGearFluids {
    private fun <T: Fluid> register(path: String, fluid: T): T =
        Registry.register(Registries.FLUID, BobsMobGear.id(path), fluid)

    val METAL_FLUID_ATTRIBUTES = object : FluidVariantAttributeHandler {
        override fun getEmptySound(variant: FluidVariant?): Optional<SoundEvent> = Optional.of(SoundEvents.ITEM_BUCKET_EMPTY_LAVA)

        override fun getName(fluidVariant: FluidVariant): Text = fluidVariant.registryEntry.key.getOrNull()
            ?.value?.let { Text.translatable(createTranslationKey("fluid", it)) }
            ?: super.getName(fluidVariant)
    }

    private fun tagOf(id: Identifier): TagKey<Fluid> = TagKey.of(RegistryKeys.FLUID, id)
    private fun tagOf(path: String) = tagOf(BobsMobGear.id(path))

    @JvmField val IRON = register("iron", MetalFluid(0xD8D8D8, BobsMobGearParticles.IRON_DRIPS.dripping))
    @JvmField val DIAMOND = register("diamond", MetalFluid(0x4AEDD9, BobsMobGearParticles.DIAMOND_DRIPS.dripping))
    @JvmField val NETHERITE = register("netherite", MetalFluid(0x4C4143, BobsMobGearParticles.NETHERITE_DRIPS.dripping))
    @JvmField val BLACK_STEEL = register("${BobsMobGearCompat.CATACLYSM}/black_steel", MetalFluid(0x111133, BobsMobGearParticles.BLACK_STEEL_DRIPS.dripping))

    @JvmField val LIQUID_METALS = listOf(IRON, DIAMOND, NETHERITE, BLACK_STEEL)

    @JvmField val MOLTEN_IRON_TAG = tagOf(Identifier.of("c", "molten_iron"))
    @JvmField val MOLTEN_DIAMOND_TAG = tagOf(Identifier.of("c", "molten_diamond"))
    @JvmField val MOLTEN_NETHERITE_TAG = tagOf(Identifier.of("c", "molten_netherite"))

    fun register() {
        FluidVariantAttributes.register(IRON, METAL_FLUID_ATTRIBUTES)
        FluidVariantAttributes.register(DIAMOND, METAL_FLUID_ATTRIBUTES)
        FluidVariantAttributes.register(NETHERITE, METAL_FLUID_ATTRIBUTES)
        FluidVariantAttributes.register(BLACK_STEEL, METAL_FLUID_ATTRIBUTES)
    }
}