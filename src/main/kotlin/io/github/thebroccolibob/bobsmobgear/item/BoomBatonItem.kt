package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.block.GunflowerBlock
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearSounds
import io.github.thebroccolibob.bobsmobgear.util.get
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class BoomBatonItem(val range: Int, val cooldown: Int, val gunflower: Block, material: ToolMaterial, settings: Settings) : ToolItem(material, settings.apply {
    component(DataComponentTypes.ATTRIBUTE_MODIFIERS, SwordItem.createAttributeModifiers(material, 2, -2f))
}) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val placementContext = ItemPlacementContext(context)
        val state = gunflower.getPlacementState(placementContext) ?: return ActionResult.FAIL

        val world = placementContext.world
        val pos = placementContext.blockPos
        val player = placementContext.player
        val stack = placementContext.stack

        if (!state.canPlaceAt(world, pos)) return ActionResult.FAIL

        world.setBlockState(pos, state, Block.NOTIFY_LISTENERS)
        state.block.onPlaced(world, pos, state, player, stack)
        if (player is ServerPlayerEntity)
            Criteria.PLACED_BLOCK.trigger(player, pos, stack)

        world.playSound(
            player,
            pos,
            state.soundGroup.placeSound,
            SoundCategory.BLOCKS,
            (state.soundGroup.getVolume() + 1.0f) / 2.0f,
            state.soundGroup.getPitch() * 0.8f
        )
        world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(player, state))

        return ActionResult.SUCCESS
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user[hand]
        if (stack.damage + 1 >= stack.maxDamage)
            return TypedActionResult.fail(stack)
        user.setCurrentHand(hand)
        return TypedActionResult.consume(stack)
    }

    override fun usageTick(world: World, user: LivingEntity, stack: ItemStack?, remainingUseTicks: Int) {
        if (world.isClient && user.itemUseTime == USE_TIME)
            user.playSound(BobsMobGearSounds.WEAPON_ATTACK_READY)
        super.usageTick(world, user, stack, remainingUseTicks)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (world.isClient || user.itemUseTime < USE_TIME) return
        val center = user.blockPos
        val flowerPosS = BlockPos.iterate(center.x - range, center.y - range, center.z - range, center.x + range, center.y + range, center.z + range).filter {
            world[it].block is GunflowerBlock
        }
        for (pos in flowerPosS) {
            val state = world[pos]
            (state.block as? GunflowerBlock)?.explode(state, world, pos, user)
        }
        if (flowerPosS.isEmpty()) return
        (user as? PlayerEntity)?.itemCooldownManager?.set(stack.item, cooldown)
    }

    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity?): Int = 72000

    override fun getUseAction(stack: ItemStack?): UseAction = UseAction.BOW

    override fun postHit(stack: ItemStack?, target: LivingEntity?, attacker: LivingEntity?): Boolean = true

    override fun postDamageEntity(stack: ItemStack, target: LivingEntity?, attacker: LivingEntity) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND)
    }

    override fun canMine(state: BlockState?, world: World?, pos: BlockPos?, miner: PlayerEntity): Boolean = !miner.isCreative

    companion object {
        const val USE_TIME = 10
    }
}