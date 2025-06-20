package io.github.thebroccolibob.bobsmobgear.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World

fun interface ClientSpecialAttackCallback {
    operator fun invoke(stack: ItemStack, user: PlayerEntity, world: World, hand: Hand): ActionResult

    companion object : ClientSpecialAttackCallback {
        val EVENT: Event<ClientSpecialAttackCallback> = EventFactory.createArrayBacked(ClientSpecialAttackCallback::class.java) { listeners ->
            ClientSpecialAttackCallback { stack, user, world, hand ->
                listeners.firstNotNullOfOrNull { listener ->
                    listener.invoke(stack, user, world, hand).takeIf { it != ActionResult.PASS }
                } ?: ActionResult.PASS
            }
        }

        override fun invoke(stack: ItemStack, user: PlayerEntity, world: World, hand: Hand): ActionResult =
            EVENT.invoker()(stack, user, world, hand)

        fun register(listener: ClientSpecialAttackCallback) {
            EVENT.register(listener)
        }
    }
}