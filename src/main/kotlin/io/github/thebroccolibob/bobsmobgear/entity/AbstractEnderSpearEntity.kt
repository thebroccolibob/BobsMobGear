package io.github.thebroccolibob.bobsmobgear.entity

import io.github.thebroccolibob.bobsmobgear.util.contains
import io.github.thebroccolibob.bobsmobgear.util.set
import net.minecraft.component.EnchantmentEffectComponentTypes
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

abstract class AbstractEnderSpearEntity : PersistentProjectileEntity {
    constructor(type: EntityType<out AbstractEnderSpearEntity>, world: World) : super(type, world)
    constructor(type: EntityType<out AbstractEnderSpearEntity>, owner: LivingEntity, world: World, stack: ItemStack) : super(type, owner, world, stack, null) {
        thrownSlot = (owner as? PlayerEntity)?.run {
            if (offHandStack == stack) PlayerInventory.OFF_HAND_SLOT else inventory.getSlotWithStack(stack)
        } ?: -1
    }
    constructor(type: EntityType<out AbstractEnderSpearEntity>, x: Double, y: Double, z: Double, world: World, stack: ItemStack) : super(type, x, y, z, world, stack, null)

    protected var thrownSlot: Int = -1
        private set

    val hasLoyalty get() = EnchantmentEffectComponentTypes.TRIDENT_RETURN_ACCELERATION in itemStack.enchantments

    override fun tick() {
        super.tick()
        if (world.isClient) repeat(when {
            !inGround && !hasNoGravity() -> 8
            random.nextFloat() < 0.25 -> 1
            else -> 0
        }) {
            world.addParticle(
                ParticleTypes.PORTAL,
                x,
                y,
                z,
                (random.nextDouble() - 0.5) * 1.0,
                -((random.nextDouble() - 0.5) * 1.0) - 0.5,
                (random.nextDouble() - 0.5) * 1.0
            )
        }
    }

    override fun tickInVoid() {
        if (world.isClient) return
        if (hasLoyalty)
            teleportToOwner()
        else
            super.tickInVoid()
    }

    override fun age() {
        if (pickupType != PickupPermission.ALLOWED)
            super.age()
    }

    private fun playTeleportEffect(x: Double, y: Double, z: Double, dh: Double, dy: Double, reverse: Boolean) {
        world.playSound(null, x, y, z, SoundEvents.ENTITY_PLAYER_TELEPORT, soundCategory)
        (world as? ServerWorld)?.spawnParticles(
            if (reverse) ParticleTypes.REVERSE_PORTAL else ParticleTypes.PORTAL,
            x,
            y,
            z,
            16,
            dh / 2,
            dy / 2,
            dh / 2,
            if (reverse) 0.1 else 0.2,
        )
    }

    protected fun playTeleportEffect(target: Vec3d = pos, toOwner: Boolean = false) {
        playTeleportEffect(target.x, target.y, target.z, width.toDouble(), height.toDouble(), !toOwner)
        owner?.run {
            playTeleportEffect(x, y, z, width.toDouble(), height.toDouble(), toOwner)
        }
    }

    protected fun returnToOwner(): Boolean {
        if (pickupType == PickupPermission.CREATIVE_ONLY && (owner as? PlayerEntity)?.isCreative == true) return true
        if (pickupType != PickupPermission.ALLOWED) return false
        val owner = (owner as? PlayerEntity)?.takeIf { it.isAlive } ?: return false
        val stack = asItemStack()

        if (thrownSlot == PlayerInventory.OFF_HAND_SLOT) {
            if (owner.offHandStack.isEmpty) {
                owner[Hand.OFF_HAND] = stack
                return true
            }
        } else if (thrownSlot != -1 && owner.inventory.getStack(thrownSlot).isEmpty) {
            owner.inventory.setStack(thrownSlot, stack)
            return true
        }
        return owner.giveItemStack(stack)
    }

    protected fun returnToOwnerOrDrop(hitResult: BlockHitResult? = null) {
        when {
            returnToOwner() -> discard()
            hitResult != null -> setPosition(hitResult.pos)
            pickupType != PickupPermission.ALLOWED -> {}
            else -> {
                dropStack(asItemStack())
                discard()
            }
        }
    }

    protected open fun teleportToOwner() {
        if (owner?.isAlive != true) return
        playTeleportEffect(toOwner = false)
        if (returnToOwner()) {
            discard()
            return
        }
        setPosition(owner!!.pos.add(0.0, 1.0, 0.0))
        setVelocity(0.0, 0.0, 0.0)
    }

    protected fun bounce() {
        velocity = velocity.multiply(-0.01, -0.1, -0.01)
    }
}