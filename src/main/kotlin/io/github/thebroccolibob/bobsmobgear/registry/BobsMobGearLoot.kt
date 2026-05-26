package io.github.thebroccolibob.bobsmobgear.registry

import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.entity.mob.Angriness
import net.minecraft.entity.mob.WardenEntity
import net.minecraft.loot.LootTable
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.util.*

object BobsMobGearLoot {
    private fun of(name: String): RegistryKey<LootTable> = RegistryKey.of(RegistryKeys.LOOT_TABLE, BobsMobGear.id(name))

    val SHEAR_WARDEN = of("shear_warden")

    fun register() {
        UseEntityCallback.EVENT.register { player, world, hand, entity, _ ->
            if (entity !is WardenEntity) return@register ActionResult.PASS
            val stack = player[hand]
            if (!(stack isIn ConventionalItemTags.SHEAR_TOOLS)) return@register ActionResult.PASS
            if (BobsMobGearAttachments.SHEARED in entity) return@register ActionResult.FAIL

            player.playSound(BobsMobGearSounds.WARDEN_SHEARED)

            if (world !is ServerWorld) return@register ActionResult.SUCCESS

            val table = world.server.reloadableRegistries.getLootTable(SHEAR_WARDEN)
            val parameters = LootContextParameterSet.Builder(world).apply {
                add(LootContextParameters.ORIGIN, entity.pos)
                add(LootContextParameters.THIS_ENTITY, entity)
            }.build(LootContextTypes.SHEARING)
            for (stack in table.generateLoot(parameters))
                entity.dropStack(stack, entity.height)

            entity.increaseAngerAt(player, Angriness.ANGRY.threshold + 20, false)
            entity.set(BobsMobGearAttachments.SHEARED)

            stack.damage(1, player, hand)

            ActionResult.SUCCESS
        }
    }
}