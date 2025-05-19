package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.util.get
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolMaterial
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.world.World

class FleshGloveItem(
    material: ToolMaterial,
    settings: Settings,
    private val blockSuccessChance: Float,
) : AbstractFleshGlove(material, settings) {

    override fun getUseAction(stack: ItemStack?) = UseAction.BLOCK
    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity?) = 72000

    override fun use(world: World?, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        user.setCurrentHand(hand)
        return TypedActionResult.consume(user[hand])
    }
}