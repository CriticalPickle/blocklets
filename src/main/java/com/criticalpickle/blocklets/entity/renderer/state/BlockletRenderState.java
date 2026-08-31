package com.criticalpickle.blocklets.entity.renderer.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.AnimationState;

public class BlockletRenderState extends LivingEntityRenderState {
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState startActionAnimationState = new AnimationState();
    public final AnimationState mineAnimationState = new AnimationState();
    public final AnimationState swingAnimationState = new AnimationState();
    public int lightTexture = -1;
    public final ItemStackRenderState blockRenderState = new ItemStackRenderState();
    public final ItemStackRenderState toolRenderState = new ItemStackRenderState();
}
