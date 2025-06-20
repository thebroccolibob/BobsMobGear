package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.enchantment.RepairEquipmentEffect
import io.github.thebroccolibob.bobsmobgear.util.Translation
import net.minecraft.component.ComponentType
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.effect.EnchantmentEffectEntry
import net.minecraft.enchantment.effect.EnchantmentValueEffect
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.Util.createTranslationKey

typealias ValueEntry = List<EnchantmentEffectEntry<EnchantmentValueEffect>>

object BobsMobGearEnchantments {
    private fun <T> register(id: Identifier, type: ComponentType<T>): ComponentType<T> =
        Registry.register(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, id, type)

    private fun <T> register(id: Identifier, init: ComponentType.Builder<T>.() -> Unit) =
        register(id, ComponentType.builder<T>().apply(init).build())

    private fun <T> register(path: String, init: ComponentType.Builder<T>.() -> Unit) =
        register(BobsMobGear.id(path), init)

    private fun keyOf(path: String): RegistryKey<Enchantment> = RegistryKey.of(RegistryKeys.ENCHANTMENT, BobsMobGear.id(path))

    private fun tagOf(path: String): TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, BobsMobGear.id(path))

    val REPAIR_ENTITY_EQUIPMENT = register<EnchantmentEffectEntry<RepairEquipmentEffect>>("repair_entity_equipment") {
        codec(EnchantmentEffectEntry.createCodec(RepairEquipmentEffect.CODEC, LootContextTypes.ENCHANTED_ITEM))
    }

    val REPAIR_HAND_EQUIPMENT = register<EnchantmentEffectEntry<RepairEquipmentEffect>>("repair_hand_equipment") {
        codec(EnchantmentEffectEntry.createCodec(RepairEquipmentEffect.CODEC, LootContextTypes.ENCHANTED_ITEM))
    }

    val MENDER = keyOf("mender")
    val MENDER_NAME = Translation.unit(createTranslationKey("enchantment", MENDER.value))

    val EXCLUSIVE_SET_MENDER = tagOf("exclusive_set/mender")

    fun register() {
        RepairEquipmentEffect.register()
    }
}