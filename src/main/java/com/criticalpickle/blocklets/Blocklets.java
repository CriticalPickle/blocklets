package com.criticalpickle.blocklets;

import com.criticalpickle.blocklets.entity.client.BlockletModel;
import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import com.criticalpickle.blocklets.registration.ModBlocks;
import com.criticalpickle.blocklets.registration.ModEntityTypes;
import com.criticalpickle.blocklets.registration.ModRegistration;
import com.criticalpickle.blocklets.util.MiningClaimManager;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// Coded by CriticalPickle

@Mod(Blocklets.MODID)
public class Blocklets {
    public static final String MODID = "blocklets";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Blocklets(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerBlockColors);
        modEventBus.addListener(this::registerLayers);
        modEventBus.addListener(this::registerAttributes);

        ModRegistration.init(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        // Register items to existing creative tabs
        modEventBus.addListener(ModRegistration::addCreative);

        // Register NeoForge's ModConfigSpec so that FML can create and load the config file
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Thanks for downloading Critical Creatures! Setting up...");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Critical Crates is now being loaded on the server!");
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel) {
            MiningClaimManager.releaseFrom(event);
        }
    }

    public void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                (state, level, pos, tintIndex)
                        -> level != null && pos != null ? BiomeColors.getAverageWaterColor(level, pos) : -1,
                ModBlocks.INCUBATION_CHAMBER.get()
        );
    }

    public void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BlockletModel.LAYER_LOCATION, BlockletModel::createBodyLayer);
    }

    public void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.BLOCKLET_ENTITY.get(), BlockletEntity.createAttributes().build());
    }
}
