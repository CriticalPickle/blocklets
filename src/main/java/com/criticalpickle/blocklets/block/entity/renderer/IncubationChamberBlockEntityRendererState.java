package com.criticalpickle.blocklets.block.entity.renderer;

import com.criticalpickle.blocklets.block.entity.IncubationChamberBlockEntity;
import com.criticalpickle.blocklets.entity.renderer.state.BlockletRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class IncubationChamberBlockEntityRendererState extends BlockEntityRenderState {
    public IncubationChamberBlockEntity blockEntity;
    public EntityRenderState entityRenderState = null;
}
