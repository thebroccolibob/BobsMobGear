package io.github.thebroccolibob.bobsmobgear.item

import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

interface UsingAttackable : AttackEndBehavior {
    fun canAttackWhileUsing(stack: ItemStack, user: LivingEntity): Boolean

    override fun onAttackEnd(player: ServerPlayerEntity, targetCount: Int, stack: ItemStack) {
        if (player.activeItem == stack)
            player.stopUsingItem()
    }
}
