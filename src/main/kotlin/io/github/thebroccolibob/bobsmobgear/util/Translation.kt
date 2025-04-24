package io.github.thebroccolibob.bobsmobgear.util

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder
import net.minecraft.text.MutableText
import net.minecraft.text.Text

abstract class Translation(val translationKey: String) {
    companion object {
        fun unit(translationKey: String, init: MutableText.() -> Unit = {}) = UnitTranslation(translationKey, init)
        fun arg(translationKey: String, init: MutableText.() -> Unit = {}) = ArgTranslation(translationKey, init)
    }
}

class UnitTranslation(translationKey: String, private val init: MutableText.() -> Unit) : Translation(translationKey) {
    val text: Text = text()
    fun text(): MutableText = Text.translatable(translationKey).apply(init)
}

class ArgTranslation(translationKey: String, private val init: MutableText.() -> Unit) : Translation(translationKey) {
    fun text(vararg args: Any): MutableText = Text.translatable(translationKey, *args).apply(init)
}

fun TranslationBuilder.add(translation: Translation, value: String) {
    add(translation.translationKey, value)
}
