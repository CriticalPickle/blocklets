package com.criticalpickle.blocklets.entity.renderer.layer;

import com.criticalpickle.blocklets.entity.client.BlockletModel;
import com.criticalpickle.blocklets.entity.renderer.state.BlockletRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jspecify.annotations.NonNull;

public class BlockletEntityToolLayer extends RenderLayer<BlockletRenderState, BlockletModel> {
    public BlockletEntityToolLayer(RenderLayerParent<BlockletRenderState, BlockletModel> parentRenderer) {
        super(parentRenderer);
    }

    @Override
    public void submit(
            @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector,
            int i, @NonNull BlockletRenderState state, float v, float v1
    ) {
        final ItemStackRenderState itemRendererState = state.toolRenderState;
        if (itemRendererState.isEmpty()) return;
        final int lightTexture = state.lightTexture;
        if (lightTexture == -1) return;

        final ModelPart arms =  this.getParentModel().getArms();
        arms.translateAndRotate(poseStack);
        arms.getChild("right_arm").translateAndRotate(poseStack);

        poseStack.pushPose();
        this.getParentModel().getArms().translateAndRotate(poseStack);
        poseStack.translate(0.1f, -0.8f, 0.12f);
        poseStack.scale(0.4f, 0.4f, 0.4f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(45));
        itemRendererState.submit(poseStack, submitNodeCollector, lightTexture, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }
}
