package io.github.thebroccolibob.bobsmobgear

import io.github.thebroccolibob.bobsmobgear.item.WardenFistItem
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.get
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.event.EntityPositionSource
import net.minecraft.world.event.GameEvent
import net.minecraft.world.event.PositionSource
import net.minecraft.world.event.Vibrations
import net.minecraft.world.event.Vibrations.ListenerData
import net.minecraft.world.event.Vibrations.VibrationListener
import net.minecraft.world.event.listener.EntityGameEventHandler

class PlayerVibrationHandler(private val player: PlayerEntity) : Vibrations, Vibrations.Callback {
    private var cooldown = 0
    private val positionSource = EntityPositionSource(player, player.standingEyeHeight)

    private val vibrationListenerData = ListenerData()
    val gameEventListener = EntityGameEventHandler(VibrationListener(this))

    override fun getRange(): Int = 8

    override fun getPositionSource(): PositionSource = positionSource

    private fun canAcceptCharge(stack: ItemStack) =
        cooldown <= 0 &&
        !player.itemCooldownManager.isCoolingDown(stack.item) &&
        stack[BobsMobGearItems.SONIC_CHARGE]?.let { it < WardenFistItem.MAX_SONIC_CHARGE } == true

    override fun accepts(
        world: ServerWorld,
        pos: BlockPos,
        event: RegistryEntry<GameEvent>,
        emitter: GameEvent.Emitter
    ): Boolean =
        !player.isDead &&
        emitter.sourceEntity?.let { it.isLiving && it != player } == true &&
        Hand.entries.any { hand -> canAcceptCharge(player[hand]) }

    override fun accept(
        world: ServerWorld,
        pos: BlockPos?,
        event: RegistryEntry<GameEvent>?,
        sourceEntity: Entity?,
        entity: Entity?,
        distance: Float
    ) {
        Hand.entries.map { player[it] }.firstOrNull(::canAcceptCharge)?.let {
            it[BobsMobGearItems.SONIC_CHARGE] = it.getOrDefault(BobsMobGearItems.SONIC_CHARGE, 0) + 1
        }
        world.playSoundFromEntity(null, player, SoundEvents.ENTITY_WARDEN_TENDRIL_CLICKS, player.soundCategory, 1f, 1f)
        cooldown = MAX_COOLDOWN
    }

    override fun getVibrationListenerData(): ListenerData = vibrationListenerData

    override fun getVibrationCallback(): Vibrations.Callback = this

    fun tick() {
        Vibrations.Ticker.tick(player.world, vibrationListenerData, this)
        if (cooldown > 0)
            cooldown--
    }

    companion object {
        const val MAX_COOLDOWN = 40
    }

    interface Holder {
        fun `bobsmobgear$getVibrationHandler`(): PlayerVibrationHandler
    }
}
