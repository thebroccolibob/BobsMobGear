package io.github.thebroccolibob.bobsmobgear.util

import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType

inline fun blockSettings(init: AbstractBlock.Settings.() -> Unit): AbstractBlock.Settings =
    AbstractBlock.Settings.create().apply(init)

fun <T: BlockEntity> BlockEntityType(factory: BlockEntityType.BlockEntityFactory<T>, vararg blocks: Block): BlockEntityType<T> =
    BlockEntityType.Builder.create(factory, *blocks).build()
