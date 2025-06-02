package io.github.thebroccolibob.bobsmobgear.datagen

import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearEnchantments
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.enchantment.Enchantments
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.EnchantmentTags
import java.util.concurrent.CompletableFuture

class EnchantmentTagGenerator(
    output: FabricDataOutput,
    completableFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider.EnchantmentTagProvider(output, completableFuture) {
    override fun configure(wrapperLookup: RegistryWrapper.WrapperLookup) {
        getOrCreateTagBuilder(BobsMobGearEnchantments.EXCLUSIVE_SET_MENDER).add(
            Enchantments.UNBREAKING,
            Enchantments.MENDING,
        )
        getOrCreateTagBuilder(EnchantmentTags.NON_TREASURE).add(
            BobsMobGearEnchantments.MENDER
        )
    }
}