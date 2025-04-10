package io.github.thebroccolibob.bobsmobgear.data

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.thebroccolibob.bobsmobgear.util.packetCodecTuple
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.predicate.block.BlockStatePredicate
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryCodecs
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World
import net.minecraft.world.gen.blockpredicate.BlockPredicate

class TemplateRecipe(
    val blockBelow: BlockPredicate,
    val templateBlock: Block,
    val base: Ingredient,
    val ingredients: DefaultedList<Ingredient>,
    val fluid: FluidVariant,
    val requiresHammer: Boolean,
    val result: ItemStack,
) : Recipe<TemplateRecipeInput> {

    override fun matches(input: TemplateRecipeInput, world: World): Boolean {
        TODO("Not yet implemented")
    }

    override fun craft(input: TemplateRecipeInput, lookup: WrapperLookup): ItemStack {
        TODO("Not yet implemented")
    }

    override fun fits(width: Int, height: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getResult(registriesLookup: WrapperLookup): ItemStack {
        TODO("Not yet implemented")
    }

    override fun getSerializer(): RecipeSerializer<*> {
        TODO("Not yet implemented")
    }

    override fun getType(): RecipeType<*> {
        TODO("Not yet implemented")
    }

    companion object {
        val CODEC = RecordCodecBuilder.create {
            it.group(
                BlockPredicate.BASE_CODEC.optionalFieldOf("block_below", BlockPredicate.alwaysTrue()).forGetter(TemplateRecipe::blockBelow),
                Registries.BLOCK.codec.fieldOf("template_block").forGetter(TemplateRecipe::templateBlock),
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("base").forGetter(TemplateRecipe::base),
                Ingredient.DISALLOW_EMPTY_CODEC.listOf().xmap({ DefaultedList.copyOf(Ingredient.EMPTY, *it.toTypedArray()) }, { it })
                    .optionalFieldOf("ingredients", DefaultedList.of()).forGetter(TemplateRecipe::ingredients),
                FluidVariant.CODEC.optionalFieldOf("fluid", FluidVariant.blank()).forGetter(TemplateRecipe::fluid),
                Codec.BOOL.optionalFieldOf("requires_hammer", false).forGetter(TemplateRecipe::requiresHammer),
                ItemStack.VALIDATED_UNCOUNTED_CODEC.fieldOf("result").forGetter(TemplateRecipe::result)
            ).apply(it, ::TemplateRecipe)
        }

        val PACKET_CODEC = packetCodecTuple(
            PacketCodecs.codec(BlockPredicate.BASE_CODEC), TemplateRecipe::blockBelow,
            PacketCodecs.registryValue(RegistryKeys.BLOCK), TemplateRecipe::templateBlock,
            Ingredient.PACKET_CODEC, TemplateRecipe::base,
            Ingredient.PACKET_CODEC.collect({ PacketCodecs.collection({ DefaultedList.ofSize(it, Ingredient.EMPTY)}, Ingredient.PACKET_CODEC) }), TemplateRecipe::ingredients,
            FluidVariant.PACKET_CODEC, TemplateRecipe::fluid,
            PacketCodecs.BOOL, TemplateRecipe::requiresHammer,
            ItemStack.PACKET_CODEC, TemplateRecipe::result,
            ::TemplateRecipe
        )

        //val SERIALIZER = Recipe
    }
}