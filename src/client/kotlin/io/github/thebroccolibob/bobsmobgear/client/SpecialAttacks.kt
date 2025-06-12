package io.github.thebroccolibob.bobsmobgear.client

import io.github.thebroccolibob.bobsmobgear.client.duck.TriggersAttack
import io.github.thebroccolibob.bobsmobgear.event.ClientSpecialAttackCallback
import io.github.thebroccolibob.bobsmobgear.item.HasSpecialAttack
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import net.bettercombat.api.AttackHand
import net.bettercombat.api.client.BetterCombatClientEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World

object SpecialAttacks : ClientSpecialAttackCallback {

    override fun invoke(stack: ItemStack, user: PlayerEntity, world: World, hand: Hand): ActionResult {
        val item = stack.item
        if (item !is HasSpecialAttack) return ActionResult.PASS
        val client = MinecraftClient.getInstance()
        (client as TriggersAttack).`bobsmobgear$startAttack`()
        return ActionResult.CONSUME
    }

    fun register() {
        ClientSpecialAttackCallback.EVENT.register(this)
    }
}
