package com.criticalpickle.blocklets.registration;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class ModRegistration {
    public static void init(IEventBus modEventBus) {
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTabs.CREATIVE_MODE_TABS.register(modEventBus);
    }

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        // EX: if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
        //  event.accept(ITEM);
        // }
    }
}
