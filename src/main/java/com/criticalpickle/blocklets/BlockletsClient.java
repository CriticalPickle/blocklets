package com.criticalpickle.blocklets;

import com.criticalpickle.blocklets.block.entity.renderer.IncubationChamberBlockEntityRenderer;
import com.criticalpickle.blocklets.entity.renderer.BlockletEntityRenderer;
import com.criticalpickle.blocklets.registration.ModBlockEntities;
import com.criticalpickle.blocklets.registration.ModEntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Blocklets.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Blocklets.MODID, value = Dist.CLIENT)
public class BlockletsClient {
    public BlockletsClient(ModContainer container) {
        // Create a config screen for this mod's configs built into NeoForge "mods" tab.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);;
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        Blocklets.LOGGER.info("Critical Crates is now being loaded on the client!");
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.BLOCKLET_ENTITY.get(), BlockletEntityRenderer::new);
        event.registerBlockEntityRenderer(
                ModBlockEntities.INCUBATION_CHAMBER_BE.get(), IncubationChamberBlockEntityRenderer::new
        );
    }
}
