package com.criticalpickle.blocklets.datagen;

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.registration.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(
            PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, blockTags, Blocklets.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(ModTags.Items.QUARTZ_SLABS).add(
                Items.QUARTZ_SLAB, Items.SMOOTH_QUARTZ_SLAB
        ).replace(false);
    }
}
