package io.github.thebroccolibob.bobsmobgear.datagen

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.client.util.Sound
import io.github.thebroccolibob.bobsmobgear.client.util.SoundEntry
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearSounds
import io.github.thebroccolibob.bobsmobgear.util.AlternateCodec
import io.github.thebroccolibob.bobsmobgear.util.toOptional
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.sound.Sound
import net.minecraft.client.sound.SoundEntry
import net.minecraft.data.DataOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.util.math.floatprovider.ConstantFloatProvider
import net.minecraft.util.math.floatprovider.FloatSupplier
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull

class SoundsGenerator(
    private val dataOutput: FabricDataOutput
) : DataProvider {
    private val pathResolver = dataOutput.getResolver(DataOutput.OutputType.RESOURCE_PACK, "")

    private fun generateSounds(addSound: (SoundEvent, SoundEntry) -> Unit) {
        fun addSoundSubtitle(soundEvent: SoundEvent, vararg sounds: Sound) {
            addSound(soundEvent, SoundEntry(*sounds, subtitle = subtitleOf(soundEvent)))
        }
        fun addSoundSubtitle(soundEvent: SoundEvent, vararg sounds: Identifier) {
            addSound(soundEvent, SoundEntry(*sounds, subtitle = subtitleOf(soundEvent)))
        }

        addSoundSubtitle(BobsMobGearSounds.TEMPLATE_HAMMER,
            BobsMobGear.id("hammer1"),
            BobsMobGear.id("hammer2"),
            BobsMobGear.id("hammer3"),
        )
        addSoundSubtitle(BobsMobGearSounds.TEMPLATE_ADD_ITEM,
            Identifier.ofVanilla("entity/itemframe/add_item1"),
            Identifier.ofVanilla("entity/itemframe/add_item2"),
            Identifier.ofVanilla("entity/itemframe/add_item3"),
            Identifier.ofVanilla("entity/itemframe/add_item4"),
        )
        addSoundSubtitle(BobsMobGearSounds.TEMPLATE_REMOVE_ITEM,
            Identifier.ofVanilla("entity/itemframe/remove_item1"),
            Identifier.ofVanilla("entity/itemframe/remove_item2"),
            Identifier.ofVanilla("entity/itemframe/remove_item3"),
            Identifier.ofVanilla("entity/itemframe/remove_item4"),
        )
        addSoundSubtitle(BobsMobGearSounds.TEMPLATE_CRAFT,
            Identifier.ofVanilla("random/break"),
        )
        addSoundSubtitle(BobsMobGearSounds.FORGE_HEATER_FUEL,
            Identifier.ofVanilla("mob/ghast/fireball4")
        )
        addSoundSubtitle(BobsMobGearSounds.WEAPON_ATTACK_READY,
            Sound(Identifier.ofVanilla("random/successful_hit"), volume = 0.8f)
        )
        addSoundSubtitle(BobsMobGearSounds.EQUIPMENT_REPAIR,
            Identifier.ofVanilla("block/smithing_table/smithing_table1"),
            Identifier.ofVanilla("block/smithing_table/smithing_table2"),
            Identifier.ofVanilla("block/smithing_table/smithing_table3"),
        )
        addSoundSubtitle(BobsMobGearSounds.TONGS_PICKUP,
            Sound(Identifier.ofVanilla("random/pop"), volume = 0.5f)
        )
        addSoundSubtitle(BobsMobGearSounds.TONGS_DROP,
            Sound(Identifier.ofVanilla("random/pop"), volume = 0.5f, pitch = 0.8f)
        )
    }

    override fun run(writer: DataWriter): CompletableFuture<*> {
        val sounds = mutableMapOf<String, SoundEntry>().also { generateSounds { event, entry ->
            require(event.id.namespace == dataOutput.modId) { "Sound event ${event.id} was not under nameespace ${dataOutput.modId}" }
            it[event.id.path] = entry
        } }

        return DataProvider.writeToPath(writer, CODEC.encodeStart(JsonOps.INSTANCE, sounds).orThrow, pathResolver.resolveJson(Identifier.of(dataOutput.modId, "sounds")))
    }

    override fun getName(): String = "sounds.json"

    companion object {
        fun subtitleOf(soundEvent: SoundEvent) = soundEvent.id.let {
            val split = it.path.indexOf('.')
            "subtitles.${it.path.substring(0, split)}.${it.namespace}${it.path.substring(split)}"
        }

        val FLOAT_SUPPLIER_CODEC: Codec<FloatSupplier> = ConstantFloatProvider.VALUE_CODEC.flatComapMap(
            { it as FloatSupplier },
            { (it as? ConstantFloatProvider)?.let { value -> DataResult.success(value) } ?: DataResult.error { "FloatSupplier was not a ConstantFloatProvider" } }
        )

        val SOUND_CODEC: Codec<Sound> = AlternateCodec(
            RecordCodecBuilder.create { it.group(
                Identifier.CODEC.fieldOf("name").forGetter(Sound::getIdentifier),
                FLOAT_SUPPLIER_CODEC.optionalFieldOf("volume", ConstantFloatProvider.create(1f)).forGetter(Sound::getVolume),
                FLOAT_SUPPLIER_CODEC.optionalFieldOf("pitch", ConstantFloatProvider.create(1f)).forGetter(Sound::getVolume),
                Codec.INT.optionalFieldOf("weight", 1).forGetter(Sound::getWeight),
                Codec.BOOL.optionalFieldOf("stream", false).forGetter(Sound::isStreamed),
                Codec.BOOL.optionalFieldOf("preload", false).forGetter(Sound::isPreloaded),
                Codec.INT.optionalFieldOf("attenuation_distance", 16).forGetter(Sound::getAttenuation),
            ).apply(it) { name, volume, pitch, weight, stream, attenuation, preload ->
                Sound(name, volume, pitch, weight, Sound.RegistrationType.FILE, stream, attenuation, preload)
            } },
            Identifier.CODEC.xmap({ Sound(it) }, { it.identifier })
        ) { it.run {
            (volume as? ConstantFloatProvider)?.value == 1f
                && (pitch as? ConstantFloatProvider)?.value == 1f
                && weight == 1 && !isStreamed && !isPreloaded && attenuation == 16
        } }

        val SOUND_ENTRY_CODEC: Codec<SoundEntry> = RecordCodecBuilder.create { it.group(
            SOUND_CODEC.listOf().fieldOf("sounds").forGetter(SoundEntry::getSounds),
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(SoundEntry::canReplace),
            Codec.STRING.optionalFieldOf("subtitle").forGetter { entry -> entry.subtitle.toOptional() },
        ).apply(it) { sounds, replace, subtitle -> SoundEntry(sounds, replace, subtitle.getOrNull()) } }

        val CODEC: Codec<Map<String, SoundEntry>> = Codec.unboundedMap(Codec.STRING, SOUND_ENTRY_CODEC)
    }
}
