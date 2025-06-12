package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.duck.WebShotUser
import io.github.thebroccolibob.bobsmobgear.duck.webShot
import io.github.thebroccolibob.bobsmobgear.entity.WebShotEntity
import io.github.thebroccolibob.bobsmobgear.util.get
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class SpiderDaggerItem(material: ToolMaterial, settings: Settings) : ToolItem(material, settings.apply {
    attributeModifiers(SwordItem.createAttributeModifiers(material, 2, -1.8f))
}) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user[hand]
        (user as WebShotUser).webShot?.let {
            if (!world.isClient)
                it.discard()
            return TypedActionResult.success(stack)
        }

        if (world.isClient) return TypedActionResult.success(stack)

        world.spawnEntity(WebShotEntity(world, user).apply {
            setVelocity(user, user.pitch, user.yaw, 0f, 1f, 0.1f)
        })

        return TypedActionResult.success(stack)
    }
}