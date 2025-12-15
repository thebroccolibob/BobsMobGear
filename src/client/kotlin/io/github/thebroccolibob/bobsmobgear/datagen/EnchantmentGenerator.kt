package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.enchantment.RepairEquipmentEffect
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearEnchantments
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItemTags
import io.github.thebroccolibob.bobsmobgear.util.ComponentMap
import io.github.thebroccolibob.bobsmobgear.util.EnchantmentEffectEntry
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.enchantment.Enchantment
import net.minecraft.registry.*
import java.util.concurrent.CompletableFuture

class EnchantmentGenerator(
    dataOutput: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>,
) : FabricDynamicRegistryProvider(dataOutput, registriesFuture) {
    override fun getName(): String = "Enchantments"

    override fun configure(lookup: RegistryWrapper.WrapperLookup, entries: Entries) {
        val enchantments = lookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
        fun add(key: RegistryKey<Enchantment>) {
            entries.add(key, enchantments.getOrThrow(key).value())
        }

        add(BobsMobGearEnchantments.MENDER)
    }

    companion object : RegistryBuilder.BootstrapFunction<Enchantment> {
        override fun run(registerable: Registerable<Enchantment>) {
            val enchantments = registerable.getRegistryLookup(RegistryKeys.ENCHANTMENT)

            registerable.register(BobsMobGearEnchantments.MENDER, Enchantment(
                BobsMobGearEnchantments.MENDER_NAME.text,
                Enchantment.definition(
                    Registries.ITEM.getOrCreateEntryList(BobsMobGearItemTags.MENDER_ENCHANTABLE),
                    Registries.ITEM.getOrCreateEntryList(BobsMobGearItemTags.MENDER_ENCHANTABLE_PRIMARY),
                    5,
                    1,
                    Enchantment.constantCost(25),
                    Enchantment.constantCost(50),
                    8,
                    AttributeModifierSlot.MAINHAND,
                    AttributeModifierSlot.OFFHAND,
                ),
                enchantments.getOrThrow(BobsMobGearEnchantments.EXCLUSIVE_SET_MENDER),
                ComponentMap {
                    add(BobsMobGearEnchantments.REPAIR_ENTITY_EQUIPMENT, EnchantmentEffectEntry(
                        RepairEquipmentEffect(RepairEquipmentEffect.Source.DURABIILITY, 2),
                    ))
                    add(BobsMobGearEnchantments.REPAIR_HAND_EQUIPMENT, EnchantmentEffectEntry(
                        RepairEquipmentEffect(RepairEquipmentEffect.Source.XP, 1),
                    ))
                }
            ))
        }
    }
}