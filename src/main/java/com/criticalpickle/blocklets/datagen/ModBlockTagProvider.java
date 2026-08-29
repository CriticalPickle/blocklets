package com.criticalpickle.blocklets.datagen;

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.registration.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(
            PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, Blocklets.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(ModTags.Blocks.BLOCKLET_EYES_WHITELIST)
                .add(Blocks.WHITE_CONCRETE, Blocks.WHITE_CONCRETE_POWDER, Blocks.WHITE_WOOL);
        tag(ModTags.Blocks.BLOCKLET_BODY_WHITELIST).addTag(Tags.Blocks.CHESTS);
        tag(ModTags.Blocks.BLOCKLET_ARMS_WHITELIST)
                .add(Blocks.BLACK_CONCRETE, Blocks.BLACK_CONCRETE_POWDER, Blocks.BLACK_WOOL);
        tag(ModTags.Blocks.BLOCKLET_LEGS_WHITELIST)
                .add(Blocks.BLACK_CONCRETE, Blocks.BLACK_CONCRETE_POWDER, Blocks.BLACK_WOOL);
    }
}
