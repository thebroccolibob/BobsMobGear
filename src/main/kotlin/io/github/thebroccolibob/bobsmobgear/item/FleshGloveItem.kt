package io.github.thebroccolibob.bobsmobgear.item

import net.minecraft.item.ToolMaterial

class FleshGloveItem(
    material: ToolMaterial,
    settings: Settings,
    private val blockSuccessChance: Float
) : AbstractFleshGlove(material, settings) {}