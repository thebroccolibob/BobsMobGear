package io.github.thebroccolibob.bobsmobgear.util

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import java.util.*

fun List<NbtElement>.toNbtList() = NbtList().apply {
    this@toNbtList.forEach(::add)
}

fun Optional<ItemStack>.orElseEmpty() = orElse(ItemStack.EMPTY)

fun NbtCompound.getList(key: String, type: Byte) = getList(key, type.toInt())

fun <T> List<T>.extend(length: Int, defaultValue: T) = this + List(length - size) { defaultValue }
fun <T> List<T>.extend(length: Int, defaultValue: () -> T) = this + List(length - size) { defaultValue() }
