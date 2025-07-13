package io.github.thebroccolibob.bobsmobgear.mixin.bettercombat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.thebroccolibob.bobsmobgear.item.HasSpecialAttack;
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearComponents;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WeaponRegistry.class)
public abstract class WeaponRegistryMixin {
    @Shadow
    static WeaponAttributes getAttributes(Identifier itemId) {
        return null;
    }

    @WrapOperation(
            method = "lambda$loadAttributes$0",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/DefaultedRegistry;containsId(Lnet/minecraft/util/Identifier;)Z")
    )
    private static boolean addSpecialAttack(DefaultedRegistry<Item> instance, Identifier identifier, Operation<Boolean> original) {
        if (original.call(instance, identifier)) return true;
        if (!identifier.getPath().endsWith("_special")) return false;
        var baseId = identifier.withPath(path -> path.substring(0, path.length()-"_special".length()));
        return original.call(instance, baseId) && Registries.ITEM.get(baseId) instanceof HasSpecialAttack;
    }

    @Inject(
            method = "getAttributes(Lnet/minecraft/item/ItemStack;)Lnet/bettercombat/api/WeaponAttributes;",
            at = @At(value = "FIELD", target = "Lnet/minecraft/registry/Registries;ITEM:Lnet/minecraft/registry/DefaultedRegistry;"),
            cancellable = true
    )
    private static void checkSpecialAttack(ItemStack itemStack, CallbackInfoReturnable<WeaponAttributes> cir) {
        if (itemStack.contains(BobsMobGearComponents.USING_SPECIAL_ATTACK) && itemStack.getItem() instanceof HasSpecialAttack hasSpecialAttack)
            cir.setReturnValue(getAttributes(hasSpecialAttack.getSpecialAttack()));
    }
}
