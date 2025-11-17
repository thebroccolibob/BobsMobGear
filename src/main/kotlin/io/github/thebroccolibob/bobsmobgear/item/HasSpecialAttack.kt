package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.event.ClientSpecialAttackCallback
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearComponents
import io.github.thebroccolibob.bobsmobgear.util.component1
import io.github.thebroccolibob.bobsmobgear.util.component2
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.set
import net.fabricmc.fabric.api.item.v1.FabricItem
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

interface HasSpecialAttack : AttackEndBehavior, FabricItem {
    val specialAttack: Identifier get() = Registries.ITEM.getId(this as Item).withSuffixedPath("_special")

    fun runSpecialAttack(user: PlayerEntity, hand: Hand, world: World): TypedActionResult<ItemStack> {
        val stack = user[hand]
        stack.set(BobsMobGearComponents.USING_SPECIAL_ATTACK)
        if (!world.isClient) return TypedActionResult.consume(stack)
        return TypedActionResult(ClientSpecialAttackCallback(stack, user, world, hand), stack)
    }

    override fun onAttackEnd(player: ServerPlayerEntity, targetCount: Int, stack: ItemStack) {
        stack.remove(BobsMobGearComponents.USING_SPECIAL_ATTACK)
    }

    override fun allowComponentsUpdateAnimation(
        player: PlayerEntity,
        hand: Hand,
        oldStack: ItemStack,
        newStack: ItemStack
    ): Boolean {
        if (!ItemStack.areItemsEqual(oldStack, newStack)) return true
        if (!oldStack.components.all { (type, value) -> type == BobsMobGearComponents.USING_SPECIAL_ATTACK || newStack[type] == value } || !newStack.components.all { (type, value) -> type == BobsMobGearComponents.USING_SPECIAL_ATTACK || oldStack[type] == value }) return true
        return false
    }
}

