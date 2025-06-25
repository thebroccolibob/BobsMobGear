package io.github.thebroccolibob.bobsmobgear.registry

import com.mojang.serialization.Codec
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat
import io.github.thebroccolibob.bobsmobgear.item.*
import io.github.thebroccolibob.bobsmobgear.util.ComparableItemStack
import io.github.thebroccolibob.bobsmobgear.util.itemSettings
import io.github.thebroccolibob.bobsmobgear.util.plus
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Block
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ToolComponent
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.Fluids
import net.minecraft.item.*
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import kotlin.Unit
import net.minecraft.util.Unit as MCUnit

object BobsMobGearItems {
    private fun register(id: Identifier, item: Item): Item =
        Registry.register(Registries.ITEM, id, item)

    private fun register(path: String, item: Item) =
        register(BobsMobGear.id(path), item)

    private fun register(block: Block): Item = Items.register(block)

    private fun <T> register(path: String, init: ComponentType.Builder<T>.() -> Unit): ComponentType<T> =
        Registry.register(Registries.DATA_COMPONENT_TYPE, BobsMobGear.id(path), ComponentType.builder<T>().apply(init).build())

    private fun registerUnit(path: String): ComponentType<MCUnit> = register(path) {
        codec(MCUnit.CODEC)
        packetCodec(PacketCodec.unit(MCUnit.INSTANCE))
    }

    private fun registerBucket(fluid: Fluid) =
        register(Registries.FLUID.getId(fluid) + "_bucket", BucketItem(fluid, itemSettings {
            recipeRemainder(Items.BUCKET)
            maxCount(1)
        }))

    private fun registerPot(fluid: Fluid) =
        register(Registries.FLUID.getId(fluid) + "_pot", FluidPotItem(fluid, itemSettings {
            recipeRemainder(EMPTY_POT)
            maxCount(1)
        }))

    // COMPONENTS

    @JvmField
    val HEATED = registerUnit("heated")

    val TONGS_HELD_ITEM = register<ComparableItemStack>("tongs_held_item") {
        codec(ComparableItemStack.CODEC)
        packetCodec(ComparableItemStack.PACKET_CODEC)
    }

    val MAX_SONIC_CHARGE = register<Int>("max_sonic_charge") {
        codec(Codec.INT)
        packetCodec(PacketCodecs.INTEGER)
    }

    val SONIC_CHARGE = register<Int>("sonic_charge") {
        codec(Codec.INT)
        packetCodec(PacketCodecs.INTEGER)
    }

    @JvmField
    val USING_SPECIAL_ATTACK = registerUnit("using_special_attack")

    // ITEMS

    val EMPTY_TEMPLATE = register(BobsMobGearBlocks.EMPTY_TEMPLATE)
    val SWORD_TEMPLATE = register(BobsMobGearBlocks.SWORD_TEMPLATE)
    val PICKAXE_TEMPLATE = register(BobsMobGearBlocks.PICKAXE_TEMPLATE)
    val AXE_TEMPLATE = register(BobsMobGearBlocks.AXE_TEMPLATE)
    val SHOVEL_TEMPLATE = register(BobsMobGearBlocks.SHOVEL_TEMPLATE)
    val HOE_TEMPLATE = register(BobsMobGearBlocks.HOE_TEMPLATE)

    val GREATHAMMER_TEMPLATE = register(BobsMobGearBlocks.GREATHAMMER_TEMPLATE)
    val MACE_TEMPLATE = register(BobsMobGearBlocks.MACE_TEMPLATE)
    val CLAYMORE_TEMPLATE = register(BobsMobGearBlocks.CLAYMORE_TEMPLATE)
    val KITE_SHIELD_TEMPLATE = register(BobsMobGearBlocks.KITE_SHIELD_TEMPLATE)
    val DAGGER_TEMPLATE = register(BobsMobGearBlocks.DAGGER_TEMPLATE)
    val GLAIVE_TEMPLATE = register(BobsMobGearBlocks.GLAIVE_TEMPLATE)
    val SICKLE_TEMPLATE = register(BobsMobGearBlocks.SICKLE_TEMPLATE)
    val DOUBLE_AXE_TEMPLATE = register(BobsMobGearBlocks.DOUBLE_AXE_TEMPLATE)
    val SPEAR_TEMPLATE = register(BobsMobGearBlocks.SPEAR_TEMPLATE)

