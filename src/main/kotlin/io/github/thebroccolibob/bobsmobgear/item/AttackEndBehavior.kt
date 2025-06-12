package io.github.thebroccolibob.bobsmobgear.item

import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

interface AttackEndBehavior {
    fun onAttackEnd(player: ServerPlayerEntity, targetCount: Int, stack: ItemStack)
}