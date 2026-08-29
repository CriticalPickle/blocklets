package com.criticalpickle.blocklets.registration;

import com.criticalpickle.blocklets.Blocklets;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, Blocklets.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCKLETS_TAB = CREATIVE_MODE_TABS
            .register("blocklets_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.blocklets"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> new ItemStack(ModBlocks.INCUBATION_CHAMBER.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.INCUBATION_CHAMBER.get());
                    }).build());
}
