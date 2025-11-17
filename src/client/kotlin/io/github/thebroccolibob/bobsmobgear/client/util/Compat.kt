package io.github.thebroccolibob.bobsmobgear.client.util

import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat
import net.minecraft.util.Identifier

fun cataclysmId(path: String): Identifier = Identifier.of(BobsMobGearCompat.CATACLYSM, path)
