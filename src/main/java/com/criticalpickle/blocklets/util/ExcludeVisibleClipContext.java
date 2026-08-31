package com.criticalpickle.blocklets.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * Simple extension of ClipContext to exclude clipping blocks that allow for visibility.
 */
public class ExcludeVisibleClipContext extends ClipContext {
    public ExcludeVisibleClipContext(Vec3 from, Vec3 to, Block block, Fluid fluid, Entity entity) {
        super(from, to, block, fluid, entity);
    }

    @Override
    public @NotNull VoxelShape getBlockShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        if (state.isAir()) return Shapes.empty();
        if (state.getBlock() instanceof StairBlock
                || state.getBlock() instanceof SlabBlock
                || state.getBlock() instanceof WallBlock) {
            final VoxelShape shape = state.getCollisionShape(level, pos);
            return shape.isEmpty() ? Shapes.empty() : shape;
        }
        if (!state.isSolidRender()) return Shapes.empty();
        return super.getBlockShape(state, level, pos);
    }
}
