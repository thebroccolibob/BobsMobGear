package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.enchantment.RepairEquipmentEffect
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearEnchantments
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItemTags
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.ComponentMap
import io.github.thebroccolibob.bobsmobgear.util.EnchantmentEffectEntry
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.enchantment.Enchantment
import net.minecraft.registry.*
import net.minecraft.registry.entry.RegistryEntryList
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class EnchantmentGenerator(
    dataOutput: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>,
) : FabricCodecDataProvider<Enchantment>(dataOutput, registriesFuture, RegistryKeys.ENCHANTMENT, Enchantment.CODEC) {
    override fun getName(): String = "Enchantments"

    override fun configure(provider: BiConsumer<Identifier, Enchantment>, lookup: RegistryWrapper.WrapperLookup) {
        val enchantments = lookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
        fun provide(key: RegistryKey<Enchantment>) {
            provider.accept(key.value, enchantments.getOrThrow(key).value())
        }

        provide(BobsMobGearEnchantments.MENDER)
    }

    companion object : RegistryBuilder.BootstrapFunction<Enchantment> {
        override fun run(registerable: Registerable<Enchantment>) {
            val enchantments = registerable.getRegistryLookup(RegistryKeys.ENCHANTMENT)

            registerable.register(BobsMobGearEnchantments.MENDER, Enchantment(
                BobsMobGearEnchantments.MENDER_NAME.text,
                Enchantment.definition(
                    Registries.ITEM.getOrCreateEntryList(BobsMobGearItemTags.MENDER_ENCHANTABLE),
                    RegistryEntryList.of(Registries.ITEM.getEntry(BobsMobGearItems.SMITHING_HAMMER)),
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
                        RepairEquipmentEffect(RepairEquipmentEffect.Source.XP, 4),
                    ))
                }
            ))
        }
    }
}