package io.github.thebroccolibob.bobsmobgear.client.util

import dev.emi.emi.api.render.EmiTexture
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.LightType
import net.minecraft.world.World

inline operator fun MatrixStack.invoke(block: MatrixStack.() -> Unit) {
    push()
    block()
    pop()
}

data class SizedTexture(val identifier: Identifier, val width: Int, val height: Int)

fun SizedTexture.region(x: Int, y: Int, width: Int, height: Int) = EmiTexture(identifier, x, y, width, height, width, height, this.width, this.height)

fun World.getPackedLightLevel(pos: BlockPos) = LightmapTextureManager.pack(
    getLightLevel(LightType.BLOCK, pos),
    getLightLevel(LightType.SKY, pos),
)