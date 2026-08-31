package com.criticalpickle.blocklets.registration;

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.block.IncubationChamberBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Blocklets.MODID);

    public static final DeferredBlock<Block> INCUBATION_CHAMBER = registerBlockWithItem("incubation_chamber",
            () -> new IncubationChamberBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_STAINED_GLASS)
                    .setId(ResourceKey.create(Registries.BLOCK,
                            Identifier.fromNamespaceAndPath(Blocklets.MODID, "incubation_chamber")))
            ));

    private static  <T extends Block> DeferredBlock<T> registerBlockWithItem(String name, Supplier<T> block) {
        final DeferredBlock<T> REGISTERED_BLOCK = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(REGISTERED_BLOCK.get(), new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Blocklets.MODID, name)))));
        return REGISTERED_BLOCK;
    }
}
