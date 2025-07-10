package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.entity.AbstractEnderSpearEntity
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearSounds
import io.github.thebroccolibob.bobsmobgear.util.damage
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.value
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.world.World

class EnderSpearItem(val selfDamage: Float, private val cooldown: Int, private val createEntity: (LivingEntity, World, ItemStack) -> AbstractEnderSpearEntity, material: ToolMaterial, settings: Settings) :
    ToolItem(material, settings.apply {
        attributeModifiers(SwordItem.createAttributeModifiers(material, 4, -2.8f))
    }) {

    override fun use(world: World?, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        user.setCurrentHand(hand)
        return TypedActionResult.consume(user[hand])
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (user.itemUseTime < USE_TIME) return
        if (world.isClient) return
        if (stack.maxDamage - stack.damage == 1) return
        stack.damage(1, user, user.activeHand)
        createEntity(user, world, stack).apply {
            setVelocity(user, user.pitch, user.yaw, 0f, 2.5f, 1f)
            if (user is PlayerEntity && user.isInCreativeMode)
                pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY
        }.also {
            world.spawnEntity(it)
            world.playSoundFromEntity(null, it, SoundEvents.ITEM_TRIDENT_THROW.value, user.soundCategory, 1f, 1f)
        }
        if (user is PlayerEntity && !user.isInCreativeMode)
            user.inventory.removeOne(stack)
        (user as? PlayerEntity)?.itemCooldownManager?.set(this, cooldown)
    }

    override fun getUseAction(stack: ItemStack?): UseAction = UseAction.SPEAR

    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity?): Int = 72000

    override fun usageTick(world: World, user: LivingEntity, stack: ItemStack?, remainingUseTicks: Int) {
        if (world.isClient && user.itemUseTime == USE_TIME)
            user.playSound(BobsMobGearSounds.WEAPON_ATTACK_READY)
        super.usageTick(world, user, stack, remainingUseTicks)
    }

    companion object {
        const val USE_TIME = 20
    }
}