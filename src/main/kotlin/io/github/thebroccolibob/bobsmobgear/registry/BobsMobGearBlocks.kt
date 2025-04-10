package io.github.thebroccolibob.bobsmobgear.registry

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import net.minecraft.block.Block
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

object BobsMobGearBlocks {
    val SMITHING_SURFACE: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, BobsMobGear.id("smithing_surface"))

    fun register() {}
}