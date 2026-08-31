package com.criticalpickle.blocklets.entity.renderer;

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.entity.client.BlockletModel;
import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import com.criticalpickle.blocklets.entity.renderer.layer.BlockletEntityBlockLayer;
import com.criticalpickle.blocklets.entity.renderer.layer.BlockletEntityToolLayer;
import com.criticalpickle.blocklets.entity.renderer.state.BlockletRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.LightLayer;
import org.jspecify.annotations.NonNull;

public class BlockletEntityRenderer extends MobRenderer<BlockletEntity, BlockletRenderState, BlockletModel> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(Blocklets.MODID, "textures/entity/blocklet/blocklet.png");

    public BlockletEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new BlockletModel(context.bakeLayer(BlockletModel.LAYER_LOCATION)), 0.3F);
        this.addLayer(new BlockletEntityBlockLayer(this));
        this.addLayer(new BlockletEntityToolLayer(this));
    }

    @Override
    public @NonNull BlockletRenderState createRenderState() {
        return new BlockletRenderState();
    }

    @Override
    public @NonNull Identifier getTextureLocation(@NonNull BlockletRenderState blockletRenderState) {
        return TEXTURE;
    }

    @Override
    public void extractRenderState(
            @NonNull BlockletEntity blocklet, @NonNull BlockletRenderState state, float partialTicks
    ) {
        super.extractRenderState(blocklet, state, partialTicks);
        state.walkAnimationState.copyFrom(blocklet.walkAnimationState);
        state.startActionAnimationState.copyFrom(blocklet.startActionAnimationState);
        state.mineAnimationState.copyFrom(blocklet.mineAnimationState);
        state.swingAnimationState.copyFrom(blocklet.swingAnimationState);
        state.lightTexture = LightCoordsUtil.pack(
                blocklet.level().getBrightness(LightLayer.BLOCK, blocklet.blockPosition()),
                blocklet.level().getBrightness(LightLayer.SKY, blocklet.blockPosition())
        );
        final ItemModelResolver modelResolver = Minecraft.getInstance().getItemModelResolver();
        modelResolver.updateForTopItem(
                state.blockRenderState, blocklet.getBlockStack(),
                ItemDisplayContext.FIXED, blocklet.level(), null, 0
        );
        modelResolver.updateForTopItem(
                state.toolRenderState, blocklet.getEquippedItem(),
                ItemDisplayContext.FIXED, blocklet.level(), null, 0
        );
    }
}
