package io.github.thebroccolibob.bobsmobgear.client.util

import net.minecraft.client.sound.Sound
import net.minecraft.client.sound.SoundEntry
import net.minecraft.util.Identifier
import net.minecraft.util.math.floatprovider.ConstantFloatProvider

fun SoundEntry(vararg sounds: Sound, replace: Boolean = false, subtitle: String? = null) = SoundEntry(listOf(*sounds), replace, subtitle)
fun SoundEntry(vararg sounds: Identifier, replace: Boolean = false, subtitle: String? = null) = SoundEntry(sounds.map(::Sound), replace, subtitle)

fun Sound(
    id: Identifier,
    volume: Float = 1f,
    pitch: Float = 1f,
    weight: Int = 1,
    stream: Boolean = false,
    preload: Boolean = false,
    attenuation: Int = 16
) = Sound(id, ConstantFloatProvider.create(volume), ConstantFloatProvider.create(pitch), weight, Sound.RegistrationType.FILE, stream, preload, attenuation)
