package com.criticalpickle.blocklets.block.entity.renderer;

import com.criticalpickle.blocklets.Config;
import com.criticalpickle.blocklets.block.IncubationChamberBlock;
import com.criticalpickle.blocklets.block.entity.IncubationChamberBlockEntity;
import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import com.criticalpickle.blocklets.registration.ModEntityTypes;
import com.criticalpickle.blocklets.util.BlockletMaterials;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;

import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class IncubationChamberBlockEntityRenderer implements BlockEntityRenderer<IncubationChamberBlockEntity> {
    public IncubationChamberBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(
            @NotNull IncubationChamberBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource multiBufferSource, int packedLight, int packedOverlay
    ) {
        final ItemStackHandler entityInventory = blockEntity.getInventory();
        final Level level = blockEntity.getLevel();
        if (level == null) return;
        if (!Config.BLOCKLET_CAN_INCUBATE.getAsBoolean() || !blockEntity.inventoryIsFull()) {
            final int lightTexture = LightTexture.pack(
                    level.getBrightness(LightLayer.BLOCK, blockEntity.getBlockPos()),
                    level.getBrightness(LightLayer.SKY, blockEntity.getBlockPos())
            );

            for (int i = 0; i < entityInventory.getSlots(); i++) {
                final ItemStack stack = entityInventory.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
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
                itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, lightTexture, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, level, 1);
                poseStack.popPose();
            }
        }
        else {
            final EntityRenderDispatcher entityDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            final BlockletEntity blocklet = new BlockletEntity(ModEntityTypes.BLOCKLET_ENTITY.get(), level);
            final float scale = (blockEntity.getBlockState().getValue(IncubationChamberBlock.GROWTH_STAGE) + 1) / 10.0f;

            blocklet.setNoAi(true);
            blocklet.setNoGravity(true);
            blocklet.setBlockStack(entityInventory.getStackInSlot(BlockletMaterials.BODY.getInventoryIndex()));

            poseStack.pushPose();
            poseStack.translate(0.5f, 0.19f, 0.5f);
            poseStack.scale(scale, scale, scale);

            entityDispatcher.render(
                    blocklet, 0.0, 0.0, 0.0, 0.0f,
                    partialTick, poseStack, multiBufferSource, packedLight
            );
            poseStack.popPose();
        }
    }
}
