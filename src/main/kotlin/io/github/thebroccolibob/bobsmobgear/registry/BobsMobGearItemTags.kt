package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object BobsMobGearItemTags {
    private fun of(id: Identifier): TagKey<Item> = TagKey.of(RegistryKeys.ITEM, id)
    private fun of(path: String) = of(BobsMobGear.id(path))

    val FORGES_IRON_INGOT = of("forges/iron_ingot")
    val FORGES_DIAMOND = of("forges/diamond")
    val FORGES_GOLD_INGOT = of("forges/gold_ingot")
    val FORGES_NETHERITE_SCRAP = of("forges/netherite_scrap")
    val FORGES_NETHERITE_INGOT = of("forges/netherite_ingot")

    val SMITHING_HAMMERS = of("smithing_hammers")
    val TONG_HOLDABLE = of("tong_holdable")
    val MENDER_ENCHANTABLE = of("enchantable/mender")

    @JvmField
    val PREVENT_SMITHING_TABLE_SCREEN = of("prevent_smithing_table_screen")
}