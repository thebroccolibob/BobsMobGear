package io.github.thebroccolibob.bobsmobgear.data

//sealed class FluidIngredient(protected val components: ComponentChanges) : Predicate<FluidVariant> {
//    override fun apply(input: FluidVariant): Boolean {
//        return input.componentsMatch(components)
//    }
//
//    fun matches(fluid: FluidVariant) = apply(fluid)
//
//    class Tag(private val tag: TagKey<Fluid>, components: ComponentChanges) : FluidIngredient(components) {
//        @Suppress("DEPRECATION")
//        override fun apply(input: FluidVariant): Boolean = input.fluid.isIn(tag)
//
//        companion object {
//            val CODEC = AlternateCodec(
//                RecordCodecBuilder.create { it.group(
//                    TagKey.unprefixedCodec(RegistryKeys.FLUID).fieldOf("tag").forGetter(Tag::tag),
//                    ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(Tag::components)
//                ).apply(it, ::Tag) },
//                TagKey.codec(RegistryKeys.FLUID).xmap({ Tag(it, ComponentChanges.EMPTY) }, { it.tag }),
//            ) { it.components.isEmpty }
//        }
//    }
//
//    class Multiple(private val fluids: List<Fluid>, components: ComponentChanges) : FluidIngredient(components) {
//        override fun apply(input: FluidVariant): Boolean = input.fluid in fluids
//
//        companion object {
//            val CODEC = AlternateCodec(
//                RecordCodecBuilder.create { it.group(
//                    RegistryElementCodec.of(RegistryKeys.FLUID, Fluid).fieldOf("flu").forGetter(Multiple::fluids),
//                    ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(Tag::components)
//                ).apply(it, ::Tag) },
//                TagKey.codec(RegistryKeys.FLUID).xmap({ Tag(it, ComponentChanges.EMPTY) }, { it.tag }),
//            ) { it.components.isEmpty }
//        }
//    }
//
//    companion object {
//        val CODEC
//    }
//}