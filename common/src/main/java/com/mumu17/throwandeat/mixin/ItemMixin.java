package com.mumu17.throwandeat.mixin;

import com.mumu17.throwandeat.entity.FoodProjectileEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void throwFoodItem(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem().getFoodProperties() != null) {

            if ((player.isCrouching() || player.isShiftKeyDown()) && FoodProjectileEntity.isActive) {

                level.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.SNOWBALL_THROW,
                        SoundSource.NEUTRAL,
                        0.5F,
                        0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
                );

                if (!level.isClientSide) {
                    FoodProjectileEntity projectile = new FoodProjectileEntity(level, player);
                    projectile.setItem(itemStack);
                    projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                    level.addFreshEntity(projectile);
                }

                player.awardStat(Stats.ITEM_USED.get((Item) (Object) this));

                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }

                cir.setReturnValue(InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide()));
            }
        }
    }
}
