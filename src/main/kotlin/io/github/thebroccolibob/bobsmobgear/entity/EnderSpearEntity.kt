package io.github.thebroccolibob.bobsmobgear.entity

import io.github.thebroccolibob.bobsmobgear.item.EnderSpearItem
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearDamageTypes
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearEntities
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.*
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.EndermanEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
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

    private var teleported = false

    override fun getDefaultItemStack(): ItemStack = BobsMobGearItems.IRON_ENDER_SPEAR.defaultStack

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putBoolean("teleported", teleported)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        teleported = nbt.getBoolean("teleported")
    }

    private fun applySelfDamage() {
        (itemStack.item as? EnderSpearItem)?.selfDamage?.let {
            owner?.damage(damageSources.create(BobsMobGearDamageTypes.SELF_TELEFRAG, null, null), it)
        }
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        if (teleported) return
        val entity = entityHitResult.entity
        val stack = itemStack
        val damageSource = damageSources.create(BobsMobGearDamageTypes.PROJECTILE_TELEFRAG, this, owner)
        val damage = getWeaponDamage(world, stack, entity, damageSource)
        val offset = pos - entity.pos

        playSound(hitSound, 1f, 1f)
        if (entity.damage(damageSource, damage)) {
            (world as? ServerWorld)?.let {
                EnchantmentHelper.onTargetDamaged(it, entity, damageSource, stack)
            }
        }
        if (entity is EndermanEntity) {
            entity.damage(damageSources.create(BobsMobGearDamageTypes.BASE_TELEFRAG, this, owner), damage)
            if (hasLoyalty) {
                teleportToOwner()
            } else {
                setPosition(entity.pos + offset)
                setVelocity(0.0, 0.0, 0.0)
            }
            teleported = true
            return
        }

        // TODO instant attack reset?
        teleportOwnerTo(entity.pos + (entity.rotationVector.horizontal().normalize() * -(entity.width / 2 + 2.0)).add(0.0, 2.0, 0.0), entity.yaw, owner?.pitch ?: 0f)
        returnToOwnerOrDrop()
        velocity = velocity.multiply(-0.01, -0.1, -0.01)
        applySelfDamage()
        teleported = true
    }

    override fun onBlockHit(blockHitResult: BlockHitResult?) {
        if (teleported) {
            super.onBlockHit(blockHitResult)
            return
        }
        owner?.let { teleportOwnerTo(pos, it.yaw, it.pitch) }
        teleported = true
        if (returnToOwner())
            discard()
        else
            super.onBlockHit(blockHitResult)
        applySelfDamage()
    }

    override fun teleportToOwner() {
        super.teleportToOwner()
        teleported = true
    }

    private fun teleportOwnerTo(pos: Vec3d, yaw: Float, pitch: Float) {
        val world = world as? ServerWorld ?: return
        val owner = owner ?: return
        playTeleportEffect(pos, true)
        if (owner.hasVehicle())
            owner.detach()
        if (owner is ServerPlayerEntity && !owner.networkHandler.isConnectionOpen) return
        owner.teleportTo(TeleportTarget(world, pos, owner.velocity, yaw, pitch, TeleportTarget.NO_OP))
        owner.onLanding()
        if (owner is ServerPlayerEntity) owner.clearCurrentExplosion()
        world.playSound(null, x, y, z, SoundEvents.ENTITY_PLAYER_TELEPORT, owner.soundCategory, 1f, 1f)
    }
}