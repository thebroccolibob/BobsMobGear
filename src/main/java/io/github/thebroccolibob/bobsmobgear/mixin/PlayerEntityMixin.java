package io.github.thebroccolibob.bobsmobgear.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.thebroccolibob.bobsmobgear.duck.WebShotUser;
import io.github.thebroccolibob.bobsmobgear.entity.WebShotEntity;
import io.github.thebroccolibob.bobsmobgear.item.WardenFistItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements WebShotUser {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    @Nullable WebShotEntity bobsmobgear$webShot = null;

    @WrapOperation(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSources;playerAttack(Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/entity/damage/DamageSource;")
    )
    private DamageSource modifyDamageSource(DamageSources instance, PlayerEntity attacker, Operation<DamageSource> original) {
        var activeItem = getActiveItem();
        return activeItem == null || !(activeItem.getItem() instanceof WardenFistItem) ? original.call(instance, attacker) : instance.sonicBoom(attacker);
    }

    @Override
    public @Nullable WebShotEntity bobsmobgear$getWebShot() {
        if (bobsmobgear$webShot == null) return null;
        if (bobsmobgear$webShot.isRemoved())
            bobsmobgear$webShot = null;
        return bobsmobgear$webShot;
    }

    @Override
    public void bobsmobgear$setWebShot(@Nullable WebShotEntity webShot) {
        bobsmobgear$webShot = webShot;
    }
}
