package io.github.thebroccolibob.bobsmobgear.entity

import io.github.thebroccolibob.bobsmobgear.util.set
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.Hand
import net.minecraft.world.World

abstract class AbstractEnderSpearEntity : PersistentProjectileEntity {
    constructor(type: EntityType<out AbstractEnderSpearEntity>, world: World) : super(type, world)
    constructor(type: EntityType<out AbstractEnderSpearEntity>, owner: LivingEntity, world: World, stack: ItemStack) : super(type, owner, world, stack, null) {
        thrownSlot = (owner as? PlayerEntity)?.run {
            if (offHandStack == stack) PlayerInventory.OFF_HAND_SLOT else inventory.getSlotWithStack(stack)
        } ?: -1
    }
    constructor(type: EntityType<out AbstractEnderSpearEntity>, x: Double, y: Double, z: Double, world: World, stack: ItemStack) : super(type, x, y, z, world, stack, null)

    private var thrownSlot: Int = -1

    override fun tick() {
        super.tick()
        if (world.isClient) repeat(4) {
            world.addParticle(
                ParticleTypes.PORTAL,
                getParticleX(0.5),
                randomBodyY,
                getParticleZ(0.5),
                (random.nextDouble() - 0.5) * 2.0,
                -random.nextDouble(),
                (random.nextDouble() - 0.5) * 2.0
            )
        }
    }

    protected fun returnToOwner(): Boolean {
        if (thrownSlot == -1) return false
        val owner = owner as? PlayerEntity ?: return false
        val stack = asItemStack()
        if (thrownSlot == PlayerInventory.OFF_HAND_SLOT || thrownSlot == owner.inventory.selectedSlot) {
            // TODO instant cooldown
        }

        if (thrownSlot == PlayerInventory.OFF_HAND_SLOT) {
            if (owner.offHandStack.isEmpty) {
                owner[Hand.OFF_HAND] = stack
                return true
            }
            return false
        }
        return owner.inventory.insertStack(thrownSlot, stack) || owner.giveItemStack(stack)
    }

    protected fun returnToOwnerOrDrop() {
        if (returnToOwner()) return
        dropStack(asItemStack())
    }
}