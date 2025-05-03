package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.fluid.MetalFluid
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.minecraft.fluid.Fluid
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object BobsMobGearFluids {
    private fun <T: Fluid> register(path: String, fluid: T): T =
        Registry.register(Registries.FLUID, BobsMobGear.id(path), fluid)

    private fun tagOf(id: Identifier): TagKey<Fluid> = TagKey.of(RegistryKeys.FLUID, id)
    private fun tagOf(path: String) = tagOf(BobsMobGear.id(path))

    val IRON = register("iron", MetalFluid(0xD8AF93))
    val DIAMOND = register("diamond", MetalFluid(0x20C5B5))
    val GOLD = register("gold", MetalFluid(0xffff00))
    val NETHERITE_SCRAP = register("netherite_scrap", MetalFluid(0x5D342C))
    val NETHERITE = register("netherite", MetalFluid(0x111111))

    val SCRAP_AMOUNT = FluidConstants.INGOT / 4

    val LIQUID_METALS = listOf(IRON, DIAMOND, GOLD, NETHERITE_SCRAP, NETHERITE)

    val MOLTEN_IRON_TAG = tagOf(Identifier.of("c", "molten_iron"))
    val MOLTEN_DIAMOND_TAG = tagOf(Identifier.of("c", "molten_diamond"))
    val MOLTEN_GOLD_TAG = tagOf(Identifier.of("c", "molten_gold"))
    val MOLTEN_NETHERITE_TAG = tagOf(Identifier.of("c", "molten_netherite"))

    fun register() {}
}