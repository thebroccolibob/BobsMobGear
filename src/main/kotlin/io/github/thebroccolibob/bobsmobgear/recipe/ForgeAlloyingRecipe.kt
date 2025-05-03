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
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class ForgeAlloyingRecipe(
    @get:JvmName("ingredients")
    val ingredients: DefaultedList<FluidIngredient>,
    val result: FluidVariant,
    val resultAmount: Long,
) : Recipe<ForgingRecipeInput> {

    override fun matches(input: ForgingRecipeInput, world: World): Boolean {
        return ingredients.all { ingredient ->
            input.fluids.any {
                ingredient.test(it.key, it.value)
            }
        }
    }

    override fun craft(input: ForgingRecipeInput?, lookup: RegistryWrapper.WrapperLookup?): ItemStack = ItemStack.EMPTY

    override fun fits(width: Int, height: Int): Boolean = true

    override fun getResult(registriesLookup: RegistryWrapper.WrapperLookup?): ItemStack = ItemStack.EMPTY

    override fun getSerializer(): RecipeSerializer<ForgeAlloyingRecipe> = ForgeAlloyingRecipe

    override fun getType(): RecipeType<ForgeAlloyingRecipe> = ForgeAlloyingRecipe

    companion object SerializerAndType : RecipeSerializer<ForgeAlloyingRecipe>, RecipeType<ForgeAlloyingRecipe> {
        val CODEC = RecordCodecBuilder.mapCodec { it.group(
            FluidIngredient.CODEC.singleOrList().defaultedList(FluidIngredient.EMPTY).fieldOf("ingredients").forGetter(ForgeAlloyingRecipe::ingredients),
            FluidVariant.CODEC.fieldOf("result").forGetter(ForgeAlloyingRecipe::result),
            Codec.LONG.fieldOf("result_amount").forGetter(ForgeAlloyingRecipe::resultAmount),
        ).apply(it, ::ForgeAlloyingRecipe) }

        val PACKET_CODEC = PacketCodec.tuple(
            FluidIngredient.PACKET_CODEC.defaultedList(FluidIngredient.EMPTY), ForgeAlloyingRecipe::ingredients,
            FluidVariant.PACKET_CODEC, ForgeAlloyingRecipe::result,
            PacketCodecs.VAR_LONG, ForgeAlloyingRecipe::resultAmount,
            ::ForgeAlloyingRecipe
        )

        @Deprecated("Prefer using field", ReplaceWith("ForgeAlloyingRecipe.CODEC", "io.github.thebroccolibob.bobsmobgear.recipe.ForgeAlloyingRecipe"))
        override fun codec(): MapCodec<ForgeAlloyingRecipe> = CODEC

        @Deprecated("Prefer using field", ReplaceWith("ForgeAlloyingRecipe.PACKET_CODEC", "io.github.thebroccolibob.bobsmobgear.recipe.ForgeAlloyingRecipe"))
        override fun packetCodec(): PacketCodec<RegistryByteBuf, ForgeAlloyingRecipe> = PACKET_CODEC

        override fun toString(): String = "${BobsMobGear.MOD_ID}:forge_alloying"
    }
}