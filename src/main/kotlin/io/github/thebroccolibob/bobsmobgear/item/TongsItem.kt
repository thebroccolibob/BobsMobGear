package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.extinguishHeatedStack
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems.TONGS_HELD_ITEM
import io.github.thebroccolibob.bobsmobgear.util.*
import net.minecraft.block.Blocks
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.inventory.StackReference
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.registry.tag.FluidTags
import net.minecraft.screen.ScreenTexts
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.ClickType
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.Util.createTranslationKey
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

class TongsItem(settings: Settings) : Item(settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user[hand]
        if (TONGS_HELD_ITEM in stack) return TypedActionResult.fail(stack)

        val itemEntity = (ProjectileUtil.getCollision(user, { it is ItemEntity }, user.entityInteractionRange) as? EntityHitResult)?.entity as? ItemEntity
            ?: return TypedActionResult.fail(stack)

        if (!world.isClient) // Safety
            stack[TONGS_HELD_ITEM] = itemEntity.stack.split(1)

        return TypedActionResult.success(stack)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val heldItem = context.stack[TONGS_HELD_ITEM] ?: return ActionResult.PASS

        val state = context.world[context.blockPos]

        if (BobsMobGearItems.HEATED in heldItem && (state.fluidState isIn FluidTags.WATER || state isOf Blocks.WATER_CAULDRON)) {
            extinguishHeatedStack(heldItem, context.world, context.player, context.blockPos)
            context.stack[TONGS_HELD_ITEM] = heldItem
            return ActionResult.SUCCESS
        }

        if (context.world.isClient) return ActionResult.SUCCESS

        val placePos = context.blockPos.let {
            if (state.getCollisionShape(context.world, context.blockPos).isEmpty)
                it
            else
                it.offset(context.side)
        }.toBottomCenterPos()

        if (context.world.spawnEntity(ItemEntity(context.world, placePos.x, placePos.y, placePos.z, heldItem, 0.0, 0.0, 0.0)))
            context.stack.remove(TONGS_HELD_ITEM)

        return ActionResult.CONSUME
    }

    override fun onClicked(stack: ItemStack, otherStack: ItemStack, slot: Slot?, clickType: ClickType, player: PlayerEntity?, cursorStackReference: StackReference): Boolean {
        if (clickType != ClickType.RIGHT) return false

        if (otherStack.isEmpty) {
            if (TONGS_HELD_ITEM !in stack) return false

            cursorStackReference.set(stack.remove(TONGS_HELD_ITEM))
        } else {
            if (TONGS_HELD_ITEM in stack) return false

            stack[TONGS_HELD_ITEM] = otherStack.split(1)
        }

        return true
    }

    override fun onStackClicked(stack: ItemStack, slot: Slot, clickType: ClickType, player: PlayerEntity): Boolean {
        if (clickType != ClickType.RIGHT) return false

        val otherStack = slot.stack

        if (otherStack.isEmpty) {
            if (TONGS_HELD_ITEM !in stack) return false

            slot.stack = stack.remove(TONGS_HELD_ITEM)
        } else {
            if (TONGS_HELD_ITEM in stack) return false

            stack[TONGS_HELD_ITEM] = otherStack.split(1)
        }

        return true
    }

    override fun appendTooltip(
        stack: ItemStack,
        context: TooltipContext?,
        tooltip: MutableList<Text>,
        type: TooltipType?
    ) {
        stack[TONGS_HELD_ITEM]?.let {
            tooltip.add(HELD_ITEM_TOOLTIP.text() + ScreenTexts.SPACE + it.toHoverableText())
        }
    }

    companion object {
        val HELD_ITEM_TOOLTIP = Translation.unit(createTranslationKey("item", BobsMobGear.id("smithing_tongs.held_item")))
    }
}