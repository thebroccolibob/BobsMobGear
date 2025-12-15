package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.client.util.cataclysmId
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearDatagenItems
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItemTags
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.ItemTags
import net.spell_engine.api.item.Equipment
import net.spell_engine.rpg_series.tags.RPGSeriesItemTags
import java.util.concurrent.CompletableFuture

class ItemTagGenerator(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider.ItemTagProvider(output, registriesFuture) {
    override fun configure(wrapperLookup: RegistryWrapper.WrapperLookup) {
        with(getOrCreateTagBuilder(BobsMobGearItemTags.SMITHING_HAMMERS)) {
            add(BobsMobGearItems.SMITHING_HAMMER)
        }
        with(getOrCreateTagBuilder(BobsMobGearItemTags.HAMMERS)) {
            forceAddTag(BobsMobGearItemTags.SMITHING_HAMMERS)
            add(Items.MACE)
            addOptionalTag(RPGSeriesItemTags.WeaponType.ALL[Equipment.WeaponType.HAMMER])
        }
        with(getOrCreateTagBuilder(BobsMobGearItemTags.TONG_HOLDABLE)) {
            forceAddTag(ConventionalItemTags.MINING_TOOL_TOOLS)
            forceAddTag(ConventionalItemTags.MELEE_WEAPON_TOOLS)
            forceAddTag(ItemTags.SWORDS)
            forceAddTag(ItemTags.PICKAXES)
            forceAddTag(ItemTags.AXES)
            forceAddTag(ItemTags.SHOVELS)
            forceAddTag(ItemTags.HOES)
            add(Items.IRON_INGOT, Items.DIAMOND, Items.NETHERITE_INGOT)
        }
        with(getOrCreateTagBuilder(BobsMobGearItemTags.FORGES_IRON_INGOT)) {
            forceAddTag(ConventionalItemTags.IRON_ORES)
            forceAddTag(ConventionalItemTags.IRON_RAW_MATERIALS)
            forceAddTag(ConventionalItemTags.IRON_INGOTS)
        }
        with(getOrCreateTagBuilder(BobsMobGearItemTags.FORGES_DIAMOND)) {
            forceAddTag(ConventionalItemTags.DIAMOND_ORES)
            forceAddTag(ConventionalItemTags.DIAMOND_GEMS)
        }
        with(getOrCreateTagBuilder(BobsMobGearItemTags.FORGES_GOLD_INGOT)) {
            forceAddTag(ConventionalItemTags.GOLD_ORES)
            forceAddTag(ConventionalItemTags.GOLD_RAW_MATERIALS)
            forceAddTag(ConventionalItemTags.GOLD_INGOTS)
        }
        with(getOrCreateTagBuilder(BobsMobGearItemTags.FORGES_NETHERITE_SCRAP)) {
            forceAddTag(ConventionalItemTags.NETHERITE_SCRAP_ORES)
            add(Items.NETHERITE_SCRAP)
        }
        with(getOrCreateTagBuilder(BobsMobGearItemTags.FORGES_NETHERITE_INGOT)) {
            forceAddTag(ConventionalItemTags.NETHERITE_INGOTS)
        }
        getOrCreateTagBuilder(BobsMobGearItemTags.FORGES_BLACK_STEEL_INGOT).addOptional(
            Registries.ITEM.getId(BobsMobGearDatagenItems.BLACK_STEEL_INGOT),
        )
        with(getOrCreateTagBuilder(BobsMobGearItemTags.PREVENT_SMITHING_TABLE_SCREEN)) {
            add(BobsMobGearItems.SMITHING_TONGS)
            forceAddTag(BobsMobGearItemTags.SMITHING_HAMMERS)
        }
        with(getOrCreateTagBuilder(BobsMobGearItemTags.MENDER_ENCHANTABLE)) {
            forceAddTag(BobsMobGearItemTags.HAMMERS)
        }
        with(getOrCreateTagBuilder(BobsMobGearItemTags.MENDER_ENCHANTABLE_PRIMARY)) {
            forceAddTag(BobsMobGearItemTags.SMITHING_HAMMERS)
        }
        with(getOrCreateTagBuilder(BobsMobGearItemTags.SMITHING_SURFACE)) {
            add(Items.SMITHING_TABLE)
            forceAddTag(ItemTags.ANVIL)
            addOptional(cataclysmId("mechanical_fusion_anvil"))
        }
        with(getOrCreateTagBuilder(BobsMobGearItemTags.NON_FORGE_FUEL)) {
            forceAddTag(ConventionalItemTags.TOOLS)
        }
        getOrCreateTagBuilder(BobsMobGearItemTags.LOWER_USE_PRIORITY).add(
            BobsMobGearItems.IRON_FLESH_GLOVE,
            BobsMobGearItems.IRON_SPIDER_DAGGER,
            BobsMobGearItems.IRON_ENDER_SPEAR,
        )
        with(getOrCreateTagBuilder(BobsMobGearItemTags.NOT_WEAPON)) {
            forceAddTag(BobsMobGearItemTags.SMITHING_HAMMERS)
        }
        getOrCreateTagBuilder(BobsMobGearItemTags.WEAK_HEAT_SOURCES).add(
            Items.FLINT_AND_STEEL,
            Items.CAMPFIRE,
            Items.SOUL_CAMPFIRE,
            Items.MAGMA_BLOCK,
            Items.LAVA_BUCKET,
            Items.CAULDRON,
        )
        getOrCreateTagBuilder(ConventionalItemTags.MELEE_WEAPON_TOOLS).add(
            BobsMobGearItems.FLESH_GLOVE,
            BobsMobGearItems.IRON_FLESH_GLOVE,
            BobsMobGearItems.WARDEN_FIST,
            BobsMobGearItems.IRON_SPIDER_DAGGER,
            BobsMobGearItems.IRON_BONE_HAMMER,
            BobsMobGearItems.IRON_ENDER_SPEAR,
            BobsMobGearItems.IRON_ENDER_EYE_SPEAR,
        )
        with (getOrCreateTagBuilder(ConventionalItemTags.TOOLS)) {
            add(BobsMobGearItems.SMITHING_TONGS)
            forceAddTag(BobsMobGearItemTags.SMITHING_HAMMERS)
        }
        getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE).add(
            BobsMobGearItems.SMITHING_HAMMER,
            BobsMobGearItems.FLESH_GLOVE,
            BobsMobGearItems.IRON_FLESH_GLOVE,
            BobsMobGearItems.WARDEN_FIST,
            BobsMobGearItems.IRON_SPIDER_DAGGER,
            BobsMobGearItems.IRON_BONE_HAMMER,
            BobsMobGearItems.IRON_ENDER_SPEAR,
            BobsMobGearItems.IRON_ENDER_EYE_SPEAR,
        )
    }
}
