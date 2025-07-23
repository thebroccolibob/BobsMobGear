package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat
import io.github.thebroccolibob.bobsmobgear.fluid.MetalFluid
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
import net.minecraft.util.Identifier
import java.util.*

object BobsMobGearFluids {
    private fun <T: Fluid> register(path: String, fluid: T): T =
        Registry.register(Registries.FLUID, BobsMobGear.id(path), fluid)

    private fun registerAttributes(fluid: Fluid, emptySound: SoundEvent? = null) {
        FluidVariantAttributes.register(fluid, object : FluidVariantAttributeHandler {
            val emptySoundOptional = Optional.ofNullable(emptySound)

            override fun getEmptySound(variant: FluidVariant?): Optional<SoundEvent> = emptySoundOptional
        })
    }

    private fun tagOf(id: Identifier): TagKey<Fluid> = TagKey.of(RegistryKeys.FLUID, id)
    private fun tagOf(path: String) = tagOf(BobsMobGear.id(path))

    val IRON = register("iron", MetalFluid(0xD8AF93, BobsMobGearParticles.IRON_DRIPS.dripping))
    val DIAMOND = register("diamond", MetalFluid(0x20C5B5, BobsMobGearParticles.DIAMOND_DRIPS.dripping))
    val NETHERITE = register("netherite", MetalFluid(0x111111, BobsMobGearParticles.NETHERITE_DRIPS.dripping))
    val BLACK_STEEL = register("${BobsMobGearCompat.CATACLYSM}/black_steel", MetalFluid(0x111133, BobsMobGearParticles.BLACK_STEEL_DRIPS.dripping))

    val LIQUID_METALS = listOf(IRON, DIAMOND, NETHERITE)

    val MOLTEN_IRON_TAG = tagOf(Identifier.of("c", "molten_iron"))
    val MOLTEN_DIAMOND_TAG = tagOf(Identifier.of("c", "molten_diamond"))
    val MOLTEN_NETHERITE_TAG = tagOf(Identifier.of("c", "molten_netherite"))

    fun register() {
        registerAttributes(IRON, SoundEvents.ITEM_BUCKET_EMPTY_LAVA)
        registerAttributes(DIAMOND, SoundEvents.ITEM_BUCKET_EMPTY_LAVA)
        registerAttributes(NETHERITE, SoundEvents.ITEM_BUCKET_EMPTY_LAVA)
        registerAttributes(BLACK_STEEL, SoundEvents.ITEM_BUCKET_EMPTY_LAVA)
    }
}