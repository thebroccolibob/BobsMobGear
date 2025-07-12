package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.entity.AbstractEnderSpearEntity
import io.github.thebroccolibob.bobsmobgear.entity.EnderEyeSpearEntity
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearSounds
import io.github.thebroccolibob.bobsmobgear.util.damage
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.value
import net.fabricmc.fabric.api.item.v1.EnchantingContext
import net.minecraft.block.BlockState
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class EnderSpearItem(
    val selfDamage: Float,
    private val cooldown: Int,
    private val baseDamage: Int,
    private val attackSpeed: Float,
    private val maxRange: Double,
    private val createEntity: (LivingEntity, World, ItemStack) -> AbstractEnderSpearEntity,
    material: ToolMaterial,
    settings: Settings
) : ToolItem(material, settings.apply {
    attributeModifiers(SwordItem.createAttributeModifiers(material, baseDamage, attackSpeed))
}) {

    override fun use(world: World?, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user[hand]
        if (stack.maxDamage - stack.damage == 1) return TypedActionResult.fail(stack)
        user.setCurrentHand(hand)
        return TypedActionResult.consume(stack)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (user.itemUseTime < USE_TIME) return
        if (world.isClient) return
        stack.damage(1, user, user.activeHand)
        createEntity(user, world, stack).apply {
            setVelocity(user, user.pitch, user.yaw, 0f, 2.5f, 1f)
            if (user is PlayerEntity && user.isInCreativeMode)
                pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY
            if (this is EnderEyeSpearEntity)
                maxRange = this@EnderSpearItem.maxRange
        }.also {
            world.spawnEntity(it)
            world.playSoundFromEntity(null, it, SoundEvents.ITEM_TRIDENT_THROW.value, user.soundCategory, 1f, 1f)
        }
        if (user is PlayerEntity && !user.isInCreativeMode)
            user.inventory.removeOne(stack)
        if (cooldown > 0)
            (user as? PlayerEntity)?.itemCooldownManager?.set(this, cooldown)
    }

    override fun getUseAction(stack: ItemStack?): UseAction = UseAction.SPEAR

    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity?): Int = 72000

    override fun usageTick(world: World, user: LivingEntity, stack: ItemStack?, remainingUseTicks: Int) {
        if (world.isClient && user.itemUseTime == USE_TIME)
            user.playSound(BobsMobGearSounds.WEAPON_ATTACK_READY)
        super.usageTick(world, user, stack, remainingUseTicks)
    }

    override fun canBeEnchantedWith(
        stack: ItemStack,
        enchantment: RegistryEntry<Enchantment>,
        context: EnchantingContext
    ): Boolean = super.canBeEnchantedWith(stack, enchantment, context) || enchantment.matchesKey(Enchantments.LOYALTY)

    override fun postHit(stack: ItemStack?, target: LivingEntity?, attacker: LivingEntity?): Boolean = true

    override fun postDamageEntity(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND)
    }

    override fun canMine(state: BlockState?, world: World?, pos: BlockPos?, miner: PlayerEntity): Boolean = !miner.isCreative

    companion object {
        const val USE_TIME = 20

        fun teleporting(selfDamage: Float, cooldown: Int, createEntity: (LivingEntity, World, ItemStack) -> AbstractEnderSpearEntity, material: ToolMaterial, settings: Settings) =
            EnderSpearItem(selfDamage, cooldown, 4, -2.8f, 0.0, createEntity, material, settings)

        fun homing(maxRange: Double, createEntity: (LivingEntity, World, ItemStack) -> EnderEyeSpearEntity, material: ToolMaterial, settings: Settings) =
            EnderSpearItem(0f, 0, 5, -3f, maxRange, createEntity, material, settings)
    }
}