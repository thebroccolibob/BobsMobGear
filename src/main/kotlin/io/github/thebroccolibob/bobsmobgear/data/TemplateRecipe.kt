package io.github.thebroccolibob.bobsmobgear.data

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.thebroccolibob.bobsmobgear.BobsMobGear
import io.github.thebroccolibob.bobsmobgear.util.defaultedList
import io.github.thebroccolibob.bobsmobgear.util.packetCodecTuple
import io.github.thebroccolibob.bobsmobgear.util.tagKeyPacketCodec
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World
import java.util.*

class TemplateRecipe(
    val template: Block,
    val blockBelow: Optional<TagKey<Block>>,
    val base: Ingredient,
    @get:JvmName("ingredients")
    val ingredients: DefaultedList<Ingredient>,
    val fluid: FluidVariant,
    val fluidAmount: Long,
    val requiresHammer: Boolean,
    val result: ItemStack,
) : Recipe<TemplateRecipeInput> {

    constructor(
        template: Block,
        blockBelow: TagKey<Block>?,
        base: Ingredient,
        ingredients: DefaultedList<Ingredient>,
        fluid: FluidVariant,
        fluidAmount: Long,
        requiresHammer: Boolean,
        result: ItemStack,
    ) : this(template, Optional.ofNullable(blockBelow), base, ingredients, fluid, fluidAmount, requiresHammer, result)

    override fun matches(input: TemplateRecipeInput, world: World): Boolean {
        return template == input.template
            && blockBelow.map { input.blockBelow.isIn(it) }.orElse(true)
            && base.test(input.base)
            && (input.fluid == null || fluid == input.fluid)
            && (input.fluidAmount == null || input.fluidAmount >= fluidAmount)
            && (input.ingredients == null || ((input.ingredientsPartial || input.ingredients.size == ingredients.size)
                && (ingredients zip input.ingredients).all { (ingredient, stack) -> ingredient.test(stack) }))
    }

    override fun getIngredients(): DefaultedList<Ingredient> = ingredients

    override fun craft(input: TemplateRecipeInput, lookup: WrapperLookup): ItemStack = result

    override fun fits(width: Int, height: Int): Boolean = height >= 1 && width >= ingredients.size + 1

    override fun getResult(registriesLookup: WrapperLookup): ItemStack = result

    override fun getSerializer(): RecipeSerializer<*> = SerializerAndType

    override fun getType(): RecipeType<TemplateRecipe> = SerializerAndType

    override fun createIcon(): ItemStack = template.asItem().defaultStack

    override fun isEmpty(): Boolean = false

    companion object SerializerAndType : RecipeSerializer<TemplateRecipe>, RecipeType<TemplateRecipe> {
        val CODEC: MapCodec<TemplateRecipe> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Registries.BLOCK.codec.fieldOf("template").forGetter(TemplateRecipe::template),
                TagKey.codec(RegistryKeys.BLOCK).optionalFieldOf("block_below").forGetter(TemplateRecipe::blockBelow),
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("base").forGetter(TemplateRecipe::base),
                Ingredient.DISALLOW_EMPTY_CODEC.listOf().xmap({ DefaultedList.copyOf(Ingredient.EMPTY, *it.toTypedArray()) }, { it })
                    .optionalFieldOf("ingredients", DefaultedList.of()).forGetter(TemplateRecipe::ingredients),
                FluidVariant.CODEC.optionalFieldOf("fluid", FluidVariant.blank()).forGetter(TemplateRecipe::fluid),
                Codec.LONG.optionalFieldOf("fluid_amount", 0).forGetter(TemplateRecipe::fluidAmount),
                Codec.BOOL.optionalFieldOf("requires_hammer", false).forGetter(TemplateRecipe::requiresHammer),
                ItemStack.VALIDATED_UNCOUNTED_CODEC.fieldOf("result").forGetter(TemplateRecipe::result)
            ).apply(instance, ::TemplateRecipe)
        }

        val PACKET_CODEC: PacketCodec<RegistryByteBuf, TemplateRecipe> = packetCodecTuple(
            PacketCodecs.registryValue(RegistryKeys.BLOCK), TemplateRecipe::template,
            PacketCodecs.optional(tagKeyPacketCodec(RegistryKeys.BLOCK)), TemplateRecipe::blockBelow,
            Ingredient.PACKET_CODEC, TemplateRecipe::base,
            Ingredient.PACKET_CODEC.defaultedList(Ingredient.EMPTY), TemplateRecipe::ingredients,
            FluidVariant.PACKET_CODEC, TemplateRecipe::fluid,
            PacketCodecs.VAR_LONG, TemplateRecipe::fluidAmount,
            PacketCodecs.BOOL, TemplateRecipe::requiresHammer,
            ItemStack.PACKET_CODEC, TemplateRecipe::result,
            ::TemplateRecipe
        )

        @Deprecated("Prefer using field", ReplaceWith("TemplateRecipe.CODEC", "io.github.thebroccolibob.bobsmobgear.data.TemplateRecipe"))
        override fun codec(): MapCodec<TemplateRecipe> = CODEC

        @Deprecated("Prefer using field", ReplaceWith("TemplateRecipe.PACKET_CODEC", "io.github.thebroccolibob.bobsmobgear.data.TemplateRecipe"))
        override fun packetCodec(): PacketCodec<RegistryByteBuf, TemplateRecipe> = PACKET_CODEC

        override fun toString() = "${BobsMobGear.MOD_ID}:template"
    }
}
