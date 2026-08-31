package com.criticalpickle.blocklets.datagen;

import com.criticalpickle.blocklets.registration.ModBlocks;
import com.criticalpickle.blocklets.registration.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM),
                        RecipeCategory.MISC, ModBlocks.INCUBATION_CHAMBER.get())
                .define('Q', ModTags.Items.QUARTZ_SLABS)
                .define('G', Items.GLASS)
                .pattern("QQQ")
                .pattern(" G ")
                .pattern("QQQ")
                .group("incubation_chamber")
                .unlockedBy("glass", has(Tags.Items.GLASS_BLOCKS_CHEAP))
                .save(this.output);
    }

    public static class Runner extends RecipeProvider.Runner {
        protected Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(
                HolderLookup.@NotNull Provider provider, @NotNull RecipeOutput recipeOutput
        ) {
            return new ModRecipeProvider(provider, recipeOutput);
        }

        @Override
        public @NotNull String getName() {
            return "Blocklets Recipes";
        }
    }
}