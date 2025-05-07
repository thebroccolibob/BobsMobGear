package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.block.ForgeBlock
import io.github.thebroccolibob.bobsmobgear.block.TemplateBlock
import io.github.thebroccolibob.bobsmobgear.block.entity.ForgeBlockEntity
import io.github.thebroccolibob.bobsmobgear.block.entity.ForgeHeaterBlock
import io.github.thebroccolibob.bobsmobgear.block.entity.ForgeHeaterBlockEntity
import io.github.thebroccolibob.bobsmobgear.block.entity.TemplateBlockEntity
import io.github.thebroccolibob.bobsmobgear.util.BlockEntityType
import io.github.thebroccolibob.bobsmobgear.util.blockSettings
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.Blocks.createLightLevelFromLitBlockState
import net.minecraft.block.FluidBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
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

    // BLOCKS

    val SWORD_TEMPLATE = register("sword_template", TemplateBlock(blockSettings {
        sounds(BlockSoundGroup.WOOD)
        strength(0.5f)
        nonOpaque()
    }))

    val FORGE_HEATER = register("forge_heater", ForgeHeaterBlock(blockSettings {
        sounds(BlockSoundGroup.METAL)
        requiresTool()
        strength(3.5F)
        luminance(createLightLevelFromLitBlockState(14))
    }))

    val FORGE = register("forge", ForgeBlock(FORGE_HEATER, AbstractBlock.Settings.copy(FORGE_HEATER)))

    // BLOCK ENTITIES

    val TEMPLATE_BLOCK_ENTITY = register("template", BlockEntityType(::TemplateBlockEntity,
        SWORD_TEMPLATE,
    ))

    val FORGE_BLOCK_ENTITY = register("forge", BlockEntityType(::ForgeBlockEntity,
        FORGE,
    ))

    val FORGE_HEATER_BLOCK_ENTITY = register("forge_heater", BlockEntityType(::ForgeHeaterBlockEntity,
        FORGE_HEATER,
    ))

    // TAGS

    val SMITHING_SURFACE: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, BobsMobGear.id("smithing_surface"))

    fun register() {}
}
