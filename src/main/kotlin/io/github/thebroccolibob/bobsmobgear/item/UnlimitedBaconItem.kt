package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.util.Translation
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.set
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.World

class UnlimitedBaconItem(settings: Settings) : Item(settings) {
    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        super.finishUsing(stack.copy(), world, user)
        if (world.isClient) return stack
        if (user is ServerPlayerEntity) {
            user[KICK_TICKS] = MAX_KICK_TICKS
            return stack
        }
        if (!(user.type.isIn(ConventionalEntityTypeTags.BOSSES))) {
            user.dropStack(stack)
            user.discard()
            return ItemStack.EMPTY
        }
        return stack
    }

    @Suppress("UnstableApiUsage")
    companion object : ServerTickEvents.EndWorldTick {
        val KICK_REASON = Translation.unit("${BobsMobGear.MOD_ID}.no_games")
        val KICK_TICKS: AttachmentType<Int> = AttachmentRegistry.create(BobsMobGear.id("kick_ticks"))
        const val MAX_KICK_TICKS = 5

        override fun onEndTick(world: ServerWorld) {
            for (player in world.players.toList()) { // copy needed to prevent concurrent modification
                val kickTicks = player[KICK_TICKS] ?: continue
                if (kickTicks <= 0)
                    player.networkHandler.disconnect(KICK_REASON.text)
                else
                    player[KICK_TICKS] = kickTicks - 1
            }
        }

        fun register() {
            ServerTickEvents.END_WORLD_TICK.register(this)
        }
    }
}
