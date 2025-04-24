package io.github.thebroccolibob.bobsmobgear.client.util

import net.minecraft.client.util.math.MatrixStack

inline fun MatrixStack.layer(block: () -> Unit) {
    push()
    block()
    pop()
}