    val FORGE = register(BobsMobGearBlocks.FORGE)
    val FORGE_HEATER = register(BobsMobGearBlocks.FORGE_HEATER)

    val EMPTY_POT = register("empty_pot", FluidPotItem(Fluids.EMPTY, itemSettings {
        maxCount(16)
    }))

    val IRON_POT = registerPot(BobsMobGearFluids.IRON)
    val DIAMOND_POT = registerPot(BobsMobGearFluids.DIAMOND)
    val NETHERITE_POT = registerPot(BobsMobGearFluids.NETHERITE)

    val FILLED_POTS = listOf(IRON_POT, DIAMOND_POT, NETHERITE_POT)

    val SMITHING_HAMMER = register("smithing_hammer", SmithingHammerItem(itemSettings {
        maxDamage(128) // TODO decide max damage
        attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.IRON, 1, -3.2f))
        component(DataComponentTypes.TOOL, ToolComponent(listOf(), 1f, 2))
    }))

    @JvmField
    val SMITHING_TONGS = register("smithing_tongs", TongsItem(itemSettings {
        maxCount(1)
        component(TONGS_HELD_ITEM, ComparableItemStack.EMPTY)
    }))

    val WORN_HARDENED_FLESH = register("worn_hardened_flesh", Item(itemSettings {}))
    val WORN_STURDY_BONE = register("worn_sturdy_bone", Item(itemSettings {}))
    val WORN_SPIDER_FANG = register("worn_spider_fang", Item(itemSettings {}))
    val WORN_CREEPER_CORE = register("worn_creeper_core", Item(itemSettings {}))

    val FLESH_GLOVE = register("flesh_glove",
        AbstractFleshGlove(
            FLESH_GLOVE_MATERIAL,
            itemSettings {
                maxCount(1)
                rarity(Rarity.COMMON)
            }
        )
    )

    val IRON_FLESH_GLOVE = register("iron_flesh_glove",
        FleshGloveItem(
            ToolMaterials.IRON,
            itemSettings {
                maxCount(1)
                rarity(Rarity.COMMON)
            },
            0.3f
        )
    )

    val WARDEN_FIST = register("warden_fist", WardenFistItem(itemSettings {
        maxCount(1)
        rarity(Rarity.RARE)
        fireproof()
        attributeModifiers(WardenFistItem.createAttributeModifiers())
        component(MAX_SONIC_CHARGE, 16)
        component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false)
    }))

    val BONE_HAMMER = register("bone_hammer", BoneHammerItem(ToolMaterials.STONE, itemSettings {

    }))

    val SPIDER_DAGGER = register("spider_dagger", SpiderDaggerItem(ToolMaterials.STONE, itemSettings {

    }))

    // ITEM GROUPS

    private fun ItemGroup.Entries.addAll(vararg items: Item) {
        addAll(items.map { it.defaultStack })
    }

    val ITEM_GROUP = Registry.register(Registries.ITEM_GROUP, BobsMobGear.id("item_group"), FabricItemGroup.builder().apply {
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
            entries.addAll(
                FORGE,
                FORGE_HEATER,
                EMPTY_POT,
                IRON_POT,
                DIAMOND_POT,
                NETHERITE_POT,
                SMITHING_HAMMER,
                SMITHING_TONGS,
                FLESH_GLOVE,
                IRON_FLESH_GLOVE,
                SPIDER_DAGGER,
                BONE_HAMMER,
            )
            entries.addAll(listOf(
                WARDEN_FIST.defaultStack.also {
                    it[SONIC_CHARGE] = 16
                },
            ))
        }
    }.build())

    fun register() {
        FluidStorage.ITEM.registerForItems({ stack, context ->
            FullItemFluidStorage(context, EMPTY_POT, FluidVariant.of((stack.item as FluidPotItem).fluid), FluidConstants.INGOT)
        }, IRON_POT, DIAMOND_POT, NETHERITE_POT)

        FluidStorage.combinedItemApiProvider(EMPTY_POT).run {
            FILLED_POTS.forEach {
                register { context ->
                    EmptyItemFluidStorage(context, it, (it as FluidPotItem).fluid, FluidConstants.INGOT)
                }
            }
        }
    }
}


