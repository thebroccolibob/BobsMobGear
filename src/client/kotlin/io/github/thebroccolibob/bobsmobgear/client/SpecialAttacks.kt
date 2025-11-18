package io.github.thebroccolibob.bobsmobgear.client

import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat
import io.github.thebroccolibob.bobsmobgear.client.duck.TriggersAttack
import io.github.thebroccolibob.bobsmobgear.event.ClientSpecialAttackCallback
import io.github.thebroccolibob.bobsmobgear.item.HasSpecialAttack
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World

object SpecialAttacks : ClientSpecialAttackCallback {

    override fun invoke(stack: ItemStack, user: PlayerEntity, world: World, hand: Hand): ActionResult {
        if (user != MinecraftClient.getInstance().player) return ActionResult.PASS
        if (stack.item !is HasSpecialAttack) return ActionResult.PASS
        val client = MinecraftClient.getInstance()
        (client as TriggersAttack).`bobsmobgear$startAttack`()
        return ActionResult.CONSUME
    }

    fun register() {
        if (BobsMobGearCompat.BETTER_COMBAT_INSTALLED)
            ClientSpecialAttackCallback.EVENT.register(this)
    }
}
