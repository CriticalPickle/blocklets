package com.criticalpickle.blocklets.datagen;

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.registration.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagProvider(
            PackOutput output, CompletableFuture<HolderLookup.Provider> provider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, provider, Blocklets.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(ModTags.Entities.BLOCKLET_MELEE_ENEMIES).add(
                EntityType.CREEPER, EntityType.CAVE_SPIDER, EntityType.BLAZE,
                EntityType.BREEZE, EntityType.BOGGED, EntityType.DROWNED,
                EntityType.ELDER_GUARDIAN, EntityType.ENDERMAN, EntityType.ENDERMITE,
                EntityType.EVOKER, EntityType.GIANT, EntityType.GUARDIAN,
                EntityType.HOGLIN, EntityType.HUSK, EntityType.ILLUSIONER,
                EntityType.MAGMA_CUBE, EntityType.PIGLIN_BRUTE, EntityType.PILLAGER,
                EntityType.RAVAGER, EntityType.SILVERFISH, EntityType.SKELETON,
                EntityType.SLIME, EntityType.SPIDER, EntityType.STRAY,
                EntityType.VINDICATOR, EntityType.WITCH, EntityType.ZOGLIN,
                EntityType.ZOMBIE, EntityType.ZOMBIE_HORSE
        );

        tag(ModTags.Entities.BLOCKLET_AIR_ENEMIES).addTag(ModTags.Entities.BLOCKLET_MELEE_ENEMIES).add(
                EntityType.ENDER_DRAGON, EntityType.GHAST, EntityType.PHANTOM,
                EntityType.SHULKER, EntityType.VEX, EntityType.WITHER
        );

        tag(ModTags.Entities.BLOCKLET_HUSBANDRY).addTag(ModTags.Entities.BLOCKLET_MELEE_ENEMIES).add(
                EntityType.CHICKEN, EntityType.COW, EntityType.IRON_GOLEM,
                EntityType.MOOSHROOM, EntityType.RABBIT, EntityType.SHEEP
        );
    }
}
