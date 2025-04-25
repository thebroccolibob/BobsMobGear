package io.github.thebroccolibob.bobsmobgear.client.util

import net.minecraft.data.client.BlockStateVariant
import net.minecraft.data.client.VariantSettings
import net.minecraft.data.client.VariantSettings.Rotation
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

fun BlockStateVariant(
    model: Identifier,
    x: Rotation? = null,
    y: Rotation? = null,
    uvLock: Boolean? = null,
    weight: Int? = null
) = BlockStateVariant().apply {
    put(VariantSettings.MODEL, model)
    x?.let { put(VariantSettings.X, it) }
    y?.let { put(VariantSettings.Y, it) }
    uvLock?.let { put(VariantSettings.UVLOCK, it) }
    weight?.let { put(VariantSettings.WEIGHT, it) }
}

fun variantYRotation(direction: Direction): Rotation? = when (direction) {
    Direction.EAST -> Rotation.R90
    Direction.SOUTH -> Rotation.R180
    Direction.WEST -> Rotation.R270
    else -> null
}
