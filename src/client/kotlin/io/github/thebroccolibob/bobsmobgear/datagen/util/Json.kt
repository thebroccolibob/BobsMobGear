package io.github.thebroccolibob.bobsmobgear.datagen.util

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

fun JsonObject(init: JsonObject.() -> Unit) = JsonObject().apply(init)

fun jsonArrayOf(vararg elements: JsonElement) = JsonArray().apply { for (element in elements) add(element) }
fun jsonArrayOf(vararg elements: Number) = JsonArray().apply { for (element in elements) add(element) }
fun jsonArrayOf(vararg elements: String) = JsonArray().apply { for (element in elements) add(element) }
fun jsonArrayOf(vararg elements: Boolean) = JsonArray().apply { for (element in elements) add(element) }
fun jsonArrayOf(vararg elements: Char) = JsonArray().apply { for (element in elements) add(element) }
