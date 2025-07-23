package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat.ARCHERS
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat.PALADINS
import io.github.thebroccolibob.bobsmobgear.BobsMobGearCompat.ROGUES
import io.github.thebroccolibob.bobsmobgear.recipe.ForgingRecipe
import io.github.thebroccolibob.bobsmobgear.recipe.TemplateRecipe
import io.github.thebroccolibob.bobsmobgear.registry.*
import io.github.thebroccolibob.bobsmobgear.util.set
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.advancement.AdvancementRequirements
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion
import net.minecraft.block.Block
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder
import net.minecraft.data.server.recipe.RecipeExporter
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.fluid.Fluids
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.ItemTags
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import java.util.concurrent.CompletableFuture
import net.archers.item.Weapons as ArchersWeapons
import net.paladins.item.Shields as PaladinsShields
import net.paladins.item.Weapons as PaladinsWeapons
import net.rogues.item.Weapons as RoguesWeapons
import net.spell_engine.api.item.weapon.Weapon as SpellWeapon
import vectorwing.farmersdelight.common.registry.ModItems as FarmersDelightItems

class RecipeGenerator(output: FabricDataOutput, private val registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>) :
    FabricRecipeProvider(output, registriesFuture) {

    override fun generate(exporter: RecipeExporter) {
        exporter.shapedRecipe(RecipeCategory.TOOLS, BobsMobGearItems.SMITHING_HAMMER) {
            pattern("#")
            pattern("%")
            input('#', ConventionalItemTags.IRON_INGOTS)
            input('%', Items.STICK)

            itemCriterion(Items.IRON_INGOT)
        }

        // TODO Tongs recipe

        exporter.shapedRecipe(RecipeCategory.TOOLS, BobsMobGearItems.EMPTY_TEMPLATE) {
            pattern("###")
            pattern("# #")
            pattern("###")
            input('#', Items.STICK)

            itemCriterion(Items.STICK)
        }

        // the recipe generator only runs once registriesFuture is resolved
        val smithingSurface = registriesFuture.get().getWrapperOrThrow(RegistryKeys.BLOCK).getOrThrow(BobsMobGearBlocks.SMITHING_SURFACE)

        for ((material, base, stone, iron, diamond, netherite, template, modId, extraIngredient) in TOOL_TYPES) {
            val conditionedExporter = modId?.let { withConditions(exporter, ResourceConditions.allModsLoaded(modId)) } ?: exporter

            conditionedExporter.shapelessRecipe(RecipeCategory.TOOLS, template.asItem()) {
                input(BobsMobGearItems.EMPTY_TEMPLATE)
                input(base)

                itemCriterion(BobsMobGearItems.EMPTY_TEMPLATE)
            }

            if (stone != null)
                acceptTemplateRecipe(
                    TemplateRecipe(
                        template,
                        null,
                        Ingredient.ofItems(base),
                        ingredientList(*(
                            List(material) { Ingredient.fromTag(ItemTags.STONE_TOOL_MATERIALS) }
                                + Ingredient.ofItems(Items.STRING)
                        ).toTypedArray()),
                        FluidVariant.blank(),
                        0,
                        false,
                        ItemStack(stone)
                    ),
                    conditionedExporter
                )

            acceptTemplateRecipe(
                TemplateRecipe(
                    template,
                    smithingSurface,
                    Ingredient.ofItems(stone ?: base),
                    extraIngredient?.let { ingredientList(Ingredient.ofItems(it)) } ?: DefaultedList.of(),
                    FluidVariant.of(BobsMobGearFluids.IRON),
                    material * FluidConstants.INGOT,
                    true,
                    ItemStack(iron, 1).apply {
                        set(BobsMobGearComponents.HEATED)
                    }
                ),
                conditionedExporter
            )

            acceptTemplateRecipe(
                TemplateRecipe(
                    template,
                    smithingSurface,
                    Ingredient.ofItems(iron),
                    DefaultedList.of(),
                    FluidVariant.of(BobsMobGearFluids.DIAMOND),
                    material * FluidConstants.INGOT,
                    true,
                    ItemStack(diamond).apply {
                        set(BobsMobGearComponents.HEATED)
                    }
                ),
                conditionedExporter
            )

            acceptTemplateRecipe(
                TemplateRecipe(
                    template,
                    smithingSurface,
                    Ingredient.ofItems(diamond),
                    DefaultedList.of(),
                    FluidVariant.of(BobsMobGearFluids.NETHERITE),
                    1 * FluidConstants.INGOT,
                    true,
                    ItemStack(netherite).apply {
                        set(BobsMobGearComponents.HEATED)
                    }
                ),
                conditionedExporter
            )
        }

        acceptForgingRecipe(
            ForgingRecipe(
                ingredientList(Ingredient.fromTag(BobsMobGearItemTags.FORGES_IRON_INGOT)),
                FluidVariant.of(BobsMobGearFluids.IRON),
                FluidConstants.INGOT,
                200,
            ),
            exporter
        )

        acceptForgingRecipe(
            ForgingRecipe(
                ingredientList(Ingredient.fromTag(BobsMobGearItemTags.FORGES_DIAMOND)),
                FluidVariant.of(BobsMobGearFluids.DIAMOND),
                FluidConstants.INGOT,
                200,
            ),
            exporter
        )

        acceptForgingRecipe(
            BobsMobGear.id("forging/netherite_alloying"),
            ForgingRecipe(
                ingredientList(
                    Ingredient.fromTag(BobsMobGearItemTags.FORGES_GOLD_INGOT),
                    Ingredient.fromTag(BobsMobGearItemTags.FORGES_NETHERITE_SCRAP)
                ),
                FluidVariant.of(BobsMobGearFluids.NETHERITE),
                FluidConstants.INGOT / 4,
                200
            ),
            exporter
        )

        acceptForgingRecipe(
            ForgingRecipe(
                ingredientList(Ingredient.fromTag(BobsMobGearItemTags.FORGES_NETHERITE_INGOT)),
                FluidVariant.of(BobsMobGearFluids.NETHERITE),
                FluidConstants.INGOT,
                200,
            ),
            exporter
        )

        acceptTemplateRecipe(
            BobsMobGear.id("template/hot_potato"),
            TemplateRecipe(
                BobsMobGearBlocks.EMPTY_TEMPLATE,
                smithingSurface,
                Ingredient.ofItems(Items.POTATO),
                DefaultedList.of(),
                FluidVariant.of(Fluids.LAVA),
                FluidConstants.BUCKET,
                true,
                Items.POTATO.defaultStack.apply {
                    set(BobsMobGearComponents.HEATED)
                }
            ),
            exporter,
        )

        acceptTemplateRecipe(
            TemplateRecipe(
                BobsMobGearBlocks.EMPTY_TEMPLATE,
                smithingSurface,
                Ingredient.ofItems(FarmersDelightItems.BACON.get()),
                DefaultedList.of(),
                FluidVariant.of(BobsMobGearFluids.NETHERITE),
                FluidConstants.INGOT,
                true,
                BobsMobGearItems.UNLIMITED_BACON.defaultStack
            ),
            withConditions(exporter, ResourceConditions.allModsLoaded(BobsMobGearCompat.FARMERS_DELIGHT))
        )

        for ((fluid, ingot) in listOf(
            BobsMobGearFluids.IRON to Items.IRON_INGOT,
            BobsMobGearFluids.DIAMOND to Items.DIAMOND,
            BobsMobGearFluids.NETHERITE to Items.NETHERITE_INGOT,
        ))
            acceptTemplateRecipe(
                TemplateRecipe(
                    BobsMobGearBlocks.EMPTY_TEMPLATE,
                    smithingSurface,
                    Ingredient.EMPTY,
                    ingredientList(),
                    FluidVariant.of(fluid),
                    FluidConstants.INGOT,
                    true,
                    ItemStack(ingot).apply {
                        set(BobsMobGearComponents.HEATED)
                    }
                ),
                exporter
            )
    }

    data class ToolType(
        val material: Int,
        val base: Item,
        val stone: Item?,
        val iron: Item,
        val diamond: Item,
        val netherite: Item,
        val template: Block,
        val modId: String? = null,
        /**
         * Only for iron
         */
        val extraIngredient: ItemConvertible? = null,
    )

    companion object {
        private val TOOL_TYPES = listOf(
            ToolType(
                2,
                Items.WOODEN_SWORD,
                Items.STONE_SWORD,
                Items.IRON_SWORD,
                Items.DIAMOND_SWORD,
                Items.NETHERITE_SWORD,
                BobsMobGearBlocks.SWORD_TEMPLATE
            ),
            ToolType(
                3,
                Items.WOODEN_PICKAXE,
                Items.STONE_PICKAXE,
                Items.IRON_PICKAXE,
                Items.DIAMOND_PICKAXE,
                Items.NETHERITE_PICKAXE,
                BobsMobGearBlocks.PICKAXE_TEMPLATE
            ),
            ToolType(
                3,
                Items.WOODEN_AXE,
                Items.STONE_AXE,
                Items.IRON_AXE,
                Items.DIAMOND_AXE,
                Items.NETHERITE_AXE,
                BobsMobGearBlocks.AXE_TEMPLATE
            ),
            ToolType(
                1,
                Items.WOODEN_SHOVEL,
                Items.STONE_SHOVEL,
                Items.IRON_SHOVEL,
                Items.DIAMOND_SHOVEL,
                Items.NETHERITE_SHOVEL,
                BobsMobGearBlocks.SHOVEL_TEMPLATE
            ),
            ToolType(
                2,
                Items.WOODEN_HOE,
                Items.STONE_HOE,
                Items.IRON_HOE,
                Items.DIAMOND_HOE,
                Items.NETHERITE_HOE,
                BobsMobGearBlocks.HOE_TEMPLATE
            ),

            ToolType(
                4,
                PaladinsWeapons.wooden_great_hammer.item,
                PaladinsWeapons.stone_great_hammer.item,
                PaladinsWeapons.iron_great_hammer.item,
                PaladinsWeapons.diamond_great_hammer.item,
                PaladinsWeapons.netherite_great_hammer.item,
                BobsMobGearBlocks.GREATHAMMER_TEMPLATE,
                PALADINS,
           ),
            ToolType(
                2,
                Items.WOODEN_SHOVEL, // TODO
                null,
                PaladinsWeapons.iron_mace.item,
                PaladinsWeapons.diamond_mace.item,
                PaladinsWeapons.netherite_mace.item,
                BobsMobGearBlocks.MACE_TEMPLATE,
                PALADINS,
            ),
            ToolType(
                4,
                Items.WOODEN_SWORD, // TODO
                PaladinsWeapons.stone_claymore.item,
                PaladinsWeapons.iron_claymore.item,
                PaladinsWeapons.diamond_claymore.item,
                PaladinsWeapons.netherite_claymore.item,
                BobsMobGearBlocks.CLAYMORE_TEMPLATE,
                PALADINS,
            ),
            ToolType(
                6,
                Items.SHIELD,
                null,
                Registries.ITEM.get(PaladinsShields.iron_kite_shield.id),
                Registries.ITEM.get(PaladinsShields.diamond_kite_shield.id),
                Registries.ITEM.get(PaladinsShields.netherite_kite_shield.id),
                BobsMobGearBlocks.KITE_SHIELD_TEMPLATE,
                PALADINS,
                Items.LEATHER
            ),
            ToolType(
                1,
                RoguesWeapons.flint_dagger.item,
                null,
                RoguesWeapons.iron_dagger.item,
                RoguesWeapons.diamond_dagger.item,
                RoguesWeapons.netherite_dagger.item,
                BobsMobGearBlocks.DAGGER_TEMPLATE,
                ROGUES,
            ),
            ToolType(
                3,
                Items.WOODEN_AXE, // TODO
                null,
                RoguesWeapons.iron_glaive.item,
                RoguesWeapons.diamond_glaive.item,
                RoguesWeapons.netherite_glaive.item,
                BobsMobGearBlocks.GLAIVE_TEMPLATE,
                ROGUES,
            ),
            ToolType(
                2,
                Items.WOODEN_HOE, // TODO
                null,
                RoguesWeapons.iron_sickle.item,
                RoguesWeapons.diamond_sickle.item,
                RoguesWeapons.netherite_sickle.item,
                BobsMobGearBlocks.SICKLE_TEMPLATE,
                ROGUES,
            ),
            ToolType(
                4,
                Items.WOODEN_AXE, // TODO
                RoguesWeapons.stone_double_axe.item,
                RoguesWeapons.iron_double_axe.item,
                RoguesWeapons.diamond_double_axe.item,
                RoguesWeapons.netherite_double_axe.item,
                BobsMobGearBlocks.DOUBLE_AXE_TEMPLATE,
                ROGUES,
            ),
            ToolType(
                1,
                ArchersWeapons.flint_spear.item,
                null,
                ArchersWeapons.iron_spear.item,
                ArchersWeapons.diamond_spear.item,
                ArchersWeapons.netherite_spear.item,
                BobsMobGearBlocks.SPEAR_TEMPLATE,
                ARCHERS,
            ),
            ToolType(
                1,
                FarmersDelightItems.FLINT_KNIFE.get(),
                null,
                FarmersDelightItems.IRON_KNIFE.get(),
                FarmersDelightItems.DIAMOND_KNIFE.get(),
                FarmersDelightItems.NETHERITE_KNIFE.get(),
                BobsMobGearBlocks.KNIFE_TEMPLATE,
                BobsMobGearCompat.FARMERS_DELIGHT,
            )
        )

        private fun acceptTemplateRecipe(recipeId: Identifier, recipe: TemplateRecipe, exporter: RecipeExporter) {
            exporter.accept(
                recipeId,
                recipe,
                exporter.advancementBuilder.apply {
                    criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                    criterion(hasItem(recipe.template.asItem()), conditionsFromItem(recipe.template.asItem()))
                    rewards(AdvancementRewards.Builder.recipe(recipeId))
                    criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
                }.build(recipeId.withPrefixedPath("recipes/"))
            )
        }

        private fun acceptTemplateRecipe(name: String, recipe: TemplateRecipe, exporter: RecipeExporter) {
            acceptTemplateRecipe(BobsMobGear.id(name).withPrefixedPath("template/"), recipe, exporter)
        }

        private fun acceptTemplateRecipe(recipe: TemplateRecipe, exporter: RecipeExporter) {
            acceptTemplateRecipe(Registries.ITEM.getId(recipe.result.item).run {
                if (namespace == BobsMobGear.MOD_ID || namespace == Identifier.DEFAULT_NAMESPACE) path else "$namespace/$path"
           }, recipe, exporter)
        }

        private fun acceptForgingRecipe(recipeId: Identifier, recipe: ForgingRecipe, exporter: RecipeExporter) {
            exporter.accept(recipeId, recipe, null)
        }

        private fun acceptForgingRecipe(recipe: ForgingRecipe, exporter: RecipeExporter) {
            acceptForgingRecipe(Registries.FLUID.getId(recipe.result.fluid).withPrefixedPath("forging/"), recipe, exporter)
        }

        private fun ingredientList(vararg ingredients: Ingredient): DefaultedList<Ingredient> =
            DefaultedList.copyOf(Ingredient.EMPTY, *ingredients)

        private fun RecipeExporter.shapedRecipe(category: RecipeCategory, output: ItemConvertible, count: Int = 1, name: String? = null, init: ShapedRecipeJsonBuilder.() -> Unit) {
            ShapedRecipeJsonBuilder(category, output, count).apply(init).run {
                if (name == null)
                    offerTo(this@shapedRecipe)
                else
                    offerTo(this@shapedRecipe, name)
            }
        }

        private fun RecipeExporter.shapelessRecipe(category: RecipeCategory, output: ItemConvertible, count: Int = 1, name: String? = null, init: ShapelessRecipeJsonBuilder.() -> Unit) {
            ShapelessRecipeJsonBuilder(category, output, count).apply(init).run {
                if (name == null)
                    offerTo(this@shapelessRecipe)
                else
                    offerTo(this@shapelessRecipe, name)
            }
        }

        private fun CraftingRecipeJsonBuilder.itemCriterion(item: ItemConvertible) {
            criterion(hasItem(item), conditionsFromItem(item))
        }
        
        private val SpellWeapon.Entry.item get() = item()!!
    }
}
