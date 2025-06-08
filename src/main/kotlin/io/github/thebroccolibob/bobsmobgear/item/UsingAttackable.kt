package io.github.thebroccolibob.bobsmobgear.item

import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

interface UsingAttackable {
    fun canAttackWhileUsing(stack: ItemStack, user: LivingEntity): Boolean
}
