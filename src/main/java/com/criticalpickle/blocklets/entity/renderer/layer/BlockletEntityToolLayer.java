package com.criticalpickle.blocklets.entity.renderer.layer;

import com.criticalpickle.blocklets.entity.client.BlockletModel;
import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
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

public class BlockletEntityToolLayer<T extends BlockletEntity, M extends BlockletModel<T>> extends RenderLayer<T, M> {
    public BlockletEntityToolLayer(RenderLayerParent<T, M> parentRenderer) {
        super(parentRenderer);
    }

    @Override
    public void render(
            @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight,
            T blocklet, float limbSwing, float limbSwingAmount, float partialTick,
            float ageInTicks, float netHeadYaw, float headPitch
    ) {
        final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        final ItemStack stack = blocklet.getEquippedItem();
        if (stack.isEmpty()) return;

        final int lightTexture = LightTexture.pack(
                blocklet.level().getBrightness(LightLayer.BLOCK, blocklet.blockPosition()),
                blocklet.level().getBrightness(LightLayer.SKY, blocklet.blockPosition())
        );

        final ModelPart arms =  this.getParentModel().getArms();
        arms.translateAndRotate(poseStack);
        arms.getChild("right_arm").translateAndRotate(poseStack);

        poseStack.pushPose();
        poseStack.translate(0.12f, 0.2f, -0.1f);
        poseStack.scale(0.4f, 0.4f, 0.4f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.mulPose(Axis.ZP.rotationDegrees(45));
        itemRenderer.renderStatic(
                stack, ItemDisplayContext.FIXED, lightTexture, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, blocklet.level(), 1
        );
        poseStack.popPose();
    }
}
