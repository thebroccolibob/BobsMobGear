package io.github.thebroccolibob.bobsmobgear.datagen

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
        getOrCreateTagBuilder(BobsMobGearItems.SMITHING_HAMMER_TAG).add(
            Items.IRON_AXE
        )
        getOrCreateTagBuilder(BobsMobGearItems.TONG_HOLDABLE).apply {
            forceAddTag(ConventionalItemTags.MINING_TOOL_TOOLS)
            forceAddTag(ConventionalItemTags.MELEE_WEAPON_TOOLS)
            forceAddTag(ItemTags.SWORDS)
            forceAddTag(ItemTags.PICKAXES)
            forceAddTag(ItemTags.AXES)
            forceAddTag(ItemTags.SHOVELS)
            forceAddTag(ItemTags.HOES)
        }
        getOrCreateTagBuilder(BobsMobGearItems.FORGES_IRON_INGOT).apply {
            forceAddTag(ConventionalItemTags.IRON_ORES)
            forceAddTag(ConventionalItemTags.IRON_RAW_MATERIALS)
            forceAddTag(ConventionalItemTags.IRON_INGOTS)
        }
        getOrCreateTagBuilder(BobsMobGearItems.FORGES_DIAMOND).apply {
            forceAddTag(ConventionalItemTags.DIAMOND_ORES)
            forceAddTag(ConventionalItemTags.DIAMOND_GEMS)
        }
        getOrCreateTagBuilder(BobsMobGearItems.FORGES_GOLD_INGOT).apply {
            forceAddTag(ConventionalItemTags.GOLD_ORES)
            forceAddTag(ConventionalItemTags.GOLD_RAW_MATERIALS)
            forceAddTag(ConventionalItemTags.GOLD_INGOTS)
        }
        getOrCreateTagBuilder(BobsMobGearItems.FORGES_NETHERITE_SCRAP).apply {
            forceAddTag(ConventionalItemTags.NETHERITE_SCRAP_ORES)
            add(Items.NETHERITE_SCRAP)
        }
        getOrCreateTagBuilder(BobsMobGearItems.FORGES_NETHERITE_INGOT).apply {
            forceAddTag(ConventionalItemTags.NETHERITE_INGOTS)
        }
        getOrCreateTagBuilder(ConventionalItemTags.MELEE_WEAPON_TOOLS).add(
            BobsMobGearItems.FLESH_GLOVE,
            BobsMobGearItems.IRON_FLESH_GLOVE,
        )
    }
}
