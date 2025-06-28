package io.github.thebroccolibob.bobsmobgear

import net.fabricmc.loader.api.FabricLoader

object BobsMobGearCompat {
    private fun isModLoaded(id: String) = FabricLoader.getInstance().isModLoaded(id)

    val RPGSKILLS_INSTALLED = isModLoaded("rpgskills")

    const val PALADINS = "paladins"
    val PALADINS_INSTALLED = isModLoaded(PALADINS)

    const val ROGUES = "rogues"
    val ROGUES_INSTALLED = isModLoaded(ROGUES)

    const val ARCHERS = "archers"
    val ARCHERS_INSTALLED = isModLoaded(ARCHERS)
}