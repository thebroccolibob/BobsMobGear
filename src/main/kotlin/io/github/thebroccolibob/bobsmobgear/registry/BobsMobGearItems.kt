package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.item.AbstractFleshGlove
import io.github.thebroccolibob.bobsmobgear.item.FLESH_GLOVE_MATERIAL
import io.github.thebroccolibob.bobsmobgear.item.FleshGloveItem
import net.minecraft.component.ComponentType
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.item.ToolMaterials
import net.minecraft.network.codec.PacketCodec
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Rarity
import net.minecraft.util.Unit as MCUnit

object BobsMobGearItems {
    private fun register(path: String, item: Item): Item =
        Registry.register(Registries.ITEM, BobsMobGear.id(path), item)

    private fun <T> register(path: String, init: ComponentType.Builder<T>.() -> Unit): ComponentType<T> =
        Registry.register(Registries.DATA_COMPONENT_TYPE, BobsMobGear.id(path), ComponentType.builder<T>().apply(init).build())

    // ITEMS

    val SWORD_TEMPLATE = Items.register(BobsMobGearBlocks.SWORD_TEMPLATE)

    val FLESH_GLOVE = register("flesh_glove",
        AbstractFleshGlove(
            FLESH_GLOVE_MATERIAL,
            Item.Settings()
                .maxCount(1)
                .rarity(Rarity.COMMON)
        )
    )

    val IRON_FLESH_GLOVE = register("iron_flesh_glove",
        FleshGloveItem(
            ToolMaterials.IRON,
            Item.Settings()
                .maxCount(1)
                .rarity(Rarity.COMMON),
            0.3f
        )
    )

    // COMPONENTS

    val HEATED = register<MCUnit>("heated") {
        codec(MCUnit.CODEC)
        packetCodec(PacketCodec.unit(MCUnit.INSTANCE))
    }

    val SMITHING_HAMMER_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM, BobsMobGear.id("smithing_hammer"))

    fun register() {}
}


