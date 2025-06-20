package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.world.event.GameEvent

object BobsMobGearGameEvents {
    private fun register(path: String, event: GameEvent): GameEvent =
        Registry.register(Registries.GAME_EVENT, BobsMobGear.id(path), event)

    private fun register(path: String, range: Int = 16) = register(path, GameEvent(range))

    private fun tagOf(path: String): TagKey<GameEvent> = TagKey.of(RegistryKeys.GAME_EVENT, BobsMobGear.id(path))

    val CHARGES_WARDEN_FIST = tagOf("charges_warden_fist")
    val SUPER_CHARGES_WARDEN_FIST = tagOf("super_charges_warden_fist")

    fun register() {}
}