package io.github.thebroccolibob.bobsmobgear.util

import com.mojang.datafixers.util.Function8
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import java.util.*
import java.util.function.Function

/**
 * Returns a codec for encoding 8 values.
 */
fun <B, C, T1, T2, T3, T4, T5, T6, T7, T8> packetCodecTuple(
    codec1: PacketCodec<in B, T1>,
    from1: Function<C, T1>,
    codec2: PacketCodec<in B, T2>,
    from2: Function<C, T2>,
    codec3: PacketCodec<in B, T3>,
    from3: Function<C, T3>,
    codec4: PacketCodec<in B, T4>,
    from4: Function<C, T4>,
    codec5: PacketCodec<in B, T5>,
    from5: Function<C, T5>,
    codec6: PacketCodec<in B, T6>,
    from6: Function<C, T6>,
    codec7: PacketCodec<in B, T7>,
    from7: Function<C, T7>,
    codec8: PacketCodec<in B, T8>,
    from8: Function<C, T8>,
    to: Function8<T1, T2, T3, T4, T5, T6, T7, T8, C>
): PacketCodec<B, C> = object : PacketCodec<B, C> {
    override fun decode(buf: B): C {
        val object1 = codec1.decode(buf)
        val object2 = codec2.decode(buf)
        val object3 = codec3.decode(buf)
        val object4 = codec4.decode(buf)
        val object5 = codec5.decode(buf)
        val object6 = codec6.decode(buf)
        val object7 = codec7.decode(buf)
        val object8 = codec8.decode(buf)
        return to.apply(object1, object2, object3, object4, object5, object6, object7, object8)
    }

    override fun encode(buf: B, value: C) {
        codec1.encode(buf, from1.apply(value))
        codec2.encode(buf, from2.apply(value))
        codec3.encode(buf, from3.apply(value))
        codec4.encode(buf, from4.apply(value))
        codec5.encode(buf, from5.apply(value))
        codec6.encode(buf, from6.apply(value))
        codec7.encode(buf, from7.apply(value))
        codec8.encode(buf, from8.apply(value))
    }
}

/**
 * Returns a codec for a [DefaultedList]
 */
fun <B : ByteBuf, V> PacketCodec<B, V>.defaultedList(defaultValue: V): PacketCodec<B, DefaultedList<V>> =
    DefaultedListPacketCodec(this, defaultValue)

class DefaultedListPacketCodec<B : ByteBuf, V>(private val elementCodec: PacketCodec<B, V>, private val defaultValue: V, private val maxSize: Int = Int.MAX_VALUE) : PacketCodec<B, DefaultedList<V>> {
    override fun decode(buf: B): DefaultedList<V> {
        val size = PacketCodecs.readCollectionSize(buf, maxSize)
        return DefaultedList.ofSize(size, defaultValue).apply {
            repeat(size) {
                this[it] = elementCodec.decode(buf)
            }
        }
    }

    override fun encode(buf: B, value: DefaultedList<V>) {
        PacketCodecs.writeCollectionSize(buf, value.size, maxSize)
        for (item in value)
            elementCodec.encode(buf, item)
    }
}

fun <T> tagKeyPacketCodec(registry: RegistryKey<out Registry<T>>): PacketCodec<ByteBuf, TagKey<T>> =
    Identifier.PACKET_CODEC.xmap({ TagKey.of(registry, it) }, { it.id })

/**
 * A codec that has two codecs that deserialize to the same type
 */
class AlternateCodec<A>(
    private val first: Codec<A>,
    private val second: Codec<A>,
    private val useSecond: (A) -> Boolean,
) : Codec<A> {
    override fun <T : Any> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<A, T>> {
        first.decode(ops, input).let {
            return if (it.result().isPresent) it else second.decode(ops, input)
        }
    }

    override fun <T : Any> encode(input: A, ops: DynamicOps<T>, prefix: T): DataResult<T> {
        return (if (useSecond(input)) second else first).encode(input, ops, prefix)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        return (other as AlternateCodec<*>).let {
            first == it.first && second == it.second && useSecond == it.useSecond
        }
    }

    override fun hashCode() = Objects.hash(first, second, useSecond)

    override fun toString(): String = "AlternateCodec[$first, $second]"
}

fun <A> Codec<A>.singleOrList(): Codec<List<A>> =
    AlternateCodec(this.listOf(), this.xmap({ listOf(it) }, { it[0] })) { it.size == 1 }
