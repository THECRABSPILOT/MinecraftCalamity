package crab.mods.minecraftcalamity.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import crab.mods.minecraftcalamity.MinecraftCalamity;

public class CalamititeChampionArmorModel extends HumanoidModel<LivingEntity> {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(MinecraftCalamity.MODID, "calamitite_armor"), "main");

	private final ModelPart root;
	private final ModelPart boot;

	public CalamititeChampionArmorModel(ModelPart root) {
		// Pass root directly to super
		super(root);

		this.root = root;
		this.boot = root.hasChild("right_leg") && root.getChild("right_leg").hasChild("boot")
				? root.getChild("right_leg").getChild("boot")
				: null;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		// Standard Humanoid Parts attached directly to root
		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 25).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.05F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(82, 72).addBox(-1.0F, -4.5F, -1.0F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -8.5F, -3.0F, -0.5236F, 0.2182F, 0.0F));
		head.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(68, 72).addBox(-3.0F, -5.5F, -1.0F, 4.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -3.5F, -4.0F, 0.0F, 0.2182F, 0.0F));
		head.addOrReplaceChild("Head_r3", CubeListBuilder.create().texOffs(82, 83).addBox(-1.0F, -4.5F, -1.0F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -8.5F, -3.6F, -0.5236F, -0.2094F, 0.0F));
		head.addOrReplaceChild("Head_r4", CubeListBuilder.create().texOffs(72, 32).addBox(1.0F, -4.5F, 0.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(64, 50).addBox(-8.0F, -4.5F, 0.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -3.5F, -3.6F, 0.1745F, 0.0F, 0.0F));
		head.addOrReplaceChild("Head_r5", CubeListBuilder.create().texOffs(0, 73).addBox(-1.0F, -5.5F, -1.0F, 4.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -3.5F, -4.1F, 0.0F, -0.1745F, 0.0F));
		head.addOrReplaceChild("Head_r6", CubeListBuilder.create().texOffs(24, 41).addBox(0.0F, 0.0F, 2.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -8.5F, 0.0F, 0.4363F, 0.0F, 0.0F));
		head.addOrReplaceChild("Head_r7", CubeListBuilder.create().texOffs(64, 62).addBox(-1.0F, 0.0F, -4.0F, 3.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -9.5F, -0.5F, 0.2618F, 0.0F, 0.0F));
		head.addOrReplaceChild("Head_r8", CubeListBuilder.create().texOffs(32, 17).addBox(-1.0F, -2.0F, -3.8F, 2.0F, 4.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.3F, 5.6F, -1.1345F, 0.0F, 0.0F));
		head.addOrReplaceChild("Head_r9", CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 4.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, 5.5F, -1.1345F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(32, 34).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(72, 44).addBox(-4.0F, 11.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.31F))
				.texOffs(24, 50).addBox(-4.0F, 0.0F, -2.5F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.01F))
				.texOffs(48, 72).addBox(-3.0F, 6.0F, -2.2F, 6.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		body.addOrReplaceChild("Body_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -24.0F, -2.0F, 12.0F, 21.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 23.5F, 6.5F, 0.2618F, 0.0F, 0.0F));
		body.addOrReplaceChild("Body_r2", CubeListBuilder.create().texOffs(0, 86).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(-3.0F, 11.5F, 0.0F, 0.0F, 0.0F, 0.0873F));
		body.addOrReplaceChild("Body_r3", CubeListBuilder.create().texOffs(62, 85).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(3.0F, 11.5F, 0.0F, 0.0F, 0.0F, -0.0873F));

		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(48, 50).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(78, 18).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.2F))
				.texOffs(82, 50).addBox(-3.0F, 6.5F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.2F))
				.texOffs(78, 26).addBox(-3.0F, 1.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.3F))
				.texOffs(84, 63).addBox(-3.0F, 6.5F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.3F))
				.texOffs(12, 90).addBox(-3.5F, 7.5F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(48, 50).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(78, 18).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.2F))
				.texOffs(82, 50).addBox(-1.0F, 6.5F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.2F))
				.texOffs(12, 90).addBox(2.5F, 7.5F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.4F))
				.texOffs(78, 26).addBox(-1.0F, 1.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.3F))
				.texOffs(84, 63).addBox(-1.0F, 6.5F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		left_arm.addOrReplaceChild("Left_Arm_r1", CubeListBuilder.create().texOffs(78, 5).addBox(-1.0F, 0.0F, -2.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, -1.5F, 0.0F, 0.0F, 0.0F, 0.2182F));
		left_arm.addOrReplaceChild("Left_Arm_r2", CubeListBuilder.create().texOffs(78, 0).addBox(-1.0F, 0.0F, -2.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(16, 60).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

		right_leg.addOrReplaceChild("boot", CubeListBuilder.create().texOffs(14, 85).addBox(-3.9F, -5.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.5F))
				.texOffs(30, 76).addBox(-3.9F, -5.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.3F))
				.texOffs(84, 68).addBox(-3.9F, -1.0F, -3.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.3F)), PartPose.offset(1.9F, 12.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 60).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(30, 76).addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.3F))
				.texOffs(84, 68).addBox(-2.0F, 11.0F, -3.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.3F))
				.texOffs(14, 85).addBox(-2.0F, 7.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(1.9F, 12.0F, 0.0F));

		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 96).addBox(-4.0F, -4.5F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setSlot(EquipmentSlot slot) {
		if (this.head != null) this.head.visible = false;
		if (this.body != null) this.body.visible = false;
		if (this.rightArm != null) this.rightArm.visible = false;
		if (this.leftArm != null) this.leftArm.visible = false;
		if (this.rightLeg != null) this.rightLeg.visible = false;
		if (this.leftLeg != null) this.leftLeg.visible = false;
		if (this.hat != null) this.hat.visible = false;

		switch (slot) {
			case HEAD -> {
				if (this.head != null) this.head.visible = true;
				if (this.hat != null) this.hat.visible = true;
			}
			case CHEST -> {
				if (this.body != null) this.body.visible = true;
				if (this.rightArm != null) this.rightArm.visible = true;
				if (this.leftArm != null) this.leftArm.visible = true;
			}
			case LEGS, FEET -> {
				if (this.rightLeg != null) this.rightLeg.visible = true;
				if (this.leftLeg != null) this.leftLeg.visible = true;
			}
			default -> {}
		}
	}
}