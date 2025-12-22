package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.block.ForgeBlock
import io.github.thebroccolibob.bobsmobgear.block.ForgeHeaterBlock
import io.github.thebroccolibob.bobsmobgear.block.GunflowerBlock
import io.github.thebroccolibob.bobsmobgear.block.TemplateBlock
import io.github.thebroccolibob.bobsmobgear.block.entity.ForgeBlockEntity
import io.github.thebroccolibob.bobsmobgear.block.entity.ForgeHeaterBlockEntity
import io.github.thebroccolibob.bobsmobgear.block.entity.TemplateBlockEntity
import io.github.thebroccolibob.bobsmobgear.util.BlockEntityType
import io.github.thebroccolibob.bobsmobgear.util.blockSettings
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.Blocks.createLightLevelFromLitBlockState
import net.minecraft.block.FluidBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.fluid.FlowableFluid
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier

@Suppress("MemberVisibilityCanBePrivate")
object BobsMobGearBlocks {
    private fun register(id: Identifier, block: Block): Block =
        Registry.register(Registries.BLOCK, id, block)

    private fun register(path: String, block: Block): Block =
        register(BobsMobGear.id(path), block)

    private fun <T: BlockEntity> register(path: String, blockEntity: BlockEntityType<T>): BlockEntityType<T> =
        Registry.register(Registries.BLOCK_ENTITY_TYPE, BobsMobGear.id(path), blockEntity)

    private fun register(fluid: FlowableFluid, settingsBase: Block): Block =
        register(Registries.FLUID.getId(fluid), FluidBlock(fluid, AbstractBlock.Settings.copy(settingsBase)))

    private fun <T: BlockEntity> registerFluidStorage(type: BlockEntityType<T>, getStorage: T.() -> Storage<FluidVariant>?) {
        FluidStorage.SIDED.registerForBlockEntity({ blockEntity, _ -> blockEntity.getStorage() }, type)
    }

    private val templates = mutableListOf<Block>()

    private fun registerTemplate(type: String): Block =
        register("${type}_template", TemplateBlock(blockSettings {
            sounds(BlockSoundGroup.STONE)
            strength(0.5f)
            nonOpaque()
        })).also { templates.add(it) }

    // BLOCKS

    @JvmField val EMPTY_TEMPLATE = registerTemplate("empty")
    @JvmField val SWORD_TEMPLATE = registerTemplate("sword")
    @JvmField val PICKAXE_TEMPLATE = registerTemplate("pickaxe")
    @JvmField val AXE_TEMPLATE = registerTemplate("axe")
    @JvmField val SHOVEL_TEMPLATE = registerTemplate("shovel")
    @JvmField val HOE_TEMPLATE = registerTemplate("hoe")

    @JvmField val GREATHAMMER_TEMPLATE = registerTemplate("rpg/greathammer")
    @JvmField val MACE_TEMPLATE = registerTemplate("rpg/mace")
    @JvmField val CLAYMORE_TEMPLATE = registerTemplate("rpg/claymore")
    @JvmField val KITE_SHIELD_TEMPLATE = registerTemplate("rpg/kite_shield")
    @JvmField val DAGGER_TEMPLATE = registerTemplate("rpg/dagger")
    @JvmField val GLAIVE_TEMPLATE = registerTemplate("rpg/glaive")
    @JvmField val SICKLE_TEMPLATE = registerTemplate("rpg/sickle")
    @JvmField val DOUBLE_AXE_TEMPLATE = registerTemplate("rpg/double_axe")
    @JvmField val SPEAR_TEMPLATE = registerTemplate("rpg/spear")
    @JvmField val KNIFE_TEMPLATE = registerTemplate("farmersdelight/knife")

    val TEMPLATES by lazy { templates.toTypedArray() }

    @JvmField val FORGE_HEATER = register("forge_heater", ForgeHeaterBlock(blockSettings {
        sounds(BlockSoundGroup.METAL)
        requiresTool()
        strength(3.5F)
        luminance(createLightLevelFromLitBlockState(14))
    }))

    @JvmField val WEAK_HEAT_SOURCES: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, BobsMobGear.id("weak_heat_sources"))

    @JvmField val FORGE = register("forge", ForgeBlock(WEAK_HEAT_SOURCES, FORGE_HEATER, AbstractBlock.Settings.copy(FORGE_HEATER)))

    @JvmField val WORN_GUNFLOWER = register("worn_gunflower", GunflowerBlock(20, 40, 20, 2f, blockSettings {
        noCollision()
        ticksRandomly()
        breakInstantly()
        sounds(BlockSoundGroup.GRASS)
        pistonBehavior(PistonBehavior.DESTROY)
    }))

    // BLOCK ENTITIES

    @JvmField val TEMPLATE_BLOCK_ENTITY = register("template", BlockEntityType(::TemplateBlockEntity, *TEMPLATES))

    @JvmField val FORGE_BLOCK_ENTITY = register("forge", BlockEntityType(::ForgeBlockEntity,
        FORGE,
    ))

    @JvmField val FORGE_HEATER_BLOCK_ENTITY = register("forge_heater", BlockEntityType(::ForgeHeaterBlockEntity,
        FORGE_HEATER,
    ))

    // TAGS

    @JvmField val SMITHING_SURFACE: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, BobsMobGear.id("smithing_surface"))

    fun register() {
        registerFluidStorage(TEMPLATE_BLOCK_ENTITY) { fluidStorage }
        registerFluidStorage(FORGE_BLOCK_ENTITY) { fluidStorage }
    }
}
