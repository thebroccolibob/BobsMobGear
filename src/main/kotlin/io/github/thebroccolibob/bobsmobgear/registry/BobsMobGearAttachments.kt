package io.github.thebroccolibob.bobsmobgear.registry

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.minecraft.network.codec.PacketCodec
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import net.minecraft.util.Unit as McUnit

@Suppress("UnstableApiUsage")
object BobsMobGearAttachments {
    private fun <T> register(name: String, init: AttachmentRegistry.Builder<T>.() -> Unit = {}): AttachmentType<T> =
        AttachmentRegistry.create<T>(BobsMobGear.id(name)) { it.init() }

    val KICK_TICKS = register<Int>("kick_ticks")
    val SHEARED = register<McUnit>("sheared") {
        persistent(McUnit.CODEC)
        syncWith(PacketCodec.unit(McUnit.INSTANCE), AttachmentSyncPredicate.allButTarget())
    }

    fun register() {}
}