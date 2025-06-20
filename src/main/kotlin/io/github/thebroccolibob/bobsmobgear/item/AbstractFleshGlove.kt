package io.github.thebroccolibob.bobsmobgear.item

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItems
import io.github.thebroccolibob.bobsmobgear.util.ToolMaterial
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.tag.BlockTags

open class AbstractFleshGlove(
    material: ToolMaterial,
    settings: Settings,
) : ToolItem(
    material,
    settings.component(
        DataComponentTypes.ATTRIBUTE_MODIFIERS,
        SwordItem.createAttributeModifiers(material, 2, -2.0f)
    )
) {
    override fun postHit(stack: ItemStack?, target: LivingEntity?, attacker: LivingEntity?): Boolean {
        return true
    }

    override fun postDamageEntity(stack: ItemStack, target: LivingEntity?, attacker: LivingEntity?) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND)
    }
}

val FLESH_GLOVE_MATERIAL = ToolMaterial(
    attackDamage = 1.0f,
    durability = 131,
    enchantability = 5,
    inverseTag = BlockTags.PICKAXE_MINEABLE,
    miningSpeedMultiplier = 0.0f,
    repairIngredient = Ingredient.ofItems(BobsMobGearItems.WORN_HARDENED_FLESH)
)