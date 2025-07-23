package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat.CATACLYSM
import io.github.thebroccolibob.bobsmobgear.util.itemSettings
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object BobsMobGearDatagenItems {
    private val items = mutableMapOf<Identifier, Item>()

    private fun register(id: Identifier) = Item(itemSettings {}).also { items[id] = it }
    private fun register(namespace: String, path: String) = register(Identifier.of(namespace, path))

    val BLACK_STEEL_AXE     = register(CATACLYSM, "black_steel_axe")
    val BLACK_STEEL_BLOCK   = register(CATACLYSM, "black_steel_block")
    val BLACK_STEEL_HOE     = register(CATACLYSM, "black_steel_hoe")
    val BLACK_STEEL_INGOT   = register(CATACLYSM, "black_steel_ingot")
    val BLACK_STEEL_PICKAXE = register(CATACLYSM, "black_steel_pickaxe")
    val BLACK_STEEL_SHOVEL  = register(CATACLYSM, "black_steel_shovel")
    val BLACK_STEEL_SWORD   = register(CATACLYSM, "black_steel_sword")

    fun register() {
        for ((id, item) in items)
            Registry.register(Registries.ITEM, id, item)
    }
}