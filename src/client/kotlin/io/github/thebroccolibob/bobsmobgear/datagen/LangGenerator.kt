package io.github.thebroccolibob.bobsmobgear.datagen

import dev.emi.emi.api.recipe.EmiRecipeCategory
import io.github.thebroccolibob.bobsmobgear.client.HeatedTooltip
import io.github.thebroccolibob.bobsmobgear.client.SonicChargeTooltip
import io.github.thebroccolibob.bobsmobgear.client.UsePriorityTooltip
import io.github.thebroccolibob.bobsmobgear.client.emi.BobsMobGearEmiPlugin
import io.github.thebroccolibob.bobsmobgear.item.TongsItem
import io.github.thebroccolibob.bobsmobgear.item.UnlimitedBaconItem
import io.github.thebroccolibob.bobsmobgear.registry.*
import io.github.thebroccolibob.bobsmobgear.util.add
import io.github.thebroccolibob.bobsmobgear.util.value
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.entity.damage.DamageType
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.fluid.Fluid
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Util.createTranslationKey
import java.util.concurrent.CompletableFuture

class LangGenerator(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>) :
    FabricLanguageProvider(dataOutput, registryLookup) {

    override fun generateTranslations(
        registryLookup: RegistryWrapper.WrapperLookup,
        translationBuilder: TranslationBuilder
    ) = with(translationBuilder) {

        val damageTypes = registryLookup.getWrapperOrThrow(RegistryKeys.DAMAGE_TYPE)
        fun add(damageType: RegistryKey<DamageType>, base: String? = null, player: String? = null, item: String? = null) {
            add(damageTypes.getOrThrow(damageType).value(), base, player, item)
        }

        add(BobsMobGearBlocks.EMPTY_TEMPLATE, "Empty Template")
        add(BobsMobGearBlocks.SWORD_TEMPLATE, "Sword Template")
        add(BobsMobGearBlocks.PICKAXE_TEMPLATE, "Pickaxe Template")
        add(BobsMobGearBlocks.AXE_TEMPLATE, "Axe Template")
        add(BobsMobGearBlocks.SHOVEL_TEMPLATE, "Shovel Template")
        add(BobsMobGearBlocks.HOE_TEMPLATE, "Hoe Template")
        add(BobsMobGearBlocks.GREATHAMMER_TEMPLATE, "Great Hammer Template")
        add(BobsMobGearBlocks.MACE_TEMPLATE, "Mace Template")
        add(BobsMobGearBlocks.CLAYMORE_TEMPLATE, "Claymore Template")
        add(BobsMobGearBlocks.KITE_SHIELD_TEMPLATE, "Kite Shield Template")
        add(BobsMobGearBlocks.DAGGER_TEMPLATE, "Dagger Template")
        add(BobsMobGearBlocks.GLAIVE_TEMPLATE, "Glaive Template")
        add(BobsMobGearBlocks.SICKLE_TEMPLATE, "Sickle Template")
        add(BobsMobGearBlocks.DOUBLE_AXE_TEMPLATE, "Double Axe Template")
        add(BobsMobGearBlocks.SPEAR_TEMPLATE, "Spear Template")
        add(BobsMobGearBlocks.KNIFE_TEMPLATE, "Knife Template")

        add(BobsMobGearBlocks.FORGE, "Forge")
        add(BobsMobGearBlocks.FORGE_HEATER, "Forge Heater")

        add(BobsMobGearItems.SMITHING_HAMMER, "Smithing Hammer")
        add(BobsMobGearItems.SMITHING_TONGS, "Smithing Tongs")
        add(BobsMobGearItems.EMPTY_POT, "Empty Pot")
        add(BobsMobGearItems.IRON_POT, "Pot of Molten Iron")
        add(BobsMobGearItems.DIAMOND_POT, "Pot of Molten Diamond")
        add(BobsMobGearItems.NETHERITE_POT, "Pot of Molten Netherite")
        add(BobsMobGearItems.WARDEN_FIST, "Warden Fist")
        add(BobsMobGearItems.UNLIMITED_BACON, "Unlimited Bacon")
        add(UnlimitedBaconItem.KICK_REASON, "...but no games")

        add(BobsMobGearItemTags.SMITHING_HAMMERS, "Smithing Hammers")
        add(BobsMobGearItemTags.TONG_HOLDABLE, "Holdable by Tongs")
        add(BobsMobGearItemTags.FORGES_IRON_INGOT, "Forges Iron Ingot")
        add(BobsMobGearItemTags.FORGES_DIAMOND, "Forges Diamond")
        add(BobsMobGearItemTags.FORGES_GOLD_INGOT, "Forges Gold Ingot")
        add(BobsMobGearItemTags.FORGES_NETHERITE_INGOT, "Forges Netherite Ingot")
        add(BobsMobGearItemTags.FORGES_NETHERITE_SCRAP, "Forges Netherite Scrap")
        add(BobsMobGearItemTags.FORGES_BLACK_STEEL_INGOT, "Forges Black Steel Ingot")
        add(BobsMobGearItemTags.SMITHING_SURFACE, "Smithing Surfaces")
        add(BobsMobGearItemTags.MENDER_ENCHANTABLE, "Mender Enchantable")
        add(BobsMobGearItemTags.LOWER_USE_PRIORITY, "Lower Use Priority")

        add(BobsMobGearFluids.IRON, "Molten Iron")
        add(BobsMobGearFluids.DIAMOND, "Molten Diamond")
        add(BobsMobGearFluids.NETHERITE, "Molten Netherite")
        add(BobsMobGearFluids.BLACK_STEEL, "Molten Black Steel")

        add(HeatedTooltip.TOOLTIP, "Heated")
        add(TongsItem.HELD_ITEM_TOOLTIP, "Held Item:")
        add(SonicChargeTooltip.TOOLTIP, "Sonic Charge: %s/%s")
        add(UsePriorityTooltip.TOOLTIP, "Lower use priority")

        add(BobsMobGearSounds.TEMPLATE_CRAFT, "Tool crafts")
        add(BobsMobGearSounds.TEMPLATE_HAMMER, "Tool hammers")
        add(BobsMobGearSounds.TEMPLATE_ADD_ITEM, "Template fills")
        add(BobsMobGearSounds.TEMPLATE_REMOVE_ITEM, "Template empties")
        add(BobsMobGearSounds.FORGE_HEATER_FUEL, "Forge heater ignites")
        add(BobsMobGearSounds.WEAPON_ATTACK_READY, "Weapon readies")
        add(BobsMobGearSounds.EQUIPMENT_REPAIR, "Hammer mends")
        add(BobsMobGearSounds.TONGS_PICKUP, "Tongs grab")
        add(BobsMobGearSounds.TONGS_DROP, "Tongs drop")

        add(BobsMobGearEnchantments.MENDER_NAME, "Mender")

        add(BobsMobGearDamageTypes.PROJECTILE_TELEFRAG,
            base = "%s was telefragged by %s",
            item = "%s was telefragged by %s using %s",
        )
        add(BobsMobGearDamageTypes.SELF_TELEFRAG,
            base = "%s was telefragged",
            player = "%s was telefragged while fighting %s",
        )

        add(BobsMobGearEffects.BRUISED, "Bruised")
        add(BobsMobGearEffects.BROKEN, "Broken")

        add(BobsMobGearEmiPlugin.TEMPLATE_CATEGORY, "Template Smithing")
        add(BobsMobGearEmiPlugin.FORGING_CATEGORY, "Forging")
        add(BobsMobGearEmiPlugin.FORGE_FILLING_CATEGORY, "Forge Filling")
    }

    companion object {
        fun TranslationBuilder.add(sound: SoundEvent, subtitle: String) {
            add(SoundsGenerator.subtitleOf(sound), subtitle)
        }

        fun TranslationBuilder.add(fluid: Fluid, value: String) {
            add(createTranslationKey("block", Registries.FLUID.getId(fluid)), value)
        }

        fun TranslationBuilder.add(effect: RegistryEntry<StatusEffect>, value: String) {
            add(effect.value, value)
        }

        fun TranslationBuilder.add(category: EmiRecipeCategory, value: String) {
            add(createTranslationKey("emi.category", category.getId()), value)
        }

        fun TranslationBuilder.add(damageType: DamageType, suffix: String? = null, value: String) {
            add("death.attack.${damageType.msgId}${ if (suffix == null) "" else ".$suffix" }", value)
        }

        fun TranslationBuilder.add(damageType: DamageType, base: String? = null, player: String? = null, item: String? = null) {
            base?.let { add(damageType, null, it) }
            player?.let { add(damageType, "player", it) }
            item?.let { add(damageType, "item", it) }
        }
    }
}
