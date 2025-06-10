package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGearClient
import io.github.thebroccolibob.bobsmobgear.client.SonicChargeTooltip
import io.github.thebroccolibob.bobsmobgear.item.TongsItem
import io.github.thebroccolibob.bobsmobgear.registry.*
import io.github.thebroccolibob.bobsmobgear.util.add
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.fluid.Fluid
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryWrapper
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Util.createTranslationKey
import java.util.concurrent.CompletableFuture

class LangGenerator(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>) :
    FabricLanguageProvider(dataOutput, registryLookup) {

    override fun generateTranslations(
        registryLookup: RegistryWrapper.WrapperLookup,
        translationBuilder: TranslationBuilder
    ) = with(translationBuilder) {
        add(BobsMobGearBlocks.EMPTY_TEMPLATE, "Empty Template")
        add(BobsMobGearBlocks.SWORD_TEMPLATE, "Sword Template")
        add(BobsMobGearBlocks.PICKAXE_TEMPLATE, "Pickaxe Template")
        add(BobsMobGearBlocks.AXE_TEMPLATE, "Axe Template")
        add(BobsMobGearBlocks.SHOVEL_TEMPLATE, "Shovel Template")
        add(BobsMobGearBlocks.HOE_TEMPLATE, "Hoe Template")

        add(BobsMobGearBlocks.FORGE, "Forge")
        add(BobsMobGearBlocks.FORGE_HEATER, "Forge Heater")

        add(BobsMobGearItems.SMITHING_HAMMER, "Smithing Hammer")
        add(BobsMobGearItems.SMITHING_TONGS, "Smithing Tongs")

        add(BobsMobGearItems.EMPTY_POT, "Empty Pot")
        add(BobsMobGearItems.IRON_POT, "Pot of Molten Iron")
        add(BobsMobGearItems.DIAMOND_POT, "Pot of Molten Diamond")
        add(BobsMobGearItems.NETHERITE_POT, "Pot of Molten Netherite")

        add(BobsMobGearItemTags.SMITHING_HAMMERS, "Smithing Hammers")
        add(BobsMobGearItemTags.TONG_HOLDABLE, "Holdable by Tongs")
        add(BobsMobGearItemTags.FORGES_IRON_INGOT, "Forges Iron Ingot")
        add(BobsMobGearItemTags.FORGES_DIAMOND, "Forges Diamond")
        add(BobsMobGearItemTags.FORGES_GOLD_INGOT, "Forges Gold Ingot")
        add(BobsMobGearItemTags.FORGES_NETHERITE_INGOT, "Forges Netherite Ingot")
        add(BobsMobGearItemTags.FORGES_NETHERITE_SCRAP, "Forges Netherite Scrap")

        add(BobsMobGearFluids.IRON, "Molten Iron")
        add(BobsMobGearFluids.DIAMOND, "Molten Diamond")
        add(BobsMobGearFluids.NETHERITE, "Molten Netherite")

        add(BobsMobGearClient.HEATED_TOOLTIP, "Heated")
        add(TongsItem.HELD_ITEM_TOOLTIP, "Held Item:")
        add(SonicChargeTooltip.TOOLTIP, "Sonic Charge: %s/%s")

        add(BobsMobGearSounds.TEMPLATE_CRAFT, "Tool crafts")
        add(BobsMobGearSounds.TEMPLATE_HAMMER, "Tool hammers")
        add(BobsMobGearSounds.TEMPLATE_ADD_ITEM, "Template fills")
        add(BobsMobGearSounds.TEMPLATE_REMOVE_ITEM, "Template empties")

        add(BobsMobGearEnchantments.MENDER_NAME, "Mender")
    }

    companion object {
        fun TranslationBuilder.add(sound: SoundEvent, subtitle: String) {
            add(SoundsGenerator.subtitleOf(sound), subtitle)
        }

        fun TranslationBuilder.add(fluid: Fluid, value: String) {
            add(createTranslationKey("block", Registries.FLUID.getId(fluid)), value)
        }
    }
}
