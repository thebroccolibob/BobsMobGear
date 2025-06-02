package io.github.thebroccolibob.bobsmobgear.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import io.github.thebroccolibob.bobsmobgear.item.FleshGloveItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract ItemStack getActiveItem();

    @SuppressWarnings("UnreachableCode")
    @ModifyVariable(
            method = "damage",
            at = @At(value = "STORE", ordinal = 1),
            ordinal = 0
    )
    boolean fleshGloveDefend(boolean value, @Local(argsOnly = true)LocalFloatRef amount, @Local(ordinal = 2)LocalFloatRef damageBlocked) {
        if (!(getActiveItem().getItem() instanceof FleshGloveItem)) return value;
        var damageTaken = FleshGloveItem.onGloveBlock((LivingEntity) (Object) this, damageBlocked.get());

        if (damageTaken == 0) return value;
        amount.set(amount.get() + damageTaken);
        damageBlocked.set(damageBlocked.get() - damageTaken);
        return false;
    }
}
