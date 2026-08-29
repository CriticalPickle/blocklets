package com.criticalpickle.blocklets.entity.client;

// Made with Blockbench 5.1.6
// Exported for Minecraft version 1.17 or later with Mojang mappings

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BlockletModel<T extends BlockletEntity> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(Blocklets.MODID, "blocklet"), "main"
	);
	private final ModelPart root;
	private final ModelPart eyes;
	private final ModelPart arms;
	private final ModelPart legs;

	public BlockletModel(ModelPart root) {
		this.root = root;
		this.eyes = root.getChild("eyes");
		this.arms = root.getChild("arms");
		this.legs = root.getChild("legs");
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
	public void setupAnim(
			@NotNull BlockletEntity blocklet, float limbSwing, float limbSwingAmount,
			float ageInTicks, float netHeadYaw, float headPitch
	) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(blocklet.walkAnimationState, BlockletModelAnimations.WALK, ageInTicks);
		this.animate(blocklet.startActionAnimationState, BlockletModelAnimations.START_ACTION, ageInTicks);
		this.animate(blocklet.mineAnimationState, BlockletModelAnimations.MINE, ageInTicks);
		this.animate(blocklet.swingAnimationState, BlockletModelAnimations.SWING, ageInTicks);
	}

	@Override
	public void renderToBuffer(
			@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
			int packedLight, int packedOverlay, int color
	) {
		eyes.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		arms.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		legs.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}

	@Override
	public @NotNull ModelPart root() {
		return this.root;
	}

	public ModelPart getArms() {
		return arms;
	}
}