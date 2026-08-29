package com.criticalpickle.blocklets.entity.ai.goal;

import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import com.criticalpickle.blocklets.util.BlockletAction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.jetbrains.annotations.NotNull;

/**
 * Simple MeleeAttackGoal wrapper to allow for differentiation between this and HusbandryGoal.
 */
public class AttackGoal extends MeleeAttackGoal {
    public AttackGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
    }

    @Override
    public void start() {
        super.start();
        if (this.mob instanceof BlockletEntity blocklet && !blocklet.isSwinging()
                && !blocklet.getStartAction().equals(BlockletAction.MELEE_ATTACK.getClassName())) {
            blocklet.setStartAction(BlockletAction.MELEE_ATTACK.getClassName());
        }
    }

    @Override
    protected void checkAndPerformAttack(@NotNull LivingEntity target) {
        if (this.canPerformAttack(target)) {
            this.resetAttackCooldown();
            if (this.mob instanceof BlockletEntity blocklet && !blocklet.isSwinging()) {
                blocklet.setSwinging(true);
                if (blocklet.getStartAction().equals(BlockletAction.MELEE_ATTACK.getClassName())) {
                    blocklet.setStartAction("");
                }
            }
            this.mob.doHurtTarget(target);
        }
    }

    @Override
    public void tick() {
        super.tick();
        final LivingEntity target = this.mob.getTarget();
        if (this.isTimeToAttack() && target != null && !this.canPerformAttack(target)
                && this.mob instanceof BlockletEntity blocklet && blocklet.isSwinging()) {
            blocklet.setSwinging(false);
            blocklet.setStartAction(BlockletAction.MELEE_ATTACK.getClassName());
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (this.mob instanceof BlockletEntity blocklet && blocklet.isSwinging()) {
            blocklet.setSwinging(false);
        }
        else if (this.mob instanceof BlockletEntity blocklet &&
                blocklet.getStartAction().equals(BlockletAction.MELEE_ATTACK.getClassName())) {
            blocklet.setStartAction("");
        }
    }
}
