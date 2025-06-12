package io.github.thebroccolibob.bobsmobgear.item

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class BoneHammerItem(material: ToolMaterial, settings: Settings) : ToolItem(material, settings.apply {
    attributeModifiers(SwordItem.createAttributeModifiers(material, 3, -2.4f))
}), HasSpecialAttack {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        return runSpecialAttack(user, hand, world)
    }
}