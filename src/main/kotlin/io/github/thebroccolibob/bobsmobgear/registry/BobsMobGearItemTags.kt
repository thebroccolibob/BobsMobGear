package io.github.thebroccolibob.bobsmobgear.registry

import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import io.github.thebroccolibob.bobsmobgear.BobsMobGear

object BobsMobGearItemTags {
    private fun of(id: Identifier): TagKey<Item> = TagKey.of(RegistryKeys.ITEM, id)
    private fun of(path: String) = of(BobsMobGear.id(path))

    @JvmField val FORGES_IRON_INGOT = of("forges/iron_ingot")
    @JvmField val FORGES_DIAMOND = of("forges/diamond")
    @JvmField val FORGES_GOLD_INGOT = of("forges/gold_ingot")
    @JvmField val FORGES_NETHERITE_SCRAP = of("forges/netherite_scrap")
    @JvmField val FORGES_NETHERITE_INGOT = of("forges/netherite_ingot")
    @JvmField val FORGES_BLACK_STEEL_INGOT = of("forges/cataclysm/black_steel_ingot")

    @JvmField val HAMMERS = of("hammers")
    @JvmField val SMITHING_HAMMERS = of("smithing_hammers")
    @JvmField val TONG_HOLDABLE = of("tong_holdable")
    @JvmField val TONG_HOLDABLE_WEAPONS = of("tong_holdable_weapons")
    @JvmField val MENDER_ENCHANTABLE = of("enchantable/mender")
    @JvmField val MENDER_ENCHANTABLE_PRIMARY = of("enchantable/mender_primary")
    @JvmField val SMITHING_SURFACE = of(BobsMobGearBlocks.SMITHING_SURFACE.id)
    @JvmField val NON_FORGE_FUEL = of("non_forge_fuel")
    @JvmField val LOWER_USE_PRIORITY = of("lower_use_priority")
    @JvmField val PREVENT_SMITHING_TABLE_SCREEN = of("prevent_smithing_table_screen")
    @JvmField val NOT_WEAPON = of("not_weapon")
    @JvmField val WEAK_HEAT_SOURCES = of(BobsMobGearBlocks.WEAK_HEAT_SOURCES.id)
}
