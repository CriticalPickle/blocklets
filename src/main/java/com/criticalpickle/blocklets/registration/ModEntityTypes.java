package com.criticalpickle.blocklets.registration;

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Blocklets.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<BlockletEntity>> BLOCKLET_ENTITY =
            ENTITY_TYPES.register(
                    "blocklet",
                    () -> EntityType.Builder.of(BlockletEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 0.7f)
                            .clientTrackingRange(10)
                            .build(ResourceKey.create(
                                    Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(Blocklets.MODID, "blocklet")
                            ))
            );
}
