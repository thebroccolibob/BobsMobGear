package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.extinguishHeatedStack
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItemTags
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems.TONGS_HELD_ITEM
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearSounds
import io.github.thebroccolibob.bobsmobgear.util.*
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
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
import net.minecraft.sound.SoundCategory
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.ClickType
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.Util.createTranslationKey
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class TongsItem(settings: Settings) : Item(settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user[hand]
        if (stack[TONGS_HELD_ITEM]?.isEmpty != true) return TypedActionResult.fail(stack)

        val itemEntity = (ProjectileUtil.getCollision(user, { it is ItemEntity }, user.blockInteractionRange) as? EntityHitResult)?.entity as? ItemEntity
            ?: return TypedActionResult.fail(stack)

        if (!(itemEntity.stack isIn BobsMobGearItemTags.TONG_HOLDABLE)) return TypedActionResult.fail(stack)

        if (!world.isClient) {// Safety
            stack[TONGS_HELD_ITEM] = ComparableItemStack(itemEntity.stack.split(1))
            playAddSound(world, itemEntity.pos)
        }

        return TypedActionResult.success(stack)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val heldItem = context.stack[TONGS_HELD_ITEM]?.takeUnless { it.isEmpty }?.stack ?: return ActionResult.PASS

        val state = context.world[context.blockPos]

        if (BobsMobGearItems.HEATED in heldItem && (state.fluidState isIn FluidTags.WATER || state isOf Blocks.WATER_CAULDRON)) {
            context.stack[TONGS_HELD_ITEM] = heldItem.copy().also {
                extinguishHeatedStack(it, context.world, context.player, context.blockPos)
            }
            return ActionResult.SUCCESS
        }

        if (context.world.isClient) return ActionResult.SUCCESS

        val placePos = context.blockPos.let {
            if (state.getCollisionShape(context.world, context.blockPos).isEmpty)
                it
            else
                it.offset(context.side)
        }.toBottomCenterPos()

        if (context.world.spawnEntity(ItemEntity(context.world, placePos.x, placePos.y, placePos.z, heldItem, 0.0, 0.0, 0.0))) {
            context.stack.removeHeld()
            playRemoveSound(context.world, context.hitPos)
        }

        return ActionResult.CONSUME
    }

    override fun onClicked(stack: ItemStack, otherStack: ItemStack, slot: Slot?, clickType: ClickType, player: PlayerEntity, cursorStackReference: StackReference): Boolean {
        if (clickType != ClickType.RIGHT) return false

        if (otherStack.isEmpty) {
            if (stack[TONGS_HELD_ITEM]?.isEmpty != false) return false

            cursorStackReference.set(stack.removeHeld())
            playRemoveSound(player)
        } else {
            if (!(otherStack isIn BobsMobGearItemTags.TONG_HOLDABLE) || stack[TONGS_HELD_ITEM]?.isEmpty != true) return false

            stack[TONGS_HELD_ITEM] = otherStack.split(1)
            playAddSound(player)
        }

        return true
    }

    override fun onStackClicked(stack: ItemStack, slot: Slot, clickType: ClickType, player: PlayerEntity): Boolean {
        if (clickType != ClickType.RIGHT) return false

        val otherStack = slot.stack

        if (otherStack.isEmpty) {
            if (stack[TONGS_HELD_ITEM]?.isEmpty != false) return false

            slot.stack = stack.removeHeld()
            playRemoveSound(player)
        } else {
            if (!(otherStack isIn BobsMobGearItemTags.TONG_HOLDABLE) || stack[TONGS_HELD_ITEM]?.isEmpty != true) return false

            stack[TONGS_HELD_ITEM] = otherStack.split(1)
            playAddSound(player)
        }

        return true
    }

    override fun onItemEntityDestroyed(entity: ItemEntity) {
        entity.world.takeUnless { it.isClient }?.spawnEntity(
            ItemEntity(
                entity.world,
                entity.x,
                entity.y,
                entity.z,
                entity.stack.removeHeld()
            ))
    }

    override fun appendTooltip(
        stack: ItemStack,
        context: TooltipContext?,
        tooltip: MutableList<Text>,
        type: TooltipType?
    ) {
        stack[TONGS_HELD_ITEM]?.takeUnless { it.isEmpty }?.let {
            tooltip.add(HELD_ITEM_TOOLTIP.text() + ScreenTexts.SPACE + it.stack.toHoverableText())
        }
    }

    companion object {
        val HELD_ITEM_TOOLTIP = Translation.unit(createTranslationKey("item", BobsMobGear.id("smithing_tongs.held_item")))

        private fun ItemStack.removeHeld(): ItemStack = set(TONGS_HELD_ITEM, ComparableItemStack.EMPTY)!!.stack

        private fun playRemoveSound(entity: Entity) {
            entity.playSound(BobsMobGearSounds.TONGS_DROP, 1f, 1f)
        }
        private fun playRemoveSound(world: World, pos: Vec3d) {
            world.playSound(null, pos.x, pos.y, pos.z, BobsMobGearSounds.TONGS_DROP, SoundCategory.PLAYERS, 1f, 1f)
        }

        private fun playAddSound(entity: Entity) {
            entity.playSound(BobsMobGearSounds.TONGS_PICKUP, 1f, 1f)
        }
        private fun playAddSound(world: World, pos: Vec3d) {
            world.playSound(null, pos.x, pos.y, pos.z, BobsMobGearSounds.TONGS_PICKUP, SoundCategory.PLAYERS, 1f, 1f)
        }

    }
}