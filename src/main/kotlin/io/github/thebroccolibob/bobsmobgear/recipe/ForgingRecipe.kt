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
import net.minecraft.recipe.input.RecipeInput
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class ForgingRecipe(
    @get:JvmName("ingredients")
    val ingredients: DefaultedList<Ingredient>,
    val result: FluidVariant,
    val resultAmount: Long,
    val forgingTime: Int,
) : Recipe<ForgingRecipe.Input> {

    override fun matches(input: Input, world: World): Boolean = subtractItems(input.stacks.map { it.copy() })

    override fun craft(input: Input?, lookup: RegistryWrapper.WrapperLookup?): ItemStack = ItemStack.EMPTY

    override fun fits(width: Int, height: Int): Boolean = width * height <= ingredients.size

    override fun getResult(registriesLookup: RegistryWrapper.WrapperLookup?): ItemStack = ItemStack.EMPTY

    override fun getSerializer(): RecipeSerializer<ForgingRecipe> = ForgingRecipe

    override fun getType(): RecipeType<ForgingRecipe> = ForgingRecipe

    /**
     * @return If all ingredients were satisfied
     */
    fun subtractItems(stacks: List<ItemStack>): Boolean = ingredients.all { ingredient ->
        stacks.firstOrNull { ingredient.test(it) }?.also { it.decrement(1) } != null
    }

    fun <T> selectInventories(containers: Iterable<T>, getInventory: T.() -> List<ItemStack>): Set<T> {
        val inventories = containers.associateWith { it.getInventory().map(ItemStack::copy) } // copy inventories
        val selected = mutableSetOf<T>()
        ingredients.forEach { ingredient ->
            inventories.firstNotNullOfOrNull { (container, inventory) ->
                if (inventory.firstOrNull { ingredient.test(it) }?.also { it.decrement(1) } != null) container else null
            }?.let(selected::add)
        }
        return selected
    }

    companion object SerializerAndType : RecipeSerializer<ForgingRecipe>, RecipeType<ForgingRecipe> {
        val CODEC: MapCodec<ForgingRecipe> = RecordCodecBuilder.mapCodec { it.group(
            Ingredient.DISALLOW_EMPTY_CODEC.singleOrList().defaultedList(Ingredient.EMPTY).fieldOf("ingredients").forGetter(ForgingRecipe::ingredients),
            FluidVariant.CODEC.fieldOf("result").forGetter(ForgingRecipe::result),
            Codec.LONG.fieldOf("result_amount").forGetter(ForgingRecipe::resultAmount),
            Codec.INT.fieldOf("forging_time").forGetter(ForgingRecipe::forgingTime),
        ).apply(it, ::ForgingRecipe) }

        val PACKET_CODEC: PacketCodec<RegistryByteBuf, ForgingRecipe> = PacketCodec.tuple(
            Ingredient.PACKET_CODEC.defaultedList(Ingredient.EMPTY), ForgingRecipe::ingredients,
            FluidVariant.PACKET_CODEC, ForgingRecipe::result,
            PacketCodecs.VAR_LONG, ForgingRecipe::resultAmount,
            PacketCodecs.INTEGER, ForgingRecipe::forgingTime,
            ::ForgingRecipe
        )

        @Deprecated("Prefer using field", ReplaceWith("ForgingRecipe.CODEC", "io.github.thebroccolibob.bobsmobgear.recipe.ForgingRecipe"))
        override fun codec(): MapCodec<ForgingRecipe> = CODEC

        @Deprecated("Prefer using field", ReplaceWith("ForgingRecipe.PACKET_CODEC", "io.github.thebroccolibob.bobsmobgear.recipe.ForgingRecipe"))
        override fun packetCodec(): PacketCodec<RegistryByteBuf, ForgingRecipe> = PACKET_CODEC

        override fun toString(): String = "${BobsMobGear.MOD_ID}:forging"
    }

    data class Input(
        val stacks: DefaultedList<ItemStack>,
    ) : RecipeInput {
        override fun getStackInSlot(slot: Int): ItemStack = stacks[slot]

        override fun getSize(): Int = stacks.size
    }
}
