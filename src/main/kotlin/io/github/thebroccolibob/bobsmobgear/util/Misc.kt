package io.github.thebroccolibob.bobsmobgear.util

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.minecraft.component.ComponentType
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeInstance
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*
import kotlin.math.roundToInt
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
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

inline fun <T> Iterable<T>.mapToJson(transform: (T) -> JsonElement) = JsonArray().apply {
    for (item in this@mapToJson)
        add(transform(item))
}

fun getWeaponDamage(world: World?, stack: ItemStack, target: Entity, damageSource: DamageSource): Float {
    val instance = EntityAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE) {}
    instance.baseValue = 1.0
    stack.applyAttributeModifier(AttributeModifierSlot.MAINHAND) { attribute, modifier ->
        if (attribute != EntityAttributes.GENERIC_ATTACK_DAMAGE) return@applyAttributeModifier
        instance.addTemporaryModifier(modifier)
    }
    val damage = instance.value.toFloat()

    if (world !is ServerWorld) return damage

    return EnchantmentHelper.getDamage(world, stack, target, damageSource, damage)
}

fun entityProperty(getWorld: () -> World, uuidProperty: KMutableProperty0<UUID?>? = null, idProperty: KMutableProperty0<Int?>? = null) = object : ReadWriteProperty<Any?, Entity?> {
    private var entity: Entity? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): Entity? {
        val world = getWorld()

        if (world is ServerWorld) {
            if (uuidProperty != null && entity?.uuid != uuidProperty.get())
                entity = null
        } else {
            if (idProperty != null && entity?.id != idProperty.get())
                entity = null
        }

        if (entity?.isRemoved == false) return entity

        entity = if (world is ServerWorld)
            uuidProperty?.get()?.let { world.getEntity(it) }
        else
            idProperty?.get()?.let { world.getEntityById(it) }

        return entity
    }

    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: Entity?
    ) {
        entity = value
        idProperty?.set(entity?.id ?: 0)
        uuidProperty?.set(entity?.uuid)
    }
}

fun Entity.entityProperty(uuid: KMutableProperty0<UUID?>? = null, id: KMutableProperty0<Int?>? = null) = entityProperty(::getWorld, uuid, id)

fun DamageSource.toDirect() = DamageSource(typeRegistryEntry, attacker)