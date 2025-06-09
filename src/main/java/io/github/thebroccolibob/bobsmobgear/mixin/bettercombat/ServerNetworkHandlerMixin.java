package io.github.thebroccolibob.bobsmobgear.mixin.bettercombat;

import io.github.thebroccolibob.bobsmobgear.item.WardenFistItem;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.network.Packets;
import net.bettercombat.network.ServerNetwork;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerNetwork.class)
public class ServerNetworkHandlerMixin {
    @Inject(
            method = "lambda$handleAttackRequest$4",
            at = @At("TAIL")
    )
    private static void stopUsingWardenFist(ServerPlayerEntity player, Packets.C2S_AttackRequest request, AttackHand hand, WeaponAttributes attributes, WeaponAttributes.Attack attack, ServerWorld world, boolean useVanillaPacket, ServerPlayNetworkHandler handler, CallbackInfo ci) {
        if (!player.isUsingItem()) return;
        var weapon = hand.itemStack().getItem();
        if (weapon instanceof WardenFistItem) {
            if (request.entityIds().length == 0)
                player.getItemCooldownManager().set(weapon, 20);
            player.stopUsingItem();
        }
    }
}
