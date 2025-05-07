package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGearClient
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearSounds
import io.github.thebroccolibob.bobsmobgear.util.add
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper
import net.minecraft.sound.SoundEvent
import java.util.concurrent.CompletableFuture

class LangGenerator(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>) :
    FabricLanguageProvider(dataOutput, registryLookup) {

    override fun generateTranslations(
        registryLookup: RegistryWrapper.WrapperLookup,
        translationBuilder: TranslationBuilder
    ) = with(translationBuilder) {
        add(BobsMobGearBlocks.SWORD_TEMPLATE, "Sword Template")
        add(BobsMobGearItems.SMITHING_HAMMER_TAG, "Smithing Hammers")

        add(BobsMobGearClient.HEATED_TOOLTIP, "Heated")

        add(BobsMobGearSounds.TEMPLATE_CRAFT, "Tool crafts")
        add(BobsMobGearSounds.TEMPLATE_HAMMER, "Tool hammers")
        add(BobsMobGearSounds.TEMPLATE_ADD_ITEM, "Template fills")
        add(BobsMobGearSounds.TEMPLATE_REMOVE_ITEM, "Template empties")
    }

    companion object {
        fun TranslationBuilder.add(sound: SoundEvent, subtitle: String) {
            add(SoundsGenerator.subtitleOf(sound), subtitle)
        }
    }
}
