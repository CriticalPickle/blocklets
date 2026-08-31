package com.criticalpickle.blocklets.registration;

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.block.entity.IncubationChamberBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, Blocklets.MODID);

    public static final Supplier<BlockEntityType<IncubationChamberBlockEntity>> INCUBATION_CHAMBER_BE = BLOCK_ENTITIES
            .register("crate_be", () -> new BlockEntityType<>(
                    IncubationChamberBlockEntity::new, ModBlocks.INCUBATION_CHAMBER.get())
            );
}
