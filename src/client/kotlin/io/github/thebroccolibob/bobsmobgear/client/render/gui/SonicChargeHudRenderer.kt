package io.github.thebroccolibob.bobsmobgear.client.render.gui

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.getBarProgress
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Arm
import net.minecraft.util.Identifier

object SonicChargeHudRenderer : HudRenderCallback, ClientTickEvents.EndTick {
    val TEXTURE_RIGHT_EMPTY = BobsMobGear.id("sonic_charge_bar_right")
    val TEXTURE_RIGHT_FILLED = BobsMobGear.id("sonic_charge_bar_right_filled")
    val TEXTURE_LEFT_EMPTY = BobsMobGear.id("sonic_charge_bar_left")
    val TEXTURE_LEFT_FILLED = BobsMobGear.id("sonic_charge_bar_left_filled")
    const val WIDTH = 26
    const val HEIGHT = 10
    const val BAR_WIDTH = 16
    const val GAP = 16
    const val SHOWN_TICKS = 10 * 20

    private var lastLeftStack: ItemStack? = null
    private var lastRightStack: ItemStack? = null
    private var ticksRemaining = 0

    override fun onHudRender(context: DrawContext, tickCounter: RenderTickCounter) {
        val player = MinecraftClient.getInstance().player!!
        val leftStack = getSonicStack(player, Arm.LEFT)
        val rightStack = getSonicStack(player, Arm.RIGHT)
        if (leftStack != lastLeftStack) {
            ticksRemaining = SHOWN_TICKS
            lastLeftStack = leftStack
        }
        if (rightStack != lastRightStack) {
            ticksRemaining = SHOWN_TICKS
            lastRightStack = rightStack
        }
        if (ticksRemaining == 0) return
        val centerX = context.scaledWindowWidth / 2
        val centerY = context.scaledWindowHeight / 2
        when {
            leftStack != null && rightStack != null -> {
                renderBar(context, TEXTURE_LEFT_EMPTY, TEXTURE_LEFT_FILLED, leftStack, centerX - GAP - WIDTH, centerY - HEIGHT / 2, false)
                renderBar(context, TEXTURE_RIGHT_EMPTY, TEXTURE_RIGHT_FILLED, rightStack, centerX + GAP, centerY - HEIGHT / 2, true)
            }
            leftStack != null ->
                renderBar(context, TEXTURE_RIGHT_EMPTY, TEXTURE_RIGHT_FILLED, leftStack, centerX - WIDTH / 2, centerY + GAP)
            rightStack != null ->
                renderBar(context, TEXTURE_RIGHT_EMPTY, TEXTURE_RIGHT_FILLED, rightStack, centerX - WIDTH / 2, centerY + GAP)
        }
    }

    private fun getSonicStack(player: PlayerEntity, arm: Arm) =
        (if (player.mainArm == arm) player.mainHandStack else player.offHandStack).takeIf { BobsMobGearItems.MAX_SONIC_CHARGE in it }

    private fun renderBar(context: DrawContext, emptyTexture: Identifier, filledTexture: Identifier, stack: ItemStack, x: Int, y: Int, leftToRight: Boolean = true) {
        val maxCharge = stack[BobsMobGearItems.MAX_SONIC_CHARGE] ?: 1
        val charge = stack[BobsMobGearItems.SONIC_CHARGE] ?: 0
        context.drawGuiTexture(emptyTexture, x, y, WIDTH, HEIGHT)
        if (charge == maxCharge)
            context.drawGuiTexture(filledTexture, x, y, WIDTH, HEIGHT)
        else {
            val fillWidth = getBarProgress(charge, maxCharge, BAR_WIDTH)
            context.drawGuiTexture(filledTexture, WIDTH, HEIGHT, if (leftToRight) 0 else WIDTH - fillWidth, 0, if (leftToRight) x else x + WIDTH - fillWidth, y, fillWidth, HEIGHT)
        }
    }

    override fun onEndTick(client: MinecraftClient?) {
        if (ticksRemaining > 0)
            ticksRemaining--
    }

    fun register() {
        HudRenderCallback.EVENT.register(this)
        ClientTickEvents.END_CLIENT_TICK.register(this)
    }
}