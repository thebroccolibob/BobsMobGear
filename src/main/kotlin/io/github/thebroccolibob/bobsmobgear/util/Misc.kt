package io.github.thebroccolibob.bobsmobgear.util

import net.minecraft.component.ComponentType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.util.Hand
import net.minecraft.util.math.Direction
import java.util.*
import kotlin.math.roundToInt
import net.minecraft.util.Unit as MCUnit

fun List<NbtElement>.toNbtList() = NbtList().apply {
    this@toNbtList.forEach(::add)
}

fun <T: Any> T?.toOptional() = Optional.ofNullable(this)
fun Optional<ItemStack>.orElseEmpty(): ItemStack = orElse(ItemStack.EMPTY)

fun NbtCompound.getList(key: String, type: Byte): NbtList = getList(key, type.toInt())

fun <T> List<T>.extend(length: Int, defaultValue: T) = this + List(length - size) { defaultValue }
fun <T> List<T>.extend(length: Int, defaultValue: () -> T) = this + List(length - size) { defaultValue() }

fun Hand.toEquipmentSlot(): EquipmentSlot = when(this) {
    Hand.OFF_HAND -> EquipmentSlot.OFFHAND
    Hand.MAIN_HAND -> EquipmentSlot.MAINHAND
}

fun Number.isWhole(): Boolean = when (this) {
    is Byte, is Short, is Int, is Long -> true
    is Float -> this == roundToInt().toFloat()
    is Double -> this == roundToInt().toDouble()
    else -> toDouble().isWhole()
}

fun ItemStack.set(component: ComponentType<MCUnit>) {
    this[component] = MCUnit.INSTANCE
}

inline val Direction.isHorizontal get() = horizontal != -1