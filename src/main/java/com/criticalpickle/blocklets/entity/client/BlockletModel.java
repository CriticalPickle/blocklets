package com.criticalpickle.blocklets.entity.client;

// Made with Blockbench 5.1.6
// Exported for Minecraft version 1.17 or later with Mojang mappings

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import com.criticalpickle.blocklets.entity.renderer.state.BlockletRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class BlockletModel extends EntityModel<BlockletRenderState> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			Identifier.fromNamespaceAndPath(Blocklets.MODID, "blocklet"), "main"
	);
	private final ModelPart arms;
	private final KeyframeAnimation bakedWalk;
	private final KeyframeAnimation bakedStartAction;
	private final KeyframeAnimation bakedMineAction;
	private final KeyframeAnimation bakedSwingAction;

	public BlockletModel(ModelPart root) {
        super(root);
		this.arms = root.getChild("arms");
		this.bakedWalk = BlockletModelAnimations.WALK.bake(root);
		this.bakedStartAction = BlockletModelAnimations.START_ACTION.bake(root);
		this.bakedMineAction = BlockletModelAnimations.MINE.bake(root);
		this.bakedSwingAction = BlockletModelAnimations.SWING.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("eyes", CubeListBuilder.create()
				.texOffs(6, 14)
				.addBox(0.0F, -1.0F, -3.0F, 1.0F, 2.0F, 2.0F,
						new CubeDeformation(0.0F))
				.texOffs(0, 14)
				.addBox(0.0F, -1.0F, 1.0F, 1.0F, 2.0F, 2.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 14.0F, -3.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition arms = partdefinition.addOrReplaceChild("arms", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		arms.addOrReplaceChild("left_arm", CubeListBuilder.create()
				.texOffs(12, 14)
				.addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F,
						new CubeDeformation(0.0F))
				.texOffs(8, 18)
				.addBox(-1.0F, 1.0F, 1.0F, 1.0F, 3.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offset(1.0F, -1.0F, 3.0F));

		arms.addOrReplaceChild("right_arm", CubeListBuilder.create()
				.texOffs(12, 17)
				.addBox(-1.0F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F,
						new CubeDeformation(0.0F))
				.texOffs(18, 12)
				.addBox(-1.0F, 1.0F, -2.0F, 1.0F, 3.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offset(1.0F, -1.0F, -3.0F));

		PartDefinition legs = partdefinition.addOrReplaceChild("legs", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		legs.addOrReplaceChild("left_leg", CubeListBuilder.create()
				.texOffs(4, 18)
				.addBox(0.0F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F,
						new CubeDeformation(0.0F))
				.texOffs(8, 12)
				.addBox(0.0F, 3.0F, 0.0F, 3.0F, 1.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offset(-1.0F, -4.0F, 2.0F));

		legs.addOrReplaceChild("right_leg", CubeListBuilder.create()
				.texOffs(0, 18)
				.addBox(0.0F, 0.0F, -1.0F, 1.0F, 3.0F, 1.0F,
						new CubeDeformation(0.0F))
				.texOffs(0, 12)
				.addBox(0.0F, 3.0F, -1.0F, 3.0F, 1.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offset(-1.0F, -4.0F, -2.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(@NonNull BlockletRenderState state) {
		super.setupAnim(state);
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.bakedWalk.apply(state.walkAnimationState, state.ageInTicks);
		this.bakedStartAction.apply(state.startActionAnimationState, state.ageInTicks);
		this.bakedMineAction.apply(state.mineAnimationState, state.ageInTicks);
		this.bakedSwingAction.apply(state.swingAnimationState, state.ageInTicks);
	}

	public ModelPart getArms() {
		return arms;
	}
}