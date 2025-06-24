package io.github.thebroccolibob.bobsmobgear.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import io.github.thebroccolibob.bobsmobgear.duck.EquipmentChanger;
import io.github.thebroccolibob.bobsmobgear.duck.WebShotUser;
import io.github.thebroccolibob.bobsmobgear.item.FleshGloveItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

@Debug(export = true)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EquipmentChanger {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract ItemStack getActiveItem();

    @Shadow
    protected abstract @Nullable Map<EquipmentSlot, ItemStack> getEquipmentChanges();

    @Shadow protected abstract void sendEquipmentChanges();

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

    @Override
    public void bobsmobgear$sidedEquipmentChanges() {
        if (getWorld().isClient)
            getEquipmentChanges();
        else
            sendEquipmentChanges();
    }

    @ModifyExpressionValue(
            method = "travel",
            at = @At(value = "CONSTANT", args = "floatValue=0.91", ordinal = 1)
    )
    private float noDrag(float original) {
        if (!(this instanceof WebShotUser webShotUser)) return original;
        var webShot = webShotUser.bobsmobgear$getWebShot();
        if (webShot == null || !webShot.isHookedOnBlock()) return original;
        return 1;
    }

    @ModifyExpressionValue(
            method = "travel",
            at = @At(value = "CONSTANT", args = "doubleValue=0.9800000190734863", ordinal = 1)
    )
    private double noDrag(double original) {
        if (!(this instanceof WebShotUser webShotUser)) return original;
        var webShot = webShotUser.bobsmobgear$getWebShot();
        if (webShot == null || !webShot.isHookedOnBlock()) return original;
        return 1;
    }
}
