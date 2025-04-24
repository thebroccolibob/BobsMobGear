package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier

object BobsMobGearSounds {
    private fun register(id: Identifier): SoundEvent = Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id))
    private fun register(path: String) = register(BobsMobGear.id(path))

    val TEMPLATE_HAMMER = register("block.template.hammer")
    val TEMPLATE_ADD_ITEM = register("block.template.add_item")
    val TEMPLATE_REMOVE_ITEM = register("block.template.remove_item")
    val TEMPLATE_CRAFT = register("block.template.craft")

    fun register() {}
}
