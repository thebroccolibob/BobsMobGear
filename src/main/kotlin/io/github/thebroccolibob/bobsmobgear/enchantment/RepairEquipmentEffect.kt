package io.github.thebroccolibob.bobsmobgear.enchantment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.thebroccolibob.bobsmobgear.mixin.EnchantmentHelperInvoker
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearEnchantments.REPAIR_ENTITY_EQUIPMENT
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearEnchantments.REPAIR_HAND_EQUIPMENT
import io.github.thebroccolibob.bobsmobgear.util.get
import io.github.thebroccolibob.bobsmobgear.util.opposite
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.component.ComponentType
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.effect.EnchantmentEffectEntry
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.TypedActionResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

data class RepairEquipmentEffect(
    val source: Source,
    val costPerDurability: Int
) {
    enum class Source(val id: String) : StringIdentifiable {
        XP("xp"), DURABIILITY("durability");

        override fun asString(): String = id

        companion object {
            val CODEC: Codec<Source> = StringIdentifiable.createCodec(::enumValues)
        }
    }

    companion object : UseEntityCallback, UseItemCallback {
        val CODEC: Codec<RepairEquipmentEffect> = RecordCodecBuilder.create {
            it.group(
                Source.CODEC.fieldOf("source").forGetter(RepairEquipmentEffect::source),
                Codec.INT.fieldOf("cost_per_durability").forGetter(RepairEquipmentEffect::costPerDurability)
            ).apply(it, ::RepairEquipmentEffect)
        }

        private fun repairEquipment(
            stack: ItemStack,
            player: PlayerEntity,
            hand: Hand,
            holder: Entity,
            type: ComponentType<EnchantmentEffectEntry<RepairEquipmentEffect>>,
            getEquipment: () -> ItemStack?,
        ): ActionResult {
            if (!EnchantmentHelper.hasAnyEnchantmentsWith(stack, type)) return ActionResult.PASS
            if (player.world !is ServerWorld) return ActionResult.SUCCESS

            val equipment = getEquipment() ?: return ActionResult.FAIL

            EnchantmentHelperInvoker.invokeForEachEnchantment(stack, LivingEntity.getSlotForHand(hand), player) { enchantment, _, _ ->
                val effectEntry = enchantment.value().effects.get(type) ?: return@invokeForEachEnchantment

                val available = when (effectEntry.effect.source) {
                    RepairEquipmentEffect.Source.XP -> player.totalExperience
                    RepairEquipmentEffect.Source.DURABIILITY -> stack.maxDamage - stack.damage
                }

                val repairedAmount = (equipment.damage * effectEntry.effect.costPerDurability).coerceAtMost(available) / effectEntry.effect.costPerDurability
                if (repairedAmount == 0) return@invokeForEachEnchantment
                val cost = repairedAmount * effectEntry.effect.costPerDurability

                equipment.damage -= repairedAmount
                when (effectEntry.effect.source) {
                    RepairEquipmentEffect.Source.XP -> player.addExperience(-cost)
                    RepairEquipmentEffect.Source.DURABIILITY -> stack.damage(cost, player, LivingEntity.getSlotForHand(hand))
                }

                holder.world.playSoundFromEntity(null, holder, SoundEvents.BLOCK_SMITHING_TABLE_USE, player.soundCategory, 1f, 1f) // TODO custom sound event
                player.addCritParticles(holder)
            }
            return ActionResult.SUCCESS
        }

        override fun interact(
            player: PlayerEntity,
            world: World,
            hand: Hand,
            entity: Entity,
            hitResult: EntityHitResult?
        ): ActionResult {
            if (entity !is LivingEntity || EquipmentSlot.entries.all { !entity[it].isDamaged }) return ActionResult.PASS
            val stack = player[hand]

            return repairEquipment(stack, player, hand, entity, REPAIR_ENTITY_EQUIPMENT) {
                EquipmentSlot.entries
                    .shuffled()
                    .firstNotNullOfOrNull { slot ->
                        entity[slot].takeUnless { it.isEmpty || !it.isDamaged }
                    }
            }
        }

        override fun interact(player: PlayerEntity, world: World, hand: Hand): TypedActionResult<ItemStack> {
            val stack = player[hand]
            val equipment = player[hand.opposite]
            if (!equipment.isDamaged) return TypedActionResult.pass(stack)

            return TypedActionResult(repairEquipment(stack, player, hand, player, REPAIR_HAND_EQUIPMENT) { equipment }, stack)
        }

        fun register() {
            UseEntityCallback.EVENT.register(this)
            UseItemCallback.EVENT.register(this)
        }
    }
}