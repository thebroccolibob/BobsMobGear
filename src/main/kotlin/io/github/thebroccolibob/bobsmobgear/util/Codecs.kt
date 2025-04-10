package io.github.thebroccolibob.bobsmobgear.util

import com.mojang.datafixers.util.Function7
import net.minecraft.network.codec.PacketCodec
import java.util.function.Function

fun <B, C, T1, T2, T3, T4, T5, T6, T7> packetCodecTuple(
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
    to: Function7<T1, T2, T3, T4, T5, T6, T7, C>
): PacketCodec<B, C> = object : PacketCodec<B, C> {
    override fun decode(buf: B): C {
        val object1 = codec1.decode(buf)
        val object2 = codec2.decode(buf)
        val object3 = codec3.decode(buf)
        val object4 = codec4.decode(buf)
        val object5 = codec5.decode(buf)
        val object6 = codec6.decode(buf)
        val object7 = codec7.decode(buf)
        return to.apply(object1, object2, object3, object4, object5, object6, object7)
    }

    override fun encode(buf: B, value: C) {
        codec1.encode(buf, from1.apply(value))
        codec2.encode(buf, from2.apply(value))
        codec3.encode(buf, from3.apply(value))
        codec4.encode(buf, from4.apply(value))
        codec5.encode(buf, from5.apply(value))
        codec6.encode(buf, from6.apply(value))
        codec6.encode(buf, from6.apply(value))
    }
}
