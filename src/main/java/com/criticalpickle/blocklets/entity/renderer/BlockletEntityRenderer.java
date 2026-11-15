package com.criticalpickle.blocklets.entity.renderer;

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.entity.client.BlockletModel;
import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import com.criticalpickle.blocklets.entity.renderer.layer.BlockletEntityBlockLayer;
import com.criticalpickle.blocklets.entity.renderer.layer.BlockletEntityToolLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BlockletEntityRenderer extends MobRenderer<BlockletEntity, BlockletModel<BlockletEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            Blocklets.MODID, "textures/entity/blocklet/blocklet.png"
    );

    public BlockletEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new BlockletModel<>(context.bakeLayer(BlockletModel.LAYER_LOCATION)), 0.3F);
        this.addLayer(new BlockletEntityBlockLayer<>(this));
        this.addLayer(new BlockletEntityToolLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BlockletEntity entity) {
        return TEXTURE;
    }
}
