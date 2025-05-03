package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.item.FLESH_GLOVE_MATERIAL
import io.github.thebroccolibob.bobsmobgear.item.FleshGloveItem
import io.github.thebroccolibob.bobsmobgear.item.FluidPotItem
import io.github.thebroccolibob.bobsmobgear.util.itemSettings
import io.github.thebroccolibob.bobsmobgear.util.plus
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage
import net.minecraft.component.ComponentType
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.Fluids
import net.minecraft.item.BucketItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.network.codec.PacketCodec
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import kotlin.Unit
import net.minecraft.util.Unit as MCUnit

object BobsMobGearItems {
    private fun register(id: Identifier, item: Item): Item =
        Registry.register(Registries.ITEM, id, item)

    private fun register(path: String, item: Item) =
        register(BobsMobGear.id(path), item)

    private fun <T> register(path: String, init: ComponentType.Builder<T>.() -> Unit): ComponentType<T> =
        Registry.register(Registries.DATA_COMPONENT_TYPE, BobsMobGear.id(path), ComponentType.builder<T>().apply(init).build())

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

    private fun tagOf(id: Identifier): TagKey<Item> = TagKey.of(RegistryKeys.ITEM, id)
    private fun tagOf(path: String) = tagOf(BobsMobGear.id(path))

    // ITEMS

    val SWORD_TEMPLATE = Items.register(BobsMobGearBlocks.SWORD_TEMPLATE)

    val FLESH_GLOVE = register("flesh_glove",
        FleshGloveItem(
            FLESH_GLOVE_MATERIAL,
            2,
            Item.Settings()
                .maxCount(1)
                .rarity(Rarity.COMMON)
        )
    )

    val EMPTY_POT = register("empty_pot", FluidPotItem(Fluids.EMPTY, itemSettings {
        maxCount(16)
    }))

    val IRON_POT = registerPot(BobsMobGearFluids.IRON)
    val DIAMOND_POT = registerPot(BobsMobGearFluids.DIAMOND)
    val NETHERITE_POT = registerPot(BobsMobGearFluids.NETHERITE)

    val POTS = listOf(IRON_POT, DIAMOND_POT, NETHERITE_POT)

    // COMPONENTS

    @JvmField
    val HEATED = register<MCUnit>("heated") {
        codec(MCUnit.CODEC)
        packetCodec(PacketCodec.unit(MCUnit.INSTANCE))
    }

    // TAGS

    val SMITHING_HAMMER_TAG = tagOf("smithing_hammer")

    fun register() {
        FluidStorage.ITEM.registerForItems({ stack, context ->
            FullItemFluidStorage(context, EMPTY_POT, FluidVariant.of((stack.item as FluidPotItem).fluid), FluidConstants.INGOT)
        }, IRON_POT, DIAMOND_POT, NETHERITE_POT)

        FluidStorage.combinedItemApiProvider(EMPTY_POT).run {
            POTS.forEach {
                register { context ->
                    EmptyItemFluidStorage(context, it, (it as FluidPotItem).fluid, FluidConstants.INGOT)
                }
            }
        }
    }
}


