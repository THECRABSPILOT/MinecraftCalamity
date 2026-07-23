package crab.mods.minecraftcalamity.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class CustomArmorModel extends HumanoidModel<LivingEntity> {

	public CustomArmorModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		// 1. HEAD
		PartDefinition head = partdefinition.addOrReplaceChild("head",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
						.texOffs(32, 32).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.ZERO);

		// Required by HumanoidModel constructor
		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

		// 2. BODY
		PartDefinition body = partdefinition.addOrReplaceChild("body",
				CubeListBuilder.create()
						.texOffs(32, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
						.texOffs(32, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)),
				PartPose.ZERO);

		body.addOrReplaceChild("cube_r1",
				CubeListBuilder.create().texOffs(56, 10).addBox(-4.0F, -5.0F, 0.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.1F)),
				PartPose.offsetAndRotation(0.0F, 11.0F, -2.0F, 0.1309F, 0.0F, 0.0F));

		body.addOrReplaceChild("cube_r2",
				CubeListBuilder.create().texOffs(0, 59).addBox(-3.0F, -7.0F, 0.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.1F)),
				PartPose.offsetAndRotation(0.0F, 8.0F, -2.0F, 0.0873F, 0.0F, 0.0F));

		body.addOrReplaceChild("cube_r3",
				CubeListBuilder.create().texOffs(0, 49).addBox(-4.0F, -8.0F, 0.0F, 8.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 11.0F, -2.0F, 0.0873F, 0.0F, 0.0F));

		// 3. RIGHT ARM
		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm",
				CubeListBuilder.create()
						.texOffs(32, 47).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
						.texOffs(14, 60).addBox(-3.0F, 2.0F, -2.0F, 2.0F, 6.0F, 4.0F, new CubeDeformation(0.1F)),
				PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition ShoulderPad1 = right_arm.addOrReplaceChild("ShoulderPad1",
				CubeListBuilder.create().texOffs(38, 67).addBox(-2.0F, 2.0F, -3.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.6F)),
				PartPose.offsetAndRotation(-1.0F, -2.0F, 1.0F, 0.0F, 0.0F, 0.0873F));

		ShoulderPad1.addOrReplaceChild("cube_r4",
				CubeListBuilder.create().texOffs(54, 67).addBox(-3.0F, -1.0F, 0.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.5F)),
				PartPose.offsetAndRotation(1.0F, 1.0F, -3.0F, 0.0F, 0.0F, 1.4399F));

		ShoulderPad1.addOrReplaceChild("cube_r5",
				CubeListBuilder.create().texOffs(64, 48).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.2F)),
				PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

		ShoulderPad1.addOrReplaceChild("cube_r6",
				CubeListBuilder.create().texOffs(66, 36).addBox(-2.0F, -2.0F, -3.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.2F)),
				PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

		ShoulderPad1.addOrReplaceChild("cube_r7",
				CubeListBuilder.create().texOffs(64, 42).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.2F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.4399F));

		// 4. LEFT ARM
		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm",
				CubeListBuilder.create()
						.texOffs(48, 47).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
						.texOffs(26, 61).addBox(1.0F, 2.0F, -2.0F, 2.0F, 6.0F, 4.0F, new CubeDeformation(0.1F)),
				PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition ShoulderPad2 = left_arm.addOrReplaceChild("ShoulderPad2",
				CubeListBuilder.create().texOffs(56, 28).addBox(-2.0F, 2.0F, -3.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.6F)),
				PartPose.offsetAndRotation(1.0F, -2.0F, -1.0F, -3.1416F, 0.0F, 3.0107F));

		ShoulderPad2.addOrReplaceChild("cube_r8",
				CubeListBuilder.create().texOffs(66, 32).addBox(-3.0F, -1.0F, 0.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.5F)),
				PartPose.offsetAndRotation(1.0F, 1.0F, -3.0F, 0.0F, 0.0F, 1.4399F));

		ShoulderPad2.addOrReplaceChild("cube_r9",
				CubeListBuilder.create().texOffs(54, 61).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.2F)),
				PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

		ShoulderPad2.addOrReplaceChild("cube_r10",
				CubeListBuilder.create().texOffs(64, 54).addBox(-2.0F, -2.0F, -3.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.2F)),
				PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

		ShoulderPad2.addOrReplaceChild("cube_r11",
				CubeListBuilder.create().texOffs(38, 61).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.2F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.4399F));

		// 5. LEFT LEG
		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg",
				CubeListBuilder.create()
						.texOffs(0, 33).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
						.texOffs(18, 49).addBox(-2.0F, 4.0F, -2.0F, 4.0F, 8.0F, 3.0F, new CubeDeformation(0.1F))
						.texOffs(50, 32).addBox(-2.0F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.1F)),
				PartPose.offset(1.9F, 12.0F, 0.0F));

		left_leg.addOrReplaceChild("p2_r1",
				CubeListBuilder.create().texOffs(50, 42).addBox(0.0F, -1.0F, -1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.1F)),
				PartPose.offsetAndRotation(-0.9F, 12.0F, -3.0F, 0.0F, -0.7854F, 0.0F));

		// 6. RIGHT LEG
		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg",
				CubeListBuilder.create()
						.texOffs(16, 33).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
						.texOffs(56, 17).addBox(-2.2F, 4.0F, -2.0F, 4.0F, 8.0F, 3.0F, new CubeDeformation(0.1F))
						.texOffs(56, 0).addBox(-2.2F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.1F)),
				PartPose.offset(-1.9F, 12.0F, 0.0F));

		right_leg.addOrReplaceChild("p3_r1",
				CubeListBuilder.create().texOffs(0, 68).addBox(0.15F, -1.0F, -1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.1F, 12.0F, -3.0F, 0.0F, -0.7854F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
}