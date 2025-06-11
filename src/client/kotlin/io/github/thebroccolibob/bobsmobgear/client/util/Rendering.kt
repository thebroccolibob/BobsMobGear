package io.github.thebroccolibob.bobsmobgear.client.util

import dev.emi.emi.api.render.EmiTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

inline operator fun MatrixStack.invoke(block: MatrixStack.() -> Unit) {
    push()
    block()
    pop()
}

data class SizedTexture(val identifier: Identifier, val width: Int, val height: Int)

fun SizedTexture.region(x: Int, y: Int, width: Int, height: Int) = EmiTexture(identifier, x, y, width, height, width, height, this.width, this.height)