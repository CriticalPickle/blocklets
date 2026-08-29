package com.criticalpickle.blocklets.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

/**
 * Goal to where any entity (not just tamable animals) can follow owner, if it implements OwnableEntity.
 */
public class EntityFollowOwnerGoal<T extends PathfinderMob & OwnableEntity> extends Goal {
    private final T followerMob;
    private LivingEntity owner = null;
    private final double speedModifier;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;

    public EntityFollowOwnerGoal(T followerMob, double speedModifier, float startDistance, float stopDistance) {
        this.followerMob = followerMob;
        this.speedModifier = speedModifier;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        if (!(followerMob.getNavigation() instanceof GroundPathNavigation)
                && !(followerMob.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    public final boolean unableToMoveToOwner() {
        return this.followerMob.isPassenger() || this.followerMob.mayBeLeashed() ||
                (this.followerMob.getOwner() != null && this.followerMob.getOwner().isSpectator());
    }

    private boolean canTeleportTo(BlockPos pos) {
        final PathType pathtype = WalkNodeEvaluator.getPathTypeStatic(this.followerMob, pos);
        if (pathtype != PathType.WALKABLE) {
            return false;
        } else {
            BlockState blockstate = this.followerMob.level().getBlockState(pos.below());
            if (blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = pos.subtract(this.followerMob.blockPosition());
                return this.followerMob.level().noCollision(
                        this.followerMob, this.followerMob.getBoundingBox().move(blockpos)
                );
            }
        }
    }

    private boolean maybeTeleportTo(int x, int y, int z) {
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.followerMob.moveTo((double)x + (double)0.5f, y, (double)z + (double)0.5f,
                    this.followerMob.getYRot(), this.followerMob.getXRot());
            this.followerMob.getNavigation().stop();
            return true;
        }
    }

    public boolean shouldTryTeleportToOwner() {
        return this.owner != null && this.followerMob.distanceToSqr(this.owner) >= (double)144.0f;
    }

    public void tryToTeleportToOwner() {
        if (this.owner != null) {
            final BlockPos pos = this.owner.blockPosition();
            for(int i = 0; i < 10; ++i) {
                int j = this.followerMob.getRandom().nextIntBetweenInclusive(-3, 3);
                int k = this.followerMob.getRandom().nextIntBetweenInclusive(-3, 3);
                if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                    int l = this.followerMob.getRandom().nextIntBetweenInclusive(-1, 1);
                    if (maybeTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
                        return;
                    }
                }
            }
        }
    }

    @Override
    public boolean canUse() {
        final LivingEntity ownerEntity = this.followerMob.getOwner();
        if (ownerEntity == null || unableToMoveToOwner()) {
            return false;
        }
        else if (this.followerMob.distanceToSqr(ownerEntity) < (double)(this.startDistance * this.startDistance)) {
            return false;
        }
        else {
            this.owner = ownerEntity;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.followerMob.getNavigation().isDone()) {
            return false;
        }
        else {
            return !unableToMoveToOwner() && this.owner != null && !(this.followerMob.distanceToSqr(this.owner)
                    <= (double) (this.stopDistance * this.stopDistance));
        }
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.followerMob.getPathfindingMalus(PathType.WATER);
        this.followerMob.setPathfindingMalus(PathType.WATER, 0.0f);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.followerMob.getNavigation().stop();
        this.followerMob.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        final boolean flag = shouldTryTeleportToOwner();
        if (!flag && this.owner != null) {
            this.followerMob.getLookControl().setLookAt(
                    this.owner, 10.0f, (float)this.followerMob.getMaxHeadXRot()
            );
        }

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (flag) {
                tryToTeleportToOwner();
            } else {
                this.followerMob.getNavigation().moveTo(this.owner, this.speedModifier);
            }
        }
    }
}
