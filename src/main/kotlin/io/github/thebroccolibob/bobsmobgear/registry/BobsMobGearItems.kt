package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

object BobsMobGearItems {
    private fun register(path: String, item: Item): Item =
        Registry.register(Registries.ITEM, BobsMobGear.id(path), item)

    val SWORD_TEMPLATE = Items.register(BobsMobGearBlocks.SWORD_TEMPLATE)

    val SMITHING_HAMMER_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM, BobsMobGear.id("smithing_hammer"))

    fun register() {}
}
