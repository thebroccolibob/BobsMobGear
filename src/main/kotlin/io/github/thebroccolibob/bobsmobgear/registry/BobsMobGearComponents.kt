package io.github.thebroccolibob.bobsmobgear.registry

import com.mojang.serialization.Codec
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.util.ComparableItemStack
import net.minecraft.component.ComponentType
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Unit as MCUnit

object BobsMobGearComponents {

    private fun <T> register(path: String, init: ComponentType.Builder<T>.() -> Unit): ComponentType<T> =
        Registry.register(Registries.DATA_COMPONENT_TYPE, BobsMobGear.id(path), ComponentType.builder<T>().apply(init).build())

    private fun registerUnit(path: String): ComponentType<MCUnit> = register(path) {
        codec(MCUnit.CODEC)
        packetCodec(PacketCodec.unit(MCUnit.INSTANCE))
    }

    @JvmField
    val HEATED = registerUnit("heated")

    val TONGS_HELD_ITEM = register<ComparableItemStack>("tongs_held_item") {
        codec(ComparableItemStack.CODEC)
        packetCodec(ComparableItemStack.PACKET_CODEC)
    }

    val MAX_SONIC_CHARGE = register<Int>("max_sonic_charge") {
        codec(Codec.INT)
        packetCodec(PacketCodecs.INTEGER)
    }

    val SONIC_CHARGE = register<Int>("sonic_charge") {
        codec(Codec.INT)
        packetCodec(PacketCodecs.INTEGER)
    }

    @JvmField
    val USING_SPECIAL_ATTACK = registerUnit("using_special_attack")

    fun register() {}
}