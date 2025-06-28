package io.github.thebroccolibob.bobsmobgear.util

import net.minecraft.component.ComponentType
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.util.Hand
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import java.util.*
import kotlin.Unit
import kotlin.math.roundToInt
import kotlin.reflect.KProperty
import net.minecraft.util.Unit as MCUnit

fun List<NbtElement>.toNbtList() = NbtList().apply {
    this@toNbtList.forEach(::add)
}

inline fun <reified T> Collection<T>.toDefaultedList(defaultValue: T): DefaultedList<T> =
    DefaultedList.copyOf(defaultValue, *toTypedArray())

fun Collection<ItemStack>.toDefaultedList() = toDefaultedList(ItemStack.EMPTY)

fun <T: Any> T?.toOptional() = Optional.ofNullable(this)
fun Optional<ItemStack>.orElseEmpty(): ItemStack = orElse(ItemStack.EMPTY)

fun NbtCompound.getList(key: String, type: Byte): NbtList = getList(key, type.toInt())

fun <T> List<T>.extend(length: Int, defaultValue: T) = this + List(length - size) { defaultValue }
fun <T> List<T>.extend(length: Int, defaultValue: () -> T) = this + List(length - size) { defaultValue() }

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

val Hand.opposite get() = when (this) {
    Hand.MAIN_HAND -> Hand.OFF_HAND
    Hand.OFF_HAND -> Hand.MAIN_HAND
}

// Makes it so 0 -> 0, 1 -> 1, max - 1 -> barWidth - 1, max -> barWidth
fun getBarProgress(value: Int, max: Int, barWidth: Int) = when(value) {
    0 -> 0
    max -> barWidth
    else -> (value - 1) * (barWidth - 2) / (max - 2) + 1
}

fun <T> Iterable<T>.countUnique(): Map<T, Int> {
    val counts = mutableMapOf<T, Int>()
    for (element in this) {
        val key = counts.keys.firstOrNull { it == element } ?: element
        counts[key] = counts.getOrDefault(key, 0) + 1
    }
    return counts
}

fun <T: Any, R> Iterable<T>.groupConsecutive(create: (Int, T) -> R): List<R> = buildList {
    var current: T? = null
    var currentCount = 0
    for (element in this@groupConsecutive) {
        if (current == element) {
            currentCount++
        } else {
            if (current != null)
                add(create(currentCount, current))
            current = element
            currentCount = 1
        }
    }
    if (current != null)
        add(create(currentCount, current))
}

operator fun <T> TrackedData<T>.getValue(thisRef: Entity, property: KProperty<*>): T = thisRef.dataTracker.get(this)
operator fun <T> TrackedData<T>.setValue(thisRef: Entity, property: KProperty<*>, value: T) {
    thisRef.dataTracker.set(this, value)
}

operator fun TrackedData<OptionalInt>.getValue(thisRef: Entity, property: KProperty<*>): Int? = thisRef.dataTracker.get(this).let { if (it.isPresent) it.asInt else null }
operator fun TrackedData<OptionalInt>.setValue(thisRef: Entity, property: KProperty<*>, value: Int?) {
    thisRef.dataTracker.set(this, if (value == null) OptionalInt.empty() else OptionalInt.of(value))
}

fun ItemStack.damage(amount: Int, entity: LivingEntity, hand: Hand) {
    damage(amount, entity, LivingEntity.getSlotForHand(hand))
}

fun Vec3d.horizontal(): Vec3d = multiply(1.0, 0.0, 1.0)

inline fun AttributeModifiersComponent.withModified(shouldInclude: (AttributeModifiersComponent.Entry) -> Boolean = { true }, block: AttributeModifiersComponent.Builder.() -> Unit): AttributeModifiersComponent =
    AttributeModifiersComponent.builder().apply {
        for (entry in modifiers) {
            if (shouldInclude(entry))
                add(entry.attribute, entry.modifier, entry.slot)
        }
        block()
    }.build()

inline fun AttributeModifiersComponent.withRemoved(predicate: (AttributeModifiersComponent.Entry) -> Boolean): AttributeModifiersComponent =
    withModified({ !predicate(it) }) {}

inline fun <T> ItemStack.modify(componentType: ComponentType<T>, block: (T?) -> T?) {
    this[componentType] = block(this[componentType])
}

val PlayerEntity.experienceProgressPoints
    get() = ((experienceProgress + 0.0001f) * nextLevelExperience).toInt()

fun PlayerEntity.takeExperiencePoints(max: Int): Int {
    // The player has no way to query the total current xp (totalExperience is not decreased by removing levels) so
    // this funky loop-based method must be done
    var consumed = 0
    while (consumed < max) {
        if (experienceLevel == 0 && experienceProgressPoints == 0) break

        val amount = (
            if (experienceProgressPoints == 0) run {
                experienceProgress = 0f
                addExperience(-1)
                nextLevelExperience.also {
                    addExperience(1)
                }
            }
            else experienceProgressPoints
        ).coerceAtMost(max - consumed)

        if (amount <= 0) break

        consumed += amount
        addExperience(-consumed)
    }
    return consumed
}