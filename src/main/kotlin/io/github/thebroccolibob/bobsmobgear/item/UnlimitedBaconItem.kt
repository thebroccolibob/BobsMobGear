package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.util.Translation
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.World

class UnlimitedBaconItem(settings: Settings) : Item(settings) {
    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        super.finishUsing(stack.copy(), world, user)
        if (world.isClient) return stack
        if (user is ServerPlayerEntity) {
            user.networkHandler.disconnect(KICK_REASON.text)
            return stack
        }
        if (!(user.type.isIn(ConventionalEntityTypeTags.BOSSES))) {
            user.dropStack(stack)
            user.discard()
            return ItemStack.EMPTY
        }
        return stack
    }

    companion object {
        val KICK_REASON = Translation.unit("${BobsMobGear.MOD_ID}.no_games")
    }
}