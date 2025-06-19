package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearGameEvents
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.GameEventTags
import net.minecraft.world.event.GameEvent
import java.util.concurrent.CompletableFuture

class GameEventTagGenerator(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider<GameEvent>(output, RegistryKeys.GAME_EVENT, registriesFuture) {
    override fun configure(wrapperLookup: RegistryWrapper.WrapperLookup) {
        getOrCreateTagBuilder(BobsMobGearGameEvents.CHARGES_WARDEN_FIST).apply {
            forceAddTag(GameEventTags.VIBRATIONS)
            forceAddTag(BobsMobGearGameEvents.SUPER_CHARGES_WARDEN_FIST)
            add(GameEvent.SCULK_SENSOR_TENDRILS_CLICKING.registryKey())
        }
        getOrCreateTagBuilder(BobsMobGearGameEvents.SUPER_CHARGES_WARDEN_FIST).apply {
            add(GameEvent.SHRIEK.registryKey())
        }
    }
}