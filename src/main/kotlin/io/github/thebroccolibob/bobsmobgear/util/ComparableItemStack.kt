package io.github.thebroccolibob.bobsmobgear.util

import com.mojang.serialization.Codec
import net.minecraft.component.ComponentType
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec

class ComparableItemStack(val stack: ItemStack) {
    override fun hashCode(): Int {
        return ItemStack.hashCode(stack)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ComparableItemStack

        return ItemStack.areEqual(stack, other.stack)
    }

    val isEmpty get() = stack.isEmpty

    companion object {
        @JvmField val EMPTY = ComparableItemStack(ItemStack.EMPTY)

        val CODEC: Codec<ComparableItemStack> = ItemStack.CODEC.xmap(::ComparableItemStack, ComparableItemStack::stack)
        val PACKET_CODEC: PacketCodec<RegistryByteBuf, ComparableItemStack> = ItemStack.PACKET_CODEC.xmap(::ComparableItemStack, ComparableItemStack::stack)
    }
}

operator fun ItemStack.set(type: ComponentType<ComparableItemStack>, stack: ItemStack) =
    set(type, ComparableItemStack(stack))