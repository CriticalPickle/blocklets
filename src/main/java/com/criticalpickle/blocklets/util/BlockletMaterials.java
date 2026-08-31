package com.criticalpickle.blocklets.util;

import com.criticalpickle.blocklets.registration.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Associates blocklet body part with a material tag.
 */
public enum BlockletMaterials {
    EYES(ModTags.Blocks.BLOCKLET_EYES_WHITELIST, 0),
    BODY(ModTags.Blocks.BLOCKLET_BODY_WHITELIST, 1), // Needs additional check for solid blocks on top of material check.
    ARMS(ModTags.Blocks.BLOCKLET_ARMS_WHITELIST, 2),
    LEGS(ModTags.Blocks.BLOCKLET_LEGS_WHITELIST, 3);

    private final TagKey<Block> materials;
    private final int inventoryIndex;

    BlockletMaterials(TagKey<Block> validMaterials, int inventoryIndex) {
        this.materials = validMaterials;
        this.inventoryIndex = inventoryIndex;
    }

    public TagKey<Block> getMaterials() {
        return this.materials;
    }

    public int getInventoryIndex() {
        return inventoryIndex;
    }

    /**
     * Determines if block is considered a full block.
     * @param block the block to use for validation.
     * @return If the block is considered a full block.
     */
    private static boolean isFullBlock(Block block) {
        final BlockState state = block.defaultBlockState();
        final VoxelShape shape = state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        return Block.isShapeFullBlock(shape);
    }

    /**
     * Determines if items is of a valid material.
     * @param item the items to compare.
     * @return If the items is considered valid for given body part.
     */
    public boolean isValidMaterial(Item item) {
        return item instanceof BlockItem blockItem
                && (blockItem.getBlock().defaultBlockState().is(this.getMaterials())
                || (this.equals(BODY) && isFullBlock(blockItem.getBlock())));
    }
}
