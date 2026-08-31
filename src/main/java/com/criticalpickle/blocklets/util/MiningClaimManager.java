package com.criticalpickle.blocklets.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Claim manager for mining entities to ensure each block that is picked to be mined is unique.
 */
public class MiningClaimManager {
    private record Claim(UUID mobID, long expirationTick) {}
    private static final Map<ResourceKey<Level>, Map<BlockPos, Claim>> BLOCK_CLAIMS = new HashMap<>();

    /**
     * Checks if block at block position is claimed.
     * @param level the level to check at.
     * @param pos the block position to check at.
     * @return If block at block position is claimed.
     */
    public static boolean isClaimed(Level level, BlockPos pos) {
        final ResourceKey<Level> dimension = level.dimension();
        final Map<BlockPos, Claim> dimensionClaims = BLOCK_CLAIMS.computeIfAbsent(
                dimension, _ -> new HashMap<>());
        final long currentTick = level.getGameTime();
        return dimensionClaims.containsKey(pos) && currentTick <= dimensionClaims.get(pos).expirationTick();
    }

    /**
     * Try to and claim if available a block position under a specific blocklet.
     * @param pos the block position to attempt to claim.
     * @param possibleOwner the mob that is attempting to own this claim.
     * @param expireIn the number of ticks for claim to persist.
     * @return If the block position was claimed successfully.
     */
    public static boolean tryClaim(BlockPos pos, Mob possibleOwner, long expireIn) {
        final Level level = possibleOwner.level();
        final ResourceKey<Level> dimension = level.dimension();
        final Map<BlockPos, Claim> dimensionClaims = BLOCK_CLAIMS.computeIfAbsent(
                dimension, _ -> new HashMap<>());
        final long currentTick = level.getGameTime();

        if (isClaimed(level, pos)) return false;

        dimensionClaims.put(pos, new Claim(possibleOwner.getUUID(), currentTick + expireIn));
        return true;
    }

    /**
     * Attempts to release a claim made by a blocklet.
     * @param pos the block position of attempted release.
     * @param possibleOwner the suspected blocklet owner.
     * @return If the claim was successfully released.
     */
    public static boolean tryRelease(BlockPos pos, Mob possibleOwner) {
        final ResourceKey<Level> dimension = possibleOwner.level().dimension();
        final Map<BlockPos, Claim> dimensionClaims = BLOCK_CLAIMS.computeIfAbsent(
                dimension, _ -> new HashMap<>()
        );
        final Claim claim = dimensionClaims.getOrDefault(pos, null);
        if (claim != null && claim.mobID().equals(possibleOwner.getUUID())) {
            dimensionClaims.remove(pos);
            return true;
        }
        return false;
    }

    /**
     * Used to release a claim when a block is broken.
     * @param event the block break event to remove claim from.
     */
    public static void releaseFrom(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            final ResourceKey<Level> dimension = level.dimension();
            final Map<BlockPos, Claim> dimensionClaims = BLOCK_CLAIMS.computeIfAbsent(
                    dimension, _ -> new HashMap<>()
            );
            dimensionClaims.remove(event.getPos());
        }
    }
}
