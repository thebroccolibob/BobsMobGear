package io.github.thebroccolibob.bobsmobgear

import net.fabricmc.loader.api.FabricLoader

object BobsMobGearCompat {
    private fun isModLoaded(id: String) = FabricLoader.getInstance().isModLoaded(id)

    val RPGSKILLS_INSTALLED = isModLoaded("rpgskills")

    val BETTER_COMBAT_INSTALLED = isModLoaded("bettercombat")

    const val PALADINS = "paladins"
    val PALADINS_INSTALLED = isModLoaded(PALADINS)

    const val ROGUES = "rogues"
    val ROGUES_INSTALLED = isModLoaded(ROGUES)

    const val ARCHERS = "archers"
    val ARCHERS_INSTALLED = isModLoaded(ARCHERS)

    const val FARMERS_DELIGHT = "farmersdelight"
    val FARMERS_DELIGHT_INSTALLED = isModLoaded(FARMERS_DELIGHT)

    const val CATACLYSM = "cataclysm"
    val CATACLYSM_INSTALLED = isModLoaded(CATACLYSM)

    const val CREATE = "create"

    const val CONNECTOR = "connector"
    val CONNECTOR_INSTALLED = isModLoaded(CONNECTOR)
    val FLUID_FACTOR = if (CONNECTOR_INSTALLED) 81L else 1L
}
