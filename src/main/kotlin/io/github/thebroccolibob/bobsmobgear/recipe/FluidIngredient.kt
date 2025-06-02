package io.github.thebroccolibob.bobsmobgear.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.thebroccolibob.bobsmobgear.util.isIn
import io.github.thebroccolibob.bobsmobgear.util.polymorphCodec
import io.github.thebroccolibob.bobsmobgear.util.singleOrList
import io.github.thebroccolibob.bobsmobgear.util.tagKeyPacketCodec
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.component.ComponentChanges
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.Fluids
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.codec.PacketCodecs.toList
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import java.util.function.Predicate

sealed class FluidIngredient(protected val amount: Long, protected val components: ComponentChanges) : Predicate<FluidVariant> {
    override fun test(input: FluidVariant): Boolean =
        input.componentsMatch(components)

    fun test(input: FluidVariant, amount: Long) = test(input) && amount >= this.amount

    class Tag(private val tag: TagKey<Fluid>, amount: Long, components: ComponentChanges) : FluidIngredient(amount, components) {
        override fun test(input: FluidVariant): Boolean = input.fluid.defaultState isIn tag

        companion object {
            val CODEC: Codec<Tag> = RecordCodecBuilder.create { it.group(
                TagKey.unprefixedCodec(RegistryKeys.FLUID).fieldOf("tag").forGetter(Tag::tag),
                Codec.LONG.fieldOf("amount").forGetter(Tag::amount),
                ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(Tag::components)
            ).apply(it, ::Tag) }

            val PACKET_CODEC: PacketCodec<RegistryByteBuf, Tag> = PacketCodec.tuple(
                tagKeyPacketCodec(RegistryKeys.FLUID), Tag::tag,
                PacketCodecs.VAR_LONG, Tag::amount,
                ComponentChanges.PACKET_CODEC, Tag::components,
                ::Tag
            )
        }
    }

    class Multiple(private val fluids: List<Fluid>, amount: Long, components: ComponentChanges) : FluidIngredient(amount, components) {
        override fun test(input: FluidVariant): Boolean = input.fluid in fluids

        companion object {
            val CODEC: Codec<Multiple> = RecordCodecBuilder.create { it.group(
                Registries.FLUID.codec.singleOrList().fieldOf("fluids").forGetter(Multiple::fluids),
                Codec.LONG.fieldOf("amount").forGetter(Multiple::amount),
                ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(Multiple::components)
            ).apply(it, ::Multiple) }

            val PACKET_CODEC: PacketCodec<RegistryByteBuf, Multiple> = PacketCodec.tuple(
                PacketCodecs.registryValue(RegistryKeys.FLUID).collect(toList()), Multiple::fluids,
                PacketCodecs.VAR_LONG, Multiple::amount,
                ComponentChanges.PACKET_CODEC, Multiple::components,
                ::Multiple
            )
        }
    }

    companion object {
        val CODEC = polymorphCodec(Tag.CODEC, Multiple.CODEC)

        val PACKET_CODEC = polymorphCodec(Tag.PACKET_CODEC, Multiple.PACKET_CODEC)

        val EMPTY = Multiple(listOf(Fluids.EMPTY), 0, ComponentChanges.EMPTY)

        fun of(amount: Long, vararg fluids: Fluid, components: ComponentChanges = ComponentChanges.EMPTY): FluidIngredient =
            Multiple(fluids.toList(), amount, components)

        fun fromTag(amount: Long, tag: TagKey<Fluid>, components: ComponentChanges = ComponentChanges.EMPTY): FluidIngredient =
            Tag(tag, amount, components)
    }
}