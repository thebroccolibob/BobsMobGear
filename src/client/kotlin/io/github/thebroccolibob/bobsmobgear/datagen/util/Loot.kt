package io.github.thebroccolibob.bobsmobgear.datagen.util

import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.LootNumberProvider

fun lootTableBuilder(init: LootTable.Builder.() -> Unit) = LootTable.Builder().apply(init)

fun LootTable.Builder.pool(rolls: LootNumberProvider = constant(1), bonusRolls: LootNumberProvider = constant(0), init: LootPool.Builder.() -> Unit) {
    pool(with(LootPool.Builder()) {
        init()
        rolls(rolls)
        bonusRolls(bonusRolls)
        this
    })
}

fun constant(value: Float): ConstantLootNumberProvider = ConstantLootNumberProvider.create(value)
fun constant(value: Int) = constant(value.toFloat())
