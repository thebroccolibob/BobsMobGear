package io.github.thebroccolibob.bobsmobgear.client.util

import com.google.gson.JsonObject
import io.github.thebroccolibob.bobsmobgear.util.mapToJson
import net.minecraft.client.render.model.json.ModelOverride
import net.minecraft.data.client.*
import net.minecraft.data.client.Model.JsonFactory
import net.minecraft.data.client.VariantSettings.Rotation
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

fun BlockStateVariant(
    model: Identifier,
    x: Rotation? = null,
    y: Rotation? = null,
    uvLock: Boolean? = null,
    weight: Int? = null
) = BlockStateVariant().apply {
    put(VariantSettings.MODEL, model)
    x?.let { put(VariantSettings.X, it) }
    y?.let { put(VariantSettings.Y, it) }
    uvLock?.let { put(VariantSettings.UVLOCK, it) }
    weight?.let { put(VariantSettings.WEIGHT, it) }
}

fun variantYRotation(direction: Direction): Rotation? = when (direction) {
    Direction.EAST -> Rotation.R90
    Direction.SOUTH -> Rotation.R180
    Direction.WEST -> Rotation.R270
    else -> null
}

fun ItemModelGenerator.register(
    item: Item,
    model: Model,
    modelSuffix: String? = null,
    textureSuffix: String? = null,
    jsonFactory: JsonFactory = JsonFactory { id, textures -> model.createJson(id, textures) }
): Identifier =
    model.upload(
        modelSuffix?.let { ModelIds.getItemSubModelId(item, it) } ?: ModelIds.getItemModelId(item),
        TextureMap.layer0(textureSuffix?.let { TextureMap.getSubId(item, it) } ?: TextureMap.getId(item)),
        writer,
        jsonFactory,
    )

operator fun ModelOverride.Condition.component1(): Identifier = type
operator fun ModelOverride.Condition.component2() = threshold

fun ModelOverride.toJson() = JsonObject().apply {
    addProperty("model", modelId.toString())
    add("predicate", JsonObject().apply {
        for ((predicate, value) in streamConditions())
            addProperty(predicate.toString(), value)
    })
}

fun ModelOverride(modelId: Identifier, vararg conditions: Pair<Identifier, Float>) = ModelOverride(modelId, conditions.map {
    (type, threshold) -> ModelOverride.Condition(type, threshold)
})

fun ItemModelGenerator.register(id: Identifier, model: Model, textures: TextureMap, vararg overrides: ModelOverride): Identifier =
    model.upload(id, textures, writer) {
        id, consumer ->
        model.createJson(id, consumer).apply {
            add("overrides", overrides.asIterable().mapToJson(ModelOverride::toJson))
        }
    }

fun ItemModelGenerator.register(item: Item, suffix: String, model: Model, vararg overrides: ModelOverride) {
    register(ModelIds.getItemSubModelId(item, suffix), model, TextureMap.layer0(TextureMap.getSubId(item, suffix)), *overrides)
}

fun ItemModelGenerator.register(item: Item, model: Model, vararg overrides: ModelOverride) {
    register(ModelIds.getItemModelId(item), model, TextureMap.layer0(item), *overrides)
}

fun ItemModelGenerator.register(item: Item, model: Model, vararg overrides: ModelOverride, modelSuffix: String? = null, textureSuffix: String? = null) {
    register(
        modelSuffix?.let { ModelIds.getItemSubModelId(item, it) } ?: ModelIds.getItemModelId(item),
        model,
        TextureMap.layer0(textureSuffix?.let { TextureMap.getSubId(item, it) } ?: TextureMap.getId(item)),
        *overrides
    )
}
