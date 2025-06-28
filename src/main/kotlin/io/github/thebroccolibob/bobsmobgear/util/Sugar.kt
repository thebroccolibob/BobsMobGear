package io.github.thebroccolibob.bobsmobgear.util

import com.google.common.collect.HashMultimap
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.component.Component
import net.minecraft.component.ComponentMap
import net.minecraft.component.ComponentType
import net.minecraft.component.type.ItemEnchantmentsComponent
import net.minecraft.enchantment.effect.EnchantmentEffectEntry
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.FluidState
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolMaterial
import net.minecraft.loot.condition.LootCondition
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.entry.RegistryEntryList
import net.minecraft.registry.tag.TagKey
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

inline fun blockSettings(init: AbstractBlock.Settings.() -> Unit): AbstractBlock.Settings =
    AbstractBlock.Settings.create().apply(init)

inline fun itemSettings(init: Item.Settings.() -> Unit): Item.Settings =
    Item.Settings().apply(init)

fun <T: BlockEntity> BlockEntityType(factory: BlockEntityType.BlockEntityFactory<T>, vararg blocks: Block): BlockEntityType<T> =
    BlockEntityType.Builder.create(factory, *blocks).build()

fun ToolMaterial(
    attackDamage: Float,
    durability: Int,
    enchantability: Int,
    inverseTag: TagKey<Block>,
    miningSpeedMultiplier: Float,
    repairIngredient: Ingredient,
) = object : ToolMaterial {
    override fun getAttackDamage(): Float = attackDamage
    override fun getDurability(): Int = durability
    override fun getEnchantability(): Int = enchantability
    override fun getInverseTag(): TagKey<Block> = inverseTag
    override fun getMiningSpeedMultiplier(): Float = miningSpeedMultiplier
    override fun getRepairIngredient(): Ingredient = repairIngredient
}

operator fun LivingEntity.get(hand: Hand): ItemStack = getStackInHand(hand)
operator fun LivingEntity.set(hand: Hand, stack: ItemStack) {
    setStackInHand(hand, stack)
}
operator fun LivingEntity.get(slot: EquipmentSlot): ItemStack = getEquippedStack(slot)
operator fun BlockView.get(pos: BlockPos): BlockState = getBlockState(pos)
operator fun World.set(pos: BlockPos, state: BlockState) {
    setBlockState(pos, state)
}

infix fun ItemStack.isOf(item: Item) = isOf(item)
infix fun ItemStack.isIn(tag: TagKey<Item>) = isIn(tag)
infix fun BlockState.isOf(block: Block) = isOf(block)
infix fun BlockState.isIn(tag: TagKey<Block>) = isIn(tag)
infix fun BlockState.isIn(entryList: RegistryEntryList<Block>) = isIn(entryList)
infix fun FluidState.isIn(tag: TagKey<Fluid>) = isIn(tag)
infix fun <T> RegistryEntry<T>.isIn(tag: TagKey<T>) = isIn(tag)

operator fun Identifier.plus(suffix: String): Identifier = withSuffixedPath(suffix)

operator fun Vec3i.minus(other: Vec3i): Vec3i = subtract(other)
operator fun BlockPos.minus(other: Vec3i): BlockPos = subtract(other)
operator fun Vec3i.plus(other: Vec3i): Vec3i = add(other)
operator fun BlockPos.plus(other: Vec3i): BlockPos = add(other)

operator fun Vec3d.plus(other: Vec3d): Vec3d = add(other)
operator fun Vec3d.minus(other: Vec3d): Vec3d = subtract(other)
operator fun Vec3d.times(scalar: Double): Vec3d = multiply(scalar)
operator fun Vec3d.times(other: Vec3d): Vec3d = multiply(other)
operator fun Vec3d.div(scalar: Double): Vec3d = multiply(1 / scalar)

operator fun Vec3d.component1(): Double = x
operator fun Vec3d.component2(): Double = y
operator fun Vec3d.component3(): Double = z

operator fun MutableText.plus(other: Text): MutableText = append(other)

fun <T> EnchantmentEffectEntry(
    `object`: T,
    condition: LootCondition? = null
) = EnchantmentEffectEntry(`object`, Optional.ofNullable(condition))

fun ComponentMap(init: ComponentMap.Builder.() -> Unit): ComponentMap = ComponentMap.builder().apply(init).build()

operator fun <T> Component<T>.component1(): ComponentType<T> = type
operator fun <T> Component<T>.component2(): T = value

fun <K, V> multimapOf(vararg entries: Pair<K, V>): HashMultimap<K, V> = HashMultimap.create<K, V>().apply {
    for ((key, value) in entries)
        put(key, value)
}

inline val <T> RegistryEntry<T>.value: T get() = value()

operator fun <T> RegistryEntry<T>.component1(): RegistryKey<T> = key.orElseThrow()
operator fun <T> RegistryEntry<T>.component2(): T = value()

/**
 * Should behave the same as [EnchantmentHelper.hasAnyEnchantmentsWith][net.minecraft.enchantment.EnchantmentHelper.hasAnyEnchantmentsWith]
 */
operator fun ItemEnchantmentsComponent.contains(type: ComponentType<*>) =
    enchantmentEntries.any { (enchantment, _) -> type in enchantment.value.effects }