package io.github.thebroccolibob.bobsmobgear

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.network.ServerPlayerEntity
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
import io.github.thebroccolibob.bobsmobgear.network.DetectedEntityPayload
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearComponents
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearGameEvents
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.isIn

class PlayerVibrationHandler(private val player: PlayerEntity) : Vibrations, Vibrations.Callback {
    private var cooldown = 0
    private val positionSource = EntityPositionSource(player, player.height / 2)

    private val vibrationListenerData = ListenerData()
    val gameEventListener = EntityGameEventHandler(VibrationListener(this))

    override fun getRange(): Int = 8

    override fun getPositionSource(): PositionSource = positionSource

    private fun canAcceptCharge(stack: ItemStack) =
        cooldown <= 0 &&
        !player.itemCooldownManager.isCoolingDown(stack.item) &&
        stack[BobsMobGearComponents.MAX_SONIC_CHARGE]?.let { (stack[BobsMobGearComponents.SONIC_CHARGE] ?: 0) < it } == true

    override fun getTag(): TagKey<GameEvent> = BobsMobGearGameEvents.CHARGES_WARDEN_FIST

    override fun accepts(
        world: ServerWorld,
        pos: BlockPos,
        event: RegistryEntry<GameEvent>,
        emitter: GameEvent.Emitter
    ): Boolean =
        !player.isDead &&
        (event isIn BobsMobGearGameEvents.SUPER_CHARGES_WARDEN_FIST || emitter.sourceEntity?.let { it.isLiving && it != player } == true) &&
        Hand.entries.any { hand -> canAcceptCharge(player[hand]) }

    override fun accept(
        world: ServerWorld,
        pos: BlockPos?,
        event: RegistryEntry<GameEvent>,
        sourceEntity: Entity?,
        entity: Entity?,
        distance: Float
    ) {
        val full = Hand.entries
            .map { player[it] }
            .firstOrNull(::canAcceptCharge)
            ?.let { stack ->
                ((stack[BobsMobGearComponents.SONIC_CHARGE] ?: 0) +
                        if (event isIn BobsMobGearGameEvents.SUPER_CHARGES_WARDEN_FIST) SUPER_CHARGE_INCREASE else 1
                ).also {
                    stack[BobsMobGearComponents.SONIC_CHARGE] = it
                } == stack[BobsMobGearComponents.MAX_SONIC_CHARGE]
            } == true
        world.playSoundFromEntity(null, player, if (full) SoundEvents.ENTITY_WARDEN_NEARBY_CLOSEST else SoundEvents.ENTITY_WARDEN_TENDRIL_CLICKS, player.soundCategory, 1f, 1f)
        if (sourceEntity != null)
            ServerPlayNetworking.send(player as ServerPlayerEntity, DetectedEntityPayload(sourceEntity))
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

        const val SUPER_CHARGE_INCREASE = 4
    }

    interface Holder {
        fun `bobsmobgear$getVibrationHandler`(): PlayerVibrationHandler
    }
}
