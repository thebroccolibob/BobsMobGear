package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.duck.WebShotUser
import io.github.thebroccolibob.bobsmobgear.duck.webShot
import io.github.thebroccolibob.bobsmobgear.entity.WebShotEntity
import io.github.thebroccolibob.bobsmobgear.util.damage
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.minus
import io.github.thebroccolibob.bobsmobgear.util.times
import net.minecraft.block.BlockState
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper.square
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class SpiderDaggerItem(private val pullStrength: Double, material: ToolMaterial, settings: Settings) : ToolItem(material, settings.apply {
    attributeModifiers(SwordItem.createAttributeModifiers(material, 2, -1.8f))
}) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user[hand]
        (user as WebShotUser).webShot?.run {
            if (!user.shouldCancelInteraction())  {
                hookedEntity?.let { hookedEntity ->
                    val difference = user.eyePos - hookedEntity.pos
                    hookedEntity.addVelocity(if (difference.lengthSquared() * square(ENTITY_DISTANCE_MULTIPLIER) < square(pullStrength)) difference * ENTITY_DISTANCE_MULTIPLIER else difference.normalize() * pullStrength)
                    hookedEntity.velocityModified = true
                }
                if (isHookedOnBlock) {
                    val difference = pos - user.movementPos
                    val length = (difference * BLOCK_DISTANCE_MULTIPLIER).length()
                    user.addVelocity(
                        if (length < pullStrength)
                            difference.normalize() * length
                        else
                            difference.normalize() * pullStrength
                    )
                }
            }
            user.playSound(SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE)
            stack.damage(1, user, hand)
            if (!world.isClient)
                discard()
            return TypedActionResult.success(stack)
        }

        user.playSound(SoundEvents.ENTITY_FISHING_BOBBER_THROW)

        if (world.isClient) return TypedActionResult.success(stack)

        val direction = user.rotationVector
        world.spawnEntity(WebShotEntity(world, user).apply {
            setVelocity(direction.x, direction.y, direction.z, 4f, 1f)
        })

        return TypedActionResult.success(stack)
    }

    override fun postHit(stack: ItemStack?, target: LivingEntity?, attacker: LivingEntity?): Boolean = true

    override fun postDamageEntity(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND)
    }

    override fun canMine(state: BlockState?, world: World?, pos: BlockPos?, miner: PlayerEntity): Boolean = !miner.isCreative

    companion object {
        const val ENTITY_DISTANCE_MULTIPLIER = 0.2
        val BLOCK_DISTANCE_MULTIPLIER = Vec3d(1.2, 0.75, 1.2)
    }
}