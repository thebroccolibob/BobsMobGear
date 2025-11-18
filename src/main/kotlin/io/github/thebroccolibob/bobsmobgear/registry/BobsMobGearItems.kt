package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat
import io.github.thebroccolibob.bobsmobgear.entity.EnderEyeSpearEntity
import io.github.thebroccolibob.bobsmobgear.entity.EnderSpearEntity
import io.github.thebroccolibob.bobsmobgear.item.*
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearComponents.MAX_SONIC_CHARGE
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearComponents.SONIC_CHARGE
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearComponents.TONGS_HELD_ITEM
import io.github.thebroccolibob.bobsmobgear.util.*
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Block
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ToolComponent
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.Fluids
import net.minecraft.item.*
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity

object BobsMobGearItems {
    private fun register(id: Identifier, item: Item): Item =
        Registry.register(Registries.ITEM, id, item)

    private fun register(path: String, item: Item) =
        register(BobsMobGear.id(path), item)

    private fun register(block: Block): Item = Items.register(block)

    private fun registerBucket(fluid: Fluid) =
        register(Registries.FLUID.getId(fluid) + "_bucket", BucketItem(fluid, itemSettings {
            recipeRemainder(Items.BUCKET)
            maxCount(1)
        }))

    @JvmStatic
    fun registerPot(fluid: Fluid) =
        register(Registries.FLUID.getId(fluid) + "_pot", FluidPotItem(fluid, itemSettings {
            recipeRemainder(EMPTY_POT)
            maxCount(1)
        }))

    @JvmField val EMPTY_TEMPLATE = register(BobsMobGearBlocks.EMPTY_TEMPLATE)
    @JvmField val SWORD_TEMPLATE = register(BobsMobGearBlocks.SWORD_TEMPLATE)
    @JvmField val PICKAXE_TEMPLATE = register(BobsMobGearBlocks.PICKAXE_TEMPLATE)
    @JvmField val AXE_TEMPLATE = register(BobsMobGearBlocks.AXE_TEMPLATE)
    @JvmField val SHOVEL_TEMPLATE = register(BobsMobGearBlocks.SHOVEL_TEMPLATE)
    @JvmField val HOE_TEMPLATE = register(BobsMobGearBlocks.HOE_TEMPLATE)

    @JvmField val GREATHAMMER_TEMPLATE = register(BobsMobGearBlocks.GREATHAMMER_TEMPLATE)
    @JvmField val MACE_TEMPLATE = register(BobsMobGearBlocks.MACE_TEMPLATE)
    @JvmField val CLAYMORE_TEMPLATE = register(BobsMobGearBlocks.CLAYMORE_TEMPLATE)
    @JvmField val KITE_SHIELD_TEMPLATE = register(BobsMobGearBlocks.KITE_SHIELD_TEMPLATE)
    @JvmField val DAGGER_TEMPLATE = register(BobsMobGearBlocks.DAGGER_TEMPLATE)
    @JvmField val GLAIVE_TEMPLATE = register(BobsMobGearBlocks.GLAIVE_TEMPLATE)
    @JvmField val SICKLE_TEMPLATE = register(BobsMobGearBlocks.SICKLE_TEMPLATE)
    @JvmField val DOUBLE_AXE_TEMPLATE = register(BobsMobGearBlocks.DOUBLE_AXE_TEMPLATE)
    @JvmField val SPEAR_TEMPLATE = register(BobsMobGearBlocks.SPEAR_TEMPLATE)
    @JvmField val KNIFE_TEMPLATE = register(BobsMobGearBlocks.KNIFE_TEMPLATE)

    @JvmField val FORGE = register(BobsMobGearBlocks.FORGE)
    @JvmField val FORGE_HEATER = register(BobsMobGearBlocks.FORGE_HEATER)

    @JvmField val EMPTY_POT = register("empty_pot", FluidPotItem(Fluids.EMPTY, itemSettings {
        maxCount(16)
    }))

    @JvmField val IRON_POT = registerPot(BobsMobGearFluids.IRON)
    @JvmField val DIAMOND_POT = registerPot(BobsMobGearFluids.DIAMOND)
    @JvmField val NETHERITE_POT = registerPot(BobsMobGearFluids.NETHERITE)
    @JvmField val BLACK_STEEL_POT = registerPot(BobsMobGearFluids.BLACK_STEEL)

    @JvmField val FILLED_POTS = listOf(IRON_POT, DIAMOND_POT, NETHERITE_POT, BLACK_STEEL_POT)

    @JvmField val SMITHING_HAMMER = register("smithing_hammer", SmithingHammerItem(itemSettings {
        maxDamage(128) // TODO decide max damage
        attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.IRON, 1, -3.2f))
        component(DataComponentTypes.TOOL, ToolComponent(listOf(), 1f, 2))
    }))

    @JvmField val SMITHING_TONGS = register("smithing_tongs", TongsItem(itemSettings {
        maxCount(1)
        component(TONGS_HELD_ITEM, ComparableItemStack.EMPTY)
    }))

    @JvmField val WORN_HARDENED_FLESH = register("worn_hardened_flesh", Item(itemSettings {}))
    @JvmField val WORN_STURDY_BONE = register("worn_sturdy_bone", Item(itemSettings {}))
    @JvmField val WORN_SPIDER_FANG = register("worn_spider_fang", Item(itemSettings {}))
    @JvmField val WORN_CREEPER_CORE = register("worn_creeper_core", Item(itemSettings {}))
    @JvmField val WORN_SEETHING_PEARL = register("worn_seething_pearl", Item(itemSettings {}))
    @JvmField val WORN_SEETHING_EYE = register("worn_seething_eye", Item(itemSettings {}))
    @JvmField val SCULK_SYMBIOTE = register("sculk_symbiote", Item(itemSettings {}))

    @JvmField val FLESH_GLOVE = register("flesh_glove",
        AbstractFleshGlove(
            FLESH_GLOVE_MATERIAL,
            itemSettings {
                maxCount(1)
                rarity(Rarity.COMMON)
            }
        )
    )

    @JvmField val IRON_FLESH_GLOVE = register("iron_flesh_glove",
        FleshGloveItem(
            ToolMaterials.IRON,
            itemSettings {
                maxCount(1)
                rarity(Rarity.COMMON)
            },
            0.3f
        )
    )

    @JvmField val WARDEN_FIST = register("warden_fist", WardenFistItem(itemSettings {
        rarity(Rarity.RARE)
        fireproof()
        attributeModifiers(WardenFistItem.createAttributeModifiers())
        component(MAX_SONIC_CHARGE, 16)
        component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false)
    }))

    @JvmField val IRON_BONE_HAMMER = register("iron_bone_hammer", BoneHammerItem(5 * 20, ToolMaterials.STONE, itemSettings { // TODO

    }))

    @JvmField val IRON_SPIDER_DAGGER = register("iron_spider_dagger", SpiderDaggerItem(2.0, ToolMaterials.STONE, itemSettings { // TODO

    }))

    @JvmField val IRON_ENDER_SPEAR = register("iron_ender_spear", EnderSpearItem.teleporting(3f, 5 * 20, ::EnderSpearEntity, ToolMaterials.STONE, itemSettings { // TODO

    }))

    @JvmField val IRON_ENDER_EYE_SPEAR = register("iron_ender_eye_spear", EnderSpearItem.homing(16.0, ::EnderEyeSpearEntity, ToolMaterials.STONE, itemSettings { // TODO

    }))

    @JvmField val IRON_BOOM_BATON = register("iron_boom_baton", BoomBatonItem(8, 5 * 20, BobsMobGearBlocks.WORN_GUNFLOWER, ToolMaterials.IRON, itemSettings {

    }))

    @JvmField val UNLIMITED_BACON = register("unlimited_bacon", UnlimitedBaconItem(itemSettings {
        component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
        food {
            alwaysEdible()
            nutrition(3)
            saturationModifier(0.3f)
        }
        maxCount(1)
        rarity(Rarity.RARE)
    }))

    // ITEM GROUPS

    @JvmField val ITEM_GROUP: ItemGroup = Registry.register(Registries.ITEM_GROUP, BobsMobGear.id("item_group"), ItemGroup {
        icon { SMITHING_HAMMER.defaultStack }
        displayName(Text.of(FabricLoader.getInstance().getModContainer(BobsMobGear.MOD_ID).orElseThrow().metadata.name))
        entries { _, entries ->
            entries.addAll(
                EMPTY_TEMPLATE,
                SWORD_TEMPLATE,
                PICKAXE_TEMPLATE,
                AXE_TEMPLATE,
                SHOVEL_TEMPLATE,
                HOE_TEMPLATE,
            )
            if (BobsMobGearCompat.PALADINS_INSTALLED)
                entries.addAll(
                    GREATHAMMER_TEMPLATE,
                    MACE_TEMPLATE,
                    CLAYMORE_TEMPLATE,
                    KITE_SHIELD_TEMPLATE,
                )
            if (BobsMobGearCompat.ROGUES_INSTALLED)
                entries.addAll(
                    DAGGER_TEMPLATE,
                    GLAIVE_TEMPLATE,
                    SICKLE_TEMPLATE,
                    DOUBLE_AXE_TEMPLATE,
                )
            if (BobsMobGearCompat.ARCHERS_INSTALLED)
                entries.addAll(
                    SPEAR_TEMPLATE,
                )
            if (BobsMobGearCompat.FARMERS_DELIGHT_INSTALLED)
                entries.addAll(
                    KNIFE_TEMPLATE,
                )
            entries.addAll(
                FORGE,
                FORGE_HEATER,
                EMPTY_POT,
                IRON_POT,
                DIAMOND_POT,
                NETHERITE_POT,
            )
            if (BobsMobGearCompat.CATACLYSM_INSTALLED)
                entries.add(BLACK_STEEL_POT)
            entries.addAll(
                SMITHING_HAMMER,
                SMITHING_TONGS,

                WORN_HARDENED_FLESH,
                WORN_STURDY_BONE,
                WORN_SPIDER_FANG,
                WORN_CREEPER_CORE,
                WORN_SEETHING_PEARL,
                WORN_SEETHING_EYE,
                SCULK_SYMBIOTE,

                FLESH_GLOVE,
                IRON_FLESH_GLOVE,
                IRON_SPIDER_DAGGER,
                IRON_BONE_HAMMER,
                IRON_ENDER_SPEAR,
                IRON_ENDER_EYE_SPEAR,
            )
            entries.addAll(
                WARDEN_FIST.defaultStack.also {
                    it[SONIC_CHARGE] = 16
                },
            )
        }
    })

    fun register() {
        FluidStorage.ITEM.registerForItems({ stack, context ->
            FullItemFluidStorage(context, EMPTY_POT, FluidVariant.of((stack.item as FluidPotItem).fluid), FluidConstants.INGOT)
        }, IRON_POT, DIAMOND_POT, NETHERITE_POT, BLACK_STEEL_POT)

        FluidStorage.combinedItemApiProvider(EMPTY_POT).run {
            FILLED_POTS.forEach {
                register { context ->
                    EmptyItemFluidStorage(context, it, (it as FluidPotItem).fluid, FluidConstants.INGOT)
                }
            }
        }

        if (BobsMobGearCompat.IS_DATAGEN) BobsMobGearDatagenItems.register()
    }
}


