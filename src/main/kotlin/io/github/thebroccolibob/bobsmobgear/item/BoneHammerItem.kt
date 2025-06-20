package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearParticles
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class BoneHammerItem(material: ToolMaterial, settings: Settings) : ToolItem(material, settings.apply {
    attributeModifiers(SwordItem.createAttributeModifiers(material, 3, -3f))
}), HasSpecialAttack {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        return runSpecialAttack(user, hand, world)
    }

    override fun postHit(stack: ItemStack?, target: LivingEntity?, attacker: LivingEntity?): Boolean = true

    override fun postDamageEntity(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) {
        if (BobsMobGearItems.USING_SPECIAL_ATTACK in stack)
            target.addVelocity(0.0, 0.5, 0.0) // placeholder
        if (target.isDead || BobsMobGearItems.USING_SPECIAL_ATTACK in stack)
            (target.world as? ServerWorld)?.spawnParticles(BobsMobGearParticles.BONEK, target.x, target.getBodyY(0.67), target.z, 1, target.width / 2.0, target.height / 3.0, target.width / 2.0, 0.0)
    }
}