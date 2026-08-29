package com.criticalpickle.blocklets.entity.client;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

// Made with Blockbench 5.1.6
// Exported for Minecraft version 1.19 or later with Mojang mappings

public class BlockletModelAnimations {
    public static final AnimationDefinition WALK = AnimationDefinition.Builder
            .withLength(0.8F)
            .looping()
            .addAnimation("right_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, 15F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, -15F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                            AnimationChannel.Interpolations.LINEAR)
            )).addAnimation("left_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, -15F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, 15F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                            AnimationChannel.Interpolations.LINEAR)
            )).addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, 5.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, -5.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                            AnimationChannel.Interpolations.LINEAR)
            )).addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, -5.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, 5.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                            AnimationChannel.Interpolations.LINEAR)
            )).build();

    public static final AnimationDefinition START_ACTION = AnimationDefinition.Builder
            .withLength(0.25F)
            .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, -90.0F),
                            AnimationChannel.Interpolations.LINEAR)
            )).build();
    
    public static final AnimationDefinition MINE = AnimationDefinition.Builder
            .withLength(0.8333F)
            .looping()
            .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, -90.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.1667F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, -120.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, -70.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8333F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, -90.0F),
                            AnimationChannel.Interpolations.LINEAR)
            )).build();

    public static final AnimationDefinition SWING = AnimationDefinition.Builder
            .withLength(1F)
            .looping()
            .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, -90.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.05F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, -110.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.15F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, -50.0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F,
                            KeyframeAnimations.degreeVec(0.0F, 0.0F, -90.0F),
                            AnimationChannel.Interpolations.LINEAR)
            ))
            .build();
}
