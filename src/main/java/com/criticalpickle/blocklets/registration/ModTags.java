package com.criticalpickle.blocklets.registration;

import com.criticalpickle.blocklets.Blocklets;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> BLOCKLET_EYES_WHITELIST = createTag("blocklet_eyes_whitelist");
        public static final TagKey<Block> BLOCKLET_BODY_WHITELIST = createTag("blocklet_body_whitelist");
        public static final TagKey<Block> BLOCKLET_ARMS_WHITELIST = createTag("blocklet_arms_whitelist");
        public static final TagKey<Block> BLOCKLET_LEGS_WHITELIST = createTag("blocklet_legs_whitelist");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(Blocklets.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> QUARTZ_SLABS = createTag("quartz_slabs");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Blocklets.MODID, name));
        }
    }

    public static class Entities {
        public static final TagKey<EntityType<?>> BLOCKLET_MELEE_ENEMIES = createTag("blocklet_melee_enemies");
        public static final TagKey<EntityType<?>> BLOCKLET_AIR_ENEMIES = createTag("blocklet_air_enemies");
        public static final TagKey<EntityType<?>> BLOCKLET_HUSBANDRY = createTag("blocklet_husbandry");

        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Blocklets.MODID, name));
        }
    }
}
