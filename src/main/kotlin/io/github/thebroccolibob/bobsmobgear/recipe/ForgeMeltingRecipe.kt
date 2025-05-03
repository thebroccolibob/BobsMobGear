package io.github.thebroccolibob.bobsmobgear.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.util.defaultedList
import io.github.thebroccolibob.bobsmobgear.util.singleOrList
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class ForgeMeltingRecipe(
    @get:JvmName("ingredients")
    val ingredients: DefaultedList<Ingredient>,
    val result: FluidVariant,
    val resultAmount: Long,
    val forgingTime: Int,
) : Recipe<ForgingRecipeInput> {

    override fun matches(input: ForgingRecipeInput, world: World): Boolean {
        val stacks = input.stacks.map { it.copy() }
        for (ingredient in ingredients)
            stacks.firstOrNull { ingredient.test(it) }?.decrement(1) ?: return false
        return true
    }

    override fun craft(input: ForgingRecipeInput?, lookup: RegistryWrapper.WrapperLookup?): ItemStack = ItemStack.EMPTY

    override fun fits(width: Int, height: Int): Boolean = width * height <= ingredients.size

    override fun getResult(registriesLookup: RegistryWrapper.WrapperLookup?): ItemStack = ItemStack.EMPTY

    override fun getSerializer(): RecipeSerializer<ForgeMeltingRecipe> = ForgeMeltingRecipe

    override fun getType(): RecipeType<ForgeMeltingRecipe> = ForgeMeltingRecipe

    companion object SerializerAndType : RecipeSerializer<ForgeMeltingRecipe>, RecipeType<ForgeMeltingRecipe> {
        val CODEC = RecordCodecBuilder.mapCodec { it.group(
            Ingredient.DISALLOW_EMPTY_CODEC.singleOrList().defaultedList(Ingredient.EMPTY).fieldOf("ingredients").forGetter(ForgeMeltingRecipe::ingredients),
            FluidVariant.CODEC.fieldOf("result").forGetter(ForgeMeltingRecipe::result),
            Codec.LONG.fieldOf("result_amount").forGetter(ForgeMeltingRecipe::resultAmount),
            Codec.INT.fieldOf("forging_time").forGetter(ForgeMeltingRecipe::forgingTime),
        ).apply(it, ::ForgeMeltingRecipe) }

        val PACKET_CODEC = PacketCodec.tuple(
            Ingredient.PACKET_CODEC.defaultedList(Ingredient.EMPTY), ForgeMeltingRecipe::ingredients,
            FluidVariant.PACKET_CODEC, ForgeMeltingRecipe::result,
            PacketCodecs.VAR_LONG, ForgeMeltingRecipe::resultAmount,
            PacketCodecs.INTEGER, ForgeMeltingRecipe::forgingTime,
            ::ForgeMeltingRecipe
        )

        @Deprecated("Prefer using field", ReplaceWith("ForgeMeltingRecipe.CODEC", "io.github.thebroccolibob.bobsmobgear.recipe.ForgeMeltingRecipe"))
        override fun codec(): MapCodec<ForgeMeltingRecipe> = CODEC

        @Deprecated("Prefer using field", ReplaceWith("ForgeMeltingRecipe.PACKET_CODEC", "io.github.thebroccolibob.bobsmobgear.recipe.ForgeMeltingRecipe"))
        override fun packetCodec(): PacketCodec<RegistryByteBuf, ForgeMeltingRecipe> = PACKET_CODEC

        override fun toString(): String = "${BobsMobGear.MOD_ID}:forge_melting"
    }
}