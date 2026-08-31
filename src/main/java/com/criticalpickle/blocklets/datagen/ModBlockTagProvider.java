package com.criticalpickle.blocklets.datagen;

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.registration.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(
            PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(output, lookupProvider, Blocklets.MODID);
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
