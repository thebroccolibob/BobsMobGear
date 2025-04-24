package io.github.thebroccolibob.bobsmobgear.client.util

import net.minecraft.client.util.math.MatrixStack

inline operator fun MatrixStack.invoke(block: MatrixStack.() -> Unit) {
    push()
    block()
    pop()
}
