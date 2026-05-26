package io.github.thebroccolibob.bobsmobgear.registry

import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import io.github.thebroccolibob.bobsmobgear.BobsMobGear

object BobsMobGearSounds {
    private fun register(id: Identifier): SoundEvent = Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id))
    private fun register(path: String) = register(BobsMobGear.id(path))

    @JvmField val TEMPLATE_HAMMER = register("block.template.hammer")
    @JvmField val TEMPLATE_ADD_ITEM = register("block.template.add_item")
    @JvmField val TEMPLATE_REMOVE_ITEM = register("block.template.remove_item")
    @JvmField val TEMPLATE_CRAFT = register("block.template.craft")
    @JvmField val FORGE_HEATER_FUEL = register("block.forge_heater.fuel")
    @JvmField val WEAPON_ATTACK_READY = register("item.weapon.attack_ready")
    @JvmField val EQUIPMENT_REPAIR = register("enchantment.equipment_repair")
    @JvmField val TONGS_PICKUP = register("item.smithing_tongs.pickup")
    @JvmField val TONGS_DROP = register("item.smithing_tongs.drop")
    @JvmField val WARDEN_SHEARED = register("entity.warden.shear")

    fun register() {}
}
