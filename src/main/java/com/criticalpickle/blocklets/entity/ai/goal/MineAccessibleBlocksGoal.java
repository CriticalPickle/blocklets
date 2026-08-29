package com.criticalpickle.blocklets.entity.ai.goal;

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import com.criticalpickle.blocklets.util.BlockletAction;
import com.criticalpickle.blocklets.util.MiningClaimManager;
import com.criticalpickle.blocklets.util.ExcludeVisibleClipContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Goal to where entity mines any target blocks assuming they
 * are accessible by ground navigation and visible to the entity.
 */
public class MineAccessibleBlocksGoal extends Goal {
    private static final double ARRIVAL_TOLERANCE = 0.6; // Cannot go below 0.6 (hitbox size)
    protected final Mob minerMob;
    protected final TagKey<Block> targets;
    protected final int searchRadius;
    protected final int reachRadius;
    protected final double walkSpeed;

    private BlockPos targetBlockPos;
    private BlockPos targetStandingPos;
    private float miningProgress;
    private int breakStage;

    public MineAccessibleBlocksGoal(
            PathfinderMob mob, TagKey<Block> targetBlocks, int searchRadius,
            int reachRadius, double walkSpeed
    ) {
        this.minerMob = mob;
        this.targets = targetBlocks;
        this.searchRadius = searchRadius;
        this.reachRadius = reachRadius;
        this.walkSpeed = walkSpeed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    /**
     * Checks if block position is visible from current location of miner mob.
     * @param targetPos the block position to check is visible.
     * @return If block position is visible from current miner mob location.
     */
    protected boolean blockVisible(BlockPos targetPos) {
        final Vec3 eyePos = this.minerMob.getEyePosition();
        final Vec3 targetCenter = targetPos.getCenter();
        final Direction[] closestToFurthest = Direction.orderedByNearest(this.minerMob);

        for (Direction face : closestToFurthest) {
            final Vec3 faceTarget = targetCenter.add(
                    face.getStepX() * 0.49,
                    face.getStepY() * 0.49,
                    face.getStepZ() * 0.49
            );

            final ExcludeVisibleClipContext context = new ExcludeVisibleClipContext(
                    eyePos,
                    faceTarget,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    this.minerMob
            );

            final BlockHitResult hitResult = this.minerMob.level().clip(context);
            if (hitResult.getType() == HitResult.Type.MISS || (hitResult.getType() == HitResult.Type.BLOCK
                    && hitResult.getBlockPos().equals(targetPos))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets best valid standing spot for a given possible target.
     * @param possibleTarget the block position of a possible block target.
     * @return The best block position of a standing spot or null, if there is none.
     */
    private BlockPos findStandingSpot(BlockPos possibleTarget) {
        final Level level = this.minerMob.level();
        final Iterable<BlockPos> possibleStandingPositions = BlockPos.betweenClosed(
                possibleTarget.offset(-this.reachRadius, -this.reachRadius, -this.reachRadius),
                possibleTarget.offset(this.reachRadius, this.reachRadius, this.reachRadius)
        );
        BlockPos best = null;
        int bestPathLength = Integer.MAX_VALUE;

        for (BlockPos standingPos : possibleStandingPositions) {
            boolean bodyClear = !level.getBlockState(standingPos).isCollisionShapeFullBlock(level, standingPos);
            if (!bodyClear) continue;

            boolean floorSturdy = level.getBlockState(standingPos.below()).isFaceSturdy(level, standingPos.below(), Direction.UP);
            if (!floorSturdy) continue;

            final Path path = this.minerMob.getNavigation().createPath(standingPos, 0);
            if (path == null || !path.canReach()) continue;

            if (path.getNodeCount() < bestPathLength) {
                bestPathLength = path.getNodeCount();
                best = standingPos.immutable();
            }
        }
        return best;
    }

    /**
     * Finds and sets valid target block positon.
     * @return If valid target block position was found.
     */
    private boolean findValidTargets() {
        final Level level = this.minerMob.level();
        final BlockPos origin = this.minerMob.blockPosition();
        int bestPathLength = Integer.MAX_VALUE;
        final Iterable<BlockPos> possibleBlockPositions = BlockPos.betweenClosed(
                origin.offset(-this.searchRadius, -this.searchRadius, -this.searchRadius),
                origin.offset(this.searchRadius, this.searchRadius, this.searchRadius)
        );

        for (BlockPos blockPos : possibleBlockPositions) {
            if (!level.getBlockState(blockPos).is(this.targets)) continue;
            if (MiningClaimManager.isClaimed(level, blockPos)) continue;
            if (!blockVisible(blockPos)) continue;

            final BlockPos standingPos = findStandingSpot(blockPos);
            if (standingPos == null) continue;

            final Path path = this.minerMob.getNavigation().createPath(standingPos, 0);
            if (path == null) continue;

            if (path.getNodeCount() < bestPathLength) {
                bestPathLength = path.getNodeCount();
                this.targetBlockPos = blockPos.immutable();
                this.targetStandingPos = standingPos.immutable();
            }
        }

        return this.targetBlockPos != null && this.targetStandingPos != null;
    }

    @Override
    public boolean canUse() {
        return findValidTargets();
    }

    /**
     * Commands miner mob navigation to move to target block position.
     */
    protected void moveToBlock() {
        final boolean claimed = MiningClaimManager.tryClaim(this.targetBlockPos, this.minerMob, 5250);
        if (!claimed) {
            Blocklets.LOGGER.error("Unable to move {} to block at {} as it is already claimed!",
                    this.minerMob.getClass().getSimpleName(), this.targetBlockPos);
            stop();
        }
        else {
            final Path path = this.minerMob.getNavigation().createPath(this.targetStandingPos, 0);
            this.minerMob.getNavigation().moveTo(path, this.walkSpeed);
            Blocklets.LOGGER.info("MoveTo Called with: {}", path);
        }
    }

    @Override
    public void start() {
        this.miningProgress = 0;
        this.breakStage = 0;
        moveToBlock();
        if (this.minerMob instanceof BlockletEntity blocklet && !blocklet.isMining()
                && !blocklet.getStartAction().equals(BlockletAction.MINE.getClassName())) {
            blocklet.setStartAction(BlockletAction.MINE.getClassName());
        }
    }

    @Override
    public boolean canContinueToUse() {
        return targetBlockPos != null && this.minerMob.level().getBlockState(targetBlockPos).is(this.targets)
                && blockVisible(targetBlockPos);
    }

    @Override
    public void stop() {
        final boolean released = MiningClaimManager.tryRelease(this.targetBlockPos, this.minerMob);
        if (!released) Blocklets.LOGGER.error("Unable to release claim for {} at {}!",
                this.minerMob.getClass().getSimpleName(), this.targetBlockPos);

        if (this.targetBlockPos != null) {
            this.minerMob.level().destroyBlockProgress(this.minerMob.getId(), this.targetBlockPos, -1);
        }
        this.targetBlockPos = null;
        this.miningProgress = 0;
        this.breakStage = 0;
        this.minerMob.getNavigation().stop();
        if (this.minerMob instanceof BlockletEntity blocklet && blocklet.isMining()) {
            blocklet.setMining(false);
        }
        else if (this.minerMob instanceof BlockletEntity blocklet &&
                blocklet.getStartAction().equals(BlockletAction.MINE.getClassName())) {
            blocklet.setStartAction("");
        }
    }

    /**
     * @return If miner mob has arrived within arrival tolerance, can reach, and the block is visible.
     */
    protected boolean hasArrived() {
        final double dx = this.minerMob.getX() - this.targetStandingPos.getBottomCenter().x;
        final double dz = this.minerMob.getZ() - this.targetStandingPos.getBottomCenter().z;
        final double distSq = (dx * dx) + (dz * dz);
        final boolean atBlock = distSq < ARRIVAL_TOLERANCE * ARRIVAL_TOLERANCE;

        final double dy = this.minerMob.getY() - this.targetBlockPos.getBottomCenter().y;
        final double distSqY = dy * dy;
        final boolean canReach = distSqY <= this.reachRadius * this.reachRadius;

        return atBlock && canReach;
    }

    @Override
    public void tick() {
        this.minerMob.getLookControl().setLookAt(
                this.targetBlockPos.getCenter().x,
                this.targetBlockPos.getCenter().y,
                this.targetBlockPos.getCenter().z
        );

        if (!hasArrived()) {
            return;
        }

        this.minerMob.getNavigation().stop();

        final Level level = this.minerMob.level();
        final BlockState targetBlockState = level.getBlockState(this.targetBlockPos);
        final ItemStack tool = this.minerMob.getItemBySlot(EquipmentSlot.MAINHAND);
        final float harvestValue = tool.isCorrectToolForDrops(targetBlockState) ? 30.0f : 100.0f;
        final float destroySpeed = tool.getDestroySpeed(targetBlockState);
        final float hardness = targetBlockState.getDestroySpeed(level, this.targetBlockPos);
        if (hardness < 0.0f) {
            stop();
            return;
        }
        float damagePerTick = destroySpeed / hardness / harvestValue;
        if (damagePerTick > 1.0f) damagePerTick = 1.0f;

        this.miningProgress += damagePerTick;

        int stage = Math.clamp((int) (this.miningProgress * 10.0f), 0, 9);
        if (stage != this.breakStage) {
            level.destroyBlockProgress(this.minerMob.getId(), this.targetBlockPos, stage);
            this.breakStage = stage;
        }

        if (this.miningProgress >= 1.0f) {
            ItemStack particleStack = new ItemStack(targetBlockState.getBlock());
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.destroyBlock(this.targetBlockPos, true, this.minerMob);
                if (!particleStack.isEmpty()) {
                    serverLevel.sendParticles(
                            new ItemParticleOption(ParticleTypes.ITEM, particleStack),
                            targetBlockPos.getX() + 0.5,
                            targetBlockPos.getY() + 0.5,
                            targetBlockPos.getZ() + 0.5,
                            8,
                            0.3,
                            0.3,
                            0.3,
                            0.05
                    );
                }
            }

            stop();
        }
        else if (this.minerMob instanceof BlockletEntity blocklet && !blocklet.isMining()) {
            blocklet.setStartAction("");
            blocklet.setMining(true);
        }
    }
}
