package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.duck.WebShotUser
import io.github.thebroccolibob.bobsmobgear.duck.webShot
import io.github.thebroccolibob.bobsmobgear.entity.WebShotEntity
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.minus
import io.github.thebroccolibob.bobsmobgear.util.times
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.MathHelper.square
import net.minecraft.world.World

class SpiderDaggerItem(material: ToolMaterial, settings: Settings) : ToolItem(material, settings.apply {
    attributeModifiers(SwordItem.createAttributeModifiers(material, 2, -1.8f))
}) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user[hand]
        (user as WebShotUser).webShot?.run {
            hookedEntity?.let { hookedEntity ->
                val difference = user.eyePos - hookedEntity.pos
                hookedEntity.addVelocity(if (difference.lengthSquared() * square(ENTITY_DISTANCE_MULTIPLIER) < square(PULL_STRENGTH)) difference * ENTITY_DISTANCE_MULTIPLIER else difference.normalize() * PULL_STRENGTH)
                hookedEntity.velocityModified = true
            }
            if (isHookedOnBlock && !user.shouldCancelInteraction()) {
                val difference = pos - user.movementPos
                user.addVelocity(if (difference.lengthSquared() * square(BLOCK_DISTANCE_MULTIPLIER) < square(PULL_STRENGTH)) difference * BLOCK_DISTANCE_MULTIPLIER else difference.normalize() * PULL_STRENGTH)
            }
            if (!world.isClient)
                discard()
            return TypedActionResult.success(stack)
        }

        if (world.isClient) return TypedActionResult.success(stack)

        world.spawnEntity(WebShotEntity(world, user).apply {
            setVelocity(user, user.pitch, user.yaw, 0f, 4f, 0.1f)
        })

        return TypedActionResult.success(stack)
    }

    companion object {
        const val PULL_STRENGTH = 2.0
        const val ENTITY_DISTANCE_MULTIPLIER = 0.25
        const val BLOCK_DISTANCE_MULTIPLIER = 1.2
    }
}