package io.github.thebroccolibob.bobsmobgear.mixin;

import io.github.thebroccolibob.bobsmobgear.block.TemplateBlock;
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearBlocks;
import io.github.thebroccolibob.bobsmobgear.registry.BobsMobGearItemTags;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {
    @Shadow public abstract boolean isIn(TagKey<Block> tag);

    @Inject(
            method = "onUseWithItem",
            at = @At("HEAD"),
            cancellable = true)
    private void allowPlacingTemplate(ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ItemActionResult> cir) {
        if (!this.isIn(BobsMobGearBlocks.SMITHING_SURFACE)
                || hit.getSide() != Direction.UP
                || !(stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TemplateBlock || stack.isIn(BobsMobGearItemTags.PREVENT_SMITHING_TABLE_SCREEN)))
            return;

        cir.setReturnValue(ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION);
    }
}
