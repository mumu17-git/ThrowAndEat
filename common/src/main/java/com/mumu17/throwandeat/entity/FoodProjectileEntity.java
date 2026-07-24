package com.mumu17.throwandeat.entity;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FoodProjectileEntity extends ThrowableItemProjectile {

    public static boolean isActive = true;

    public FoodProjectileEntity(EntityType<? extends FoodProjectileEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FoodProjectileEntity(Level level, LivingEntity shooter) {
        super(EntityType.SNOWBALL, shooter, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.APPLE;
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        if (!this.level().isClientSide) {
            ItemStack itemStack = this.getItem();
            if (result.getEntity() instanceof Player targetPlayer) {
                FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
                if (foodProperties != null) {
                    targetPlayer.getFoodData().eat(foodProperties.nutrition(), foodProperties.saturation());
                    for (FoodProperties.PossibleEffect possibleEffect : foodProperties.effects()) {
                        if (this.random.nextFloat() < possibleEffect.probability()) {
                            targetPlayer.addEffect(possibleEffect.effect());
                        }
                    }
                    this.level().playSound(
                            null,
                            targetPlayer.getX(),
                            targetPlayer.getY(),
                            targetPlayer.getZ(),
                            targetPlayer.getEatingSound(itemStack),
                            targetPlayer.getSoundSource(),
                            0.5F,
                            this.random.nextFloat() * 0.1F + 0.9F
                    );
                }
            } else if (result.getEntity() instanceof AbstractVillager targetVillager) {
                healMob(targetVillager, itemStack);
            } else if (result.getEntity() instanceof AbstractHorse targetHorse) {
                healMob(targetHorse, itemStack);
            } else if (result.getEntity() instanceof Pig targetPig && targetPig.isSaddled()) {
                healMob(targetPig, itemStack);
            } else if (result.getEntity() instanceof Strider targetStrider && targetStrider.isSaddled()) {
                healMob(targetStrider, itemStack);
            } else if (result.getEntity() instanceof LivingEntity targetMob) {
                Player thrower = this.getOwner() instanceof Player player ? player : null;

                if (thrower != null) {
                    Vec3 originalPos = thrower.position();
                    boolean actionExecuted = false;
                    try {
                        FoodProjectileEntity.isActive = false;
                        thrower.setPos(this.getX(), this.getY(), this.getZ());

                        InteractionResult interactResult = targetMob.interact(thrower, InteractionHand.MAIN_HAND);

                        if (!interactResult.consumesAction()) {
                            interactResult = itemStack.interactLivingEntity(thrower, targetMob, InteractionHand.MAIN_HAND);
                        }

                        if (!interactResult.consumesAction()) {
                            itemStack.use(this.level(), thrower, InteractionHand.MAIN_HAND);
                        }

                        actionExecuted = interactResult.consumesAction();

                    } finally {
                        thrower.setPos(originalPos.x, originalPos.y, originalPos.z);
                        FoodProjectileEntity.isActive = true;
                    }

                    if (!actionExecuted) {
                        this.healMob(targetMob, itemStack);
                    }
                }
            }
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    private void healMob(LivingEntity targetMob, ItemStack itemStack) {
        FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
        if (foodProperties != null) {
            float healAmount = foodProperties.nutrition() * 2.0F;

            targetMob.heal(healAmount);

            for (FoodProperties.PossibleEffect possibleEffect : foodProperties.effects()) {
                if (this.random.nextFloat() < possibleEffect.probability()) {
                    targetMob.addEffect(possibleEffect.effect());
                }
            }

            this.level().playSound(
                    null,
                    targetMob.getX(),
                    targetMob.getY(),
                    targetMob.getZ(),
                    targetMob.getEatingSound(itemStack),
                    targetMob.getSoundSource(),
                    0.5F,
                    this.random.nextFloat() * 0.1F + 0.9F
            );

            this.level().broadcastEntityEvent(targetMob, (byte) 7);
        }
    }
}