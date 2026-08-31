package com.criticalpickle.blocklets.entity.renderer.layer;

import com.criticalpickle.blocklets.entity.client.BlockletModel;
import com.criticalpickle.blocklets.entity.renderer.state.BlockletRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jspecify.annotations.NonNull;

public class BlockletEntityBlockLayer extends RenderLayer<BlockletRenderState, BlockletModel> {
    public BlockletEntityBlockLayer(RenderLayerParent<BlockletRenderState, BlockletModel> parentRenderer) {
        super(parentRenderer);
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector,
                       int i, @NonNull BlockletRenderState state, float v, float v1
    ) {
        final ItemStackRenderState itemRendererState = state.blockRenderState;
        if (itemRendererState.isEmpty()) return;
        final int lightTexture = state.lightTexture;
        if (lightTexture == -1) return;

        poseStack.pushPose();
        this.getParentModel().getArms().translateAndRotate(poseStack);
        poseStack.translate(0f, 0.065f, 0f);
        poseStack.scale(0.75f, 0.75f, 0.75f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        itemRendererState.submit(poseStack, submitNodeCollector, lightTexture, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }
}
