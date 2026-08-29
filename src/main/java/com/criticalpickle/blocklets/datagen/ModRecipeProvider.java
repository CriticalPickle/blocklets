package com.criticalpickle.blocklets.datagen;

import com.criticalpickle.blocklets.registration.ModBlocks;
import com.criticalpickle.blocklets.registration.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.INCUBATION_CHAMBER.get())
                .define('Q', ModTags.Items.QUARTZ_SLABS)
                .define('G', Items.GLASS)
                .pattern("QQQ")
                .pattern(" G ")
                .pattern("QQQ")
                .group("incubation_chamber")
                .unlockedBy("glass", has(Tags.Items.GLASS_BLOCKS_CHEAP))
                .save(recipeOutput);
    }
}