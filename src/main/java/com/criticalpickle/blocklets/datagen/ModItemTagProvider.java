package com.criticalpickle.blocklets.datagen;

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.registration.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends TagsProvider<Item> {
    public ModItemTagProvider(
            PackOutput output, CompletableFuture<HolderLookup.Provider> registries
    ) {
        super(output, Registries.ITEM, registries, Blocklets.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.getOrCreateRawBuilder(ModTags.Items.QUARTZ_SLABS)
                .addElement(BuiltInRegistries.ITEM.getKey(Items.QUARTZ_SLAB))
                .addElement(BuiltInRegistries.ITEM.getKey(Items.SMOOTH_QUARTZ_SLAB))
                .replace(false);
    }
}
