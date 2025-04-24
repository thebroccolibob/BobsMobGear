package io.github.thebroccolibob.bobsmobgear.mixin;

import io.github.thebroccolibob.bobsmobgear.event.ItemTickCallback;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Inject(at = @At("TAIL"), method = "inventoryTick")
	private void itemTickCallback(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
		ItemTickCallback.EVENT.invoker().tick(entity, (ItemStack) (Object) this);
	}
}
