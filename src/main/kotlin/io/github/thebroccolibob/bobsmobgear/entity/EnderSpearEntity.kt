package io.github.thebroccolibob.bobsmobgear.entity

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearEntities
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.horizontal
import io.github.thebroccolibob.bobsmobgear.util.plus
import io.github.thebroccolibob.bobsmobgear.util.times
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import net.minecraft.world.World

class EnderSpearEntity : AbstractEnderSpearEntity {
    constructor(type: EntityType<out EnderSpearEntity>, world: World) : super(type, world)
    constructor(owner: LivingEntity, world: World, stack: ItemStack) : super(BobsMobGearEntities.ENDER_SPEAR, owner, world, stack)
    constructor(x: Double, y: Double, z: Double, world: World, stack: ItemStack) : super(BobsMobGearEntities.ENDER_SPEAR, x, y, z, world, stack)

    override fun getDefaultItemStack(): ItemStack = BobsMobGearItems.ENDER_SPEAR.defaultStack

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        val entity = entityHitResult.entity
        entity.damage(damageSources.arrow(this, owner), 8f) // TODO change damage handling and no knockback
        teleportOwnerTo(entity.pos + (entity.rotationVector.horizontal().normalize() * -(entity.width / 2 + 2.0)).add(0.0, 2.0, 0.0), entity.yaw, owner?.pitch ?: 0f)
        returnToOwnerOrDrop()
        discard()
    }

    override fun onBlockHit(blockHitResult: BlockHitResult?) {
        owner?.let { teleportOwnerTo(pos, it.yaw, it.pitch) }
        returnToOwnerOrDrop() // TODO cooldown
        discard()
    }

    private fun teleportOwnerTo(pos: Vec3d, yaw: Float, pitch: Float) {
        val world = world as? ServerWorld ?: return
        val owner = owner ?: return
        if (owner.hasVehicle())
            owner.detach()
        if (owner is ServerPlayerEntity && !owner.networkHandler.isConnectionOpen) return
        owner.teleportTo(TeleportTarget(world, pos, owner.velocity, yaw, pitch, TeleportTarget.NO_OP))
        owner.onLanding()
        if (owner is ServerPlayerEntity) owner.clearCurrentExplosion()
        world.playSound(null, x, y, z, SoundEvents.ENTITY_PLAYER_TELEPORT, owner.soundCategory, 1f, 1f)
    }
}