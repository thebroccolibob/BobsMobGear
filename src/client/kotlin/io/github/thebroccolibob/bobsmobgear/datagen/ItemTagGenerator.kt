package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItemTags
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.item.Items
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.ItemTags
import java.util.concurrent.CompletableFuture

class ItemTagGenerator(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider.ItemTagProvider(output, registriesFuture) {
    override fun configure(wrapperLookup: RegistryWrapper.WrapperLookup) {
        getOrCreateTagBuilder(BobsMobGearItemTags.SMITHING_HAMMERS).add(
            BobsMobGearItems.SMITHING_HAMMER
        )
        getOrCreateTagBuilder(BobsMobGearItemTags.TONG_HOLDABLE).apply {
            forceAddTag(ConventionalItemTags.MINING_TOOL_TOOLS)
            forceAddTag(ConventionalItemTags.MELEE_WEAPON_TOOLS)
            forceAddTag(ItemTags.SWORDS)
            forceAddTag(ItemTags.PICKAXES)
            forceAddTag(ItemTags.AXES)
            forceAddTag(ItemTags.SHOVELS)
            forceAddTag(ItemTags.HOES)
        }
        getOrCreateTagBuilder(BobsMobGearItemTags.FORGES_IRON_INGOT).apply {
            forceAddTag(ConventionalItemTags.IRON_ORES)
            forceAddTag(ConventionalItemTags.IRON_RAW_MATERIALS)
            forceAddTag(ConventionalItemTags.IRON_INGOTS)
        }
        getOrCreateTagBuilder(BobsMobGearItemTags.FORGES_DIAMOND).apply {
            forceAddTag(ConventionalItemTags.DIAMOND_ORES)
            forceAddTag(ConventionalItemTags.DIAMOND_GEMS)
        }
        getOrCreateTagBuilder(BobsMobGearItemTags.FORGES_GOLD_INGOT).apply {
            forceAddTag(ConventionalItemTags.GOLD_ORES)
            forceAddTag(ConventionalItemTags.GOLD_RAW_MATERIALS)
            forceAddTag(ConventionalItemTags.GOLD_INGOTS)
        }
        getOrCreateTagBuilder(BobsMobGearItemTags.FORGES_NETHERITE_SCRAP).apply {
            forceAddTag(ConventionalItemTags.NETHERITE_SCRAP_ORES)
            add(Items.NETHERITE_SCRAP)
        }
        getOrCreateTagBuilder(BobsMobGearItemTags.FORGES_NETHERITE_INGOT).apply {
            forceAddTag(ConventionalItemTags.NETHERITE_INGOTS)
        }
        getOrCreateTagBuilder(BobsMobGearItemTags.PREVENT_SMITHING_TABLE_SCREEN).apply {
            add(BobsMobGearItems.SMITHING_TONGS)
            forceAddTag(BobsMobGearItemTags.SMITHING_HAMMERS)
        }
        getOrCreateTagBuilder(ConventionalItemTags.MELEE_WEAPON_TOOLS).add(
            BobsMobGearItems.FLESH_GLOVE,
            BobsMobGearItems.IRON_FLESH_GLOVE,
        )
        getOrCreateTagBuilder(ConventionalItemTags.TOOLS).add(
            BobsMobGearItems.SMITHING_HAMMER,
            BobsMobGearItems.SMITHING_TONGS,
        )
    }
}
