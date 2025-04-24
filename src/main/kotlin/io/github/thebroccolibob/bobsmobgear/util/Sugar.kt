package io.github.thebroccolibob.bobsmobgear.util

import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView

inline fun blockSettings(init: AbstractBlock.Settings.() -> Unit): AbstractBlock.Settings =
    AbstractBlock.Settings.create().apply(init)

fun <T: BlockEntity> BlockEntityType(factory: BlockEntityType.BlockEntityFactory<T>, vararg blocks: Block): BlockEntityType<T> =
    BlockEntityType.Builder.create(factory, *blocks).build()

operator fun LivingEntity.get(hand: Hand): ItemStack = getStackInHand(hand)
operator fun LivingEntity.get(slot: EquipmentSlot): ItemStack = getEquippedStack(slot)
operator fun BlockView.get(pos: BlockPos): BlockState = getBlockState(pos)

infix fun ItemStack.isOf(item: Item) = isOf(item)
infix fun ItemStack.isIn(tag: TagKey<Item>) = isIn(tag)
infix fun BlockState.isOf(block: Block) = isOf(block)
infix fun BlockState.isIn(tag: TagKey<Block>) = isIn(tag)
