package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.entity.WebShotEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object BobsMobGearEntities {
    private fun <T: Entity> register(id: Identifier, type: EntityType<T>): EntityType<T> =
        Registry.register(Registries.ENTITY_TYPE, id, type)

    private fun <T: Entity> register(path: String, type: EntityType<T>) =
        register(BobsMobGear.id(path), type)

    private inline fun <T: Entity> register(path: String, factory: EntityType.EntityFactory<T>, spawnGroup: SpawnGroup = SpawnGroup.MISC, init: EntityType.Builder<T>.() -> Unit = {}) =
        register(path, EntityType.Builder.create(factory, spawnGroup).apply(init).build())

    val WEB_SHOT = register("web_shot", ::WebShotEntity) {
        dimensions(0.25f, 0.25f);
        disableSaving()
        disableSummon()
        maxTrackingRange(4)
        trackingTickInterval(5)
    }

    fun register() {}
}