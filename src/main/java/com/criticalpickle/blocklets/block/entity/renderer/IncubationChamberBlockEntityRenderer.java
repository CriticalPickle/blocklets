package com.criticalpickle.blocklets.block.entity.renderer;

import com.criticalpickle.blocklets.Config;
import com.criticalpickle.blocklets.block.IncubationChamberBlock;
import com.criticalpickle.blocklets.block.entity.IncubationChamberBlockEntity;
import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import com.criticalpickle.blocklets.registration.ModEntityTypes;
import com.criticalpickle.blocklets.util.BlockletMaterials;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class IncubationChamberBlockEntityRenderer implements BlockEntityRenderer<IncubationChamberBlockEntity, IncubationChamberBlockEntityRendererState> {
    public IncubationChamberBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public @NonNull IncubationChamberBlockEntityRendererState createRenderState() {
        return new IncubationChamberBlockEntityRendererState();
    }

    @Override
    public void extractRenderState(
            @NonNull IncubationChamberBlockEntity blockEntity,
            @NonNull IncubationChamberBlockEntityRendererState rendererState,
            float partialTicks, @NonNull Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(
                blockEntity, rendererState, partialTicks, cameraPosition, breakProgress
        );
        rendererState.blockEntity = blockEntity;
        final BlockletEntity blocklet = new BlockletEntity(ModEntityTypes.BLOCKLET_ENTITY.get(), blockEntity.getLevel());
        blocklet.setNoAi(true);
        blocklet.setNoGravity(true);
        blocklet.setBlockStack(blockEntity.getInventory().getResource(BlockletMaterials.BODY.getInventoryIndex()).toStack());
        final EntityRenderDispatcher entityDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        rendererState.entityRenderState = entityDispatcher.extractEntity(blocklet, partialTicks);
    }

    @Override
    public void submit(
            @NonNull IncubationChamberBlockEntityRendererState rendererState, @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState cameraRenderState
    ) {
        final IncubationChamberBlockEntity blockEntity = rendererState.blockEntity;
        final ItemStacksResourceHandler entityInventory = blockEntity.getInventory();
        final Level level = blockEntity.getLevel();
        if (level == null) return;
        if (!Config.BLOCKLET_CAN_INCUBATE.getAsBoolean() || !blockEntity.inventoryIsFull()) {
            final int lightTexture = LightCoordsUtil.pack(
                    level.getBrightness(LightLayer.BLOCK, blockEntity.getBlockPos()),
                    level.getBrightness(LightLayer.SKY, blockEntity.getBlockPos())
            );

            for (int i = 0; i < entityInventory.size(); i++) {
                final ItemStack stack = entityInventory.getResource(i).toStack();
                if (stack.isEmpty()) continue;
                final ItemStackRenderState itemRendererState = new ItemStackRenderState();
                poseStack.pushPose();

                float xOffset = 0.45f;
                float yOffset = 0.325f;
                if (i == BlockletMaterials.EYES.getInventoryIndex()) {
                    xOffset = 0.55f;
                    yOffset = 0.775f;
                }
                else if (i == BlockletMaterials.BODY.getInventoryIndex()) {
                    yOffset = 0.625f;
                    xOffset = 0.3f;
                }
                else if (i == BlockletMaterials.ARMS.getInventoryIndex()) {
                    yOffset = 0.475f;
                    xOffset = 0.7f;
                }

                poseStack.translate(xOffset, yOffset, 0.5f);
                poseStack.scale(0.25f, 0.25f, 0.25f);

                Minecraft.getInstance().getItemModelResolver().updateForTopItem(
                        itemRendererState, stack, ItemDisplayContext.FIXED, level, null, 0
                );

                itemRendererState.submit(
                        poseStack, submitNodeCollector, lightTexture, OverlayTexture.NO_OVERLAY, 0
                );
                poseStack.popPose();
            }
        }
        else {
            final int growthStage = blockEntity.getBlockState().getValue(IncubationChamberBlock.GROWTH_STAGE);
            final float scale = (growthStage + 1) / 10.0f;
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.19f, 0.5f);
            poseStack.scale(scale, scale, scale);
            Minecraft.getInstance().getEntityRenderDispatcher().submit(
                    rendererState.entityRenderState, cameraRenderState,
                    0.0, 0.0, 0.0,
                    poseStack, submitNodeCollector
            );
            poseStack.popPose();
        }
    }
}
