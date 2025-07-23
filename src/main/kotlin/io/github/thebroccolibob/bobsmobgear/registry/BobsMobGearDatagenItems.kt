package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.util.itemSettings
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object BobsMobGearDatagenItems {
    private val items = mutableMapOf<Identifier, Item>()

    private fun register(id: Identifier) = Item(itemSettings {}).also { items[id] = it }
    private fun register(namespace: String, path: String) = register(Identifier.of(namespace, path))

    private const val FD = "farmersdelight"
    private const val CATA = "cataclysm"


    fun register() {
        for ((id, item) in items)
            Registry.register(Registries.ITEM, id, item)
    }
}