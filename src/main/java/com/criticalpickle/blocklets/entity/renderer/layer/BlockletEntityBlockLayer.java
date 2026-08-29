package com.criticalpickle.blocklets.entity.renderer.layer;

import com.criticalpickle.blocklets.entity.client.BlockletModel;
import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;

public class BlockletEntityBlockLayer<T extends BlockletEntity, M extends BlockletModel<T>> extends RenderLayer<T, M> {
    public BlockletEntityBlockLayer(RenderLayerParent<T, M> parentRenderer) {
        super(parentRenderer);
    }

    @Override
    public void render(
            @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
            int packedLight, T blocklet, float limbSwing, float limbSwingAmount,
            float partialTick, float ageInTicks, float netHeadYaw, float headPitch
    ) {
        final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        final ItemStack stack = blocklet.getBlockStack();
        if (stack.isEmpty()) return;

        final int lightTexture = LightTexture.pack(
                blocklet.level().getBrightness(LightLayer.BLOCK, blocklet.blockPosition()),
                blocklet.level().getBrightness(LightLayer.SKY, blocklet.blockPosition())
        );
        poseStack.pushPose();
        this.getParentModel().getArms().translateAndRotate(poseStack);
        poseStack.translate(0f, 0.065f, 0f);
        poseStack.scale(0.75f, 0.75f, 0.75f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        itemRenderer.renderStatic(
                stack, ItemDisplayContext.FIXED, lightTexture, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, blocklet.level(), 1
        );
        poseStack.popPose();
    }
}
