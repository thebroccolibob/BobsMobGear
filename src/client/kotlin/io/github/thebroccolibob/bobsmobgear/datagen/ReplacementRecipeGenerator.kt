package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat.ARCHERS
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat.PALADINS
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat.ROGUES
import io.github.thebroccolibob.bobsmobgear.datagen.util.ToolType
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearDatagenItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.server.recipe.RecipeExporter
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

import net.archers.item.Weapons as ArchersWeapons
import net.paladins.item.Shields as PaladinsShields
import net.paladins.item.Weapons as PaladinsWeapons
import net.rogues.item.Weapons as RoguesWeapons
import net.spell_engine.api.item.weapon.Weapon as SpellWeapon
import vectorwing.farmersdelight.common.registry.ModItems as FarmersDelightItems

class ReplacementRecipeGenerator(
    output: FabricDataOutput,
    private val registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricRecipeProvider(output, registriesFuture) {
    override fun generate(exporter: RecipeExporter) {
        val smithingSurface = registriesFuture.get().getWrapperOrThrow(RegistryKeys.BLOCK).getOrThrow(BobsMobGearBlocks.SMITHING_SURFACE)

        for (toolType in TOOL_TYPES)
            toolType.register(exporter, smithingSurface, ::withConditions)
    }

    companion object {
        val TOOL_TYPES = listOf(
            ToolType(
                2,
                Items.WOODEN_SWORD,
                Items.STONE_SWORD,
                Items.IRON_SWORD,
                Items.DIAMOND_SWORD,
                Items.NETHERITE_SWORD,
                BobsMobGearBlocks.SWORD_TEMPLATE,
                blackSteel = BobsMobGearDatagenItems.BLACK_STEEL_SWORD,
            ),
            ToolType(
                3,
                Items.WOODEN_PICKAXE,
                Items.STONE_PICKAXE,
                Items.IRON_PICKAXE,
                Items.DIAMOND_PICKAXE,
                Items.NETHERITE_PICKAXE,
                BobsMobGearBlocks.PICKAXE_TEMPLATE,
                blackSteel = BobsMobGearDatagenItems.BLACK_STEEL_PICKAXE,
            ),
            ToolType(
                3,
                Items.WOODEN_AXE,
                Items.STONE_AXE,
                Items.IRON_AXE,
                Items.DIAMOND_AXE,
                Items.NETHERITE_AXE,
                BobsMobGearBlocks.AXE_TEMPLATE,
                blackSteel = BobsMobGearDatagenItems.BLACK_STEEL_AXE,
            ),
            ToolType(
                1,
                Items.WOODEN_SHOVEL,
                Items.STONE_SHOVEL,
                Items.IRON_SHOVEL,
                Items.DIAMOND_SHOVEL,
                Items.NETHERITE_SHOVEL,
                BobsMobGearBlocks.SHOVEL_TEMPLATE,
                blackSteel = BobsMobGearDatagenItems.BLACK_STEEL_SHOVEL,
            ),
            ToolType(
                2,
                Items.WOODEN_HOE,
                Items.STONE_HOE,
                Items.IRON_HOE,
                Items.DIAMOND_HOE,
                Items.NETHERITE_HOE,
                BobsMobGearBlocks.HOE_TEMPLATE,
                blackSteel = BobsMobGearDatagenItems.BLACK_STEEL_HOE,
            ),

            ToolType(
                4,
                PaladinsWeapons.wooden_great_hammer.item,
                PaladinsWeapons.stone_great_hammer.item,
                PaladinsWeapons.iron_great_hammer.item,
                PaladinsWeapons.diamond_great_hammer.item,
                PaladinsWeapons.netherite_great_hammer.item,
                BobsMobGearBlocks.GREATHAMMER_TEMPLATE,
                PALADINS,
            ),
            ToolType(
                2,
                Items.WOODEN_SHOVEL, // TODO
                null,
                PaladinsWeapons.iron_mace.item,
                PaladinsWeapons.diamond_mace.item,
                PaladinsWeapons.netherite_mace.item,
                BobsMobGearBlocks.MACE_TEMPLATE,
                PALADINS,
            ),
            ToolType(
                4,
                Items.WOODEN_SWORD, // TODO
                PaladinsWeapons.stone_claymore.item,
                PaladinsWeapons.iron_claymore.item,
                PaladinsWeapons.diamond_claymore.item,
                PaladinsWeapons.netherite_claymore.item,
                BobsMobGearBlocks.CLAYMORE_TEMPLATE,
                PALADINS,
            ),
            ToolType(
                6,
                Items.SHIELD,
                null,
                Registries.ITEM.get(PaladinsShields.iron_kite_shield.id),
                Registries.ITEM.get(PaladinsShields.diamond_kite_shield.id),
                Registries.ITEM.get(PaladinsShields.netherite_kite_shield.id),
                BobsMobGearBlocks.KITE_SHIELD_TEMPLATE,
                PALADINS,
                Items.LEATHER
            ),
            ToolType(
                1,
                RoguesWeapons.flint_dagger.item,
                null,
                RoguesWeapons.iron_dagger.item,
                RoguesWeapons.diamond_dagger.item,
                RoguesWeapons.netherite_dagger.item,
                BobsMobGearBlocks.DAGGER_TEMPLATE,
                ROGUES,
            ),
            ToolType(
                3,
                Items.WOODEN_AXE, // TODO
                null,
                RoguesWeapons.iron_glaive.item,
                RoguesWeapons.diamond_glaive.item,
                RoguesWeapons.netherite_glaive.item,
                BobsMobGearBlocks.GLAIVE_TEMPLATE,
                ROGUES,
            ),
            ToolType(
                2,
                Items.WOODEN_HOE, // TODO
                null,
                RoguesWeapons.iron_sickle.item,
                RoguesWeapons.diamond_sickle.item,
                RoguesWeapons.netherite_sickle.item,
                BobsMobGearBlocks.SICKLE_TEMPLATE,
                ROGUES,
            ),
            ToolType(
                4,
                Items.WOODEN_AXE, // TODO
                RoguesWeapons.stone_double_axe.item,
                RoguesWeapons.iron_double_axe.item,
                RoguesWeapons.diamond_double_axe.item,
                RoguesWeapons.netherite_double_axe.item,
                BobsMobGearBlocks.DOUBLE_AXE_TEMPLATE,
                ROGUES,
            ),
            ToolType(
                1,
                ArchersWeapons.flint_spear.item,
                null,
                ArchersWeapons.iron_spear.item,
                ArchersWeapons.diamond_spear.item,
                ArchersWeapons.netherite_spear.item,
                BobsMobGearBlocks.SPEAR_TEMPLATE,
                ARCHERS,
            ),
            ToolType(
                1,
                FarmersDelightItems.FLINT_KNIFE.get(),
                null,
                FarmersDelightItems.IRON_KNIFE.get(),
                FarmersDelightItems.DIAMOND_KNIFE.get(),
                FarmersDelightItems.NETHERITE_KNIFE.get(),
                BobsMobGearBlocks.KNIFE_TEMPLATE,
                BobsMobGearCompat.FARMERS_DELIGHT,
            )
        )

        val SpellWeapon.Entry.item get() = item()!!
    }
}
