package io.github.thebroccolibob.bobsmobgear.network

import net.minecraft.entity.Entity
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.world.World
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.netty.buffer.ByteBuf

data class DetectedEntityPayload(val entityId: Int) : CustomPayload {

    constructor(entity: Entity) : this(entity.id)

    fun getEntity(world: World): Entity = world.getEntityById(entityId)!!

    override fun getId(): CustomPayload.Id<out DetectedEntityPayload> = ID

    companion object {
        val ID = CustomPayload.Id<DetectedEntityPayload>(BobsMobGear.id("detected_entity"))
        val CODEC: PacketCodec<ByteBuf, DetectedEntityPayload> = PacketCodecs.INTEGER.xmap(::DetectedEntityPayload, DetectedEntityPayload::entityId)
    }
}