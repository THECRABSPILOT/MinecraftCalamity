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

public class CalamititeArmorModel extends HumanoidModel<LivingEntity> {
	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(new ResourceLocation(MinecraftCalamity.MODID, "calamitite"), "main");

	private EquipmentSlot slot = EquipmentSlot.CHEST;

	// Direct references to the boot child parts so we can toggle their visibility
	private final ModelPart rightBoot;
	private final ModelPart leftBoot;

	public CalamititeArmorModel(ModelPart root) {
		super(root); // Binds head, hat, body, rightArm, leftArm, rightLeg, leftLeg

		// Grab boot children from the leg parts
		this.rightBoot = this.rightLeg.getChild("right_boot");
		this.leftBoot = this.leftLeg.getChild("left_boot");
	}

	public void setSlot(EquipmentSlot slot) {
		this.slot = slot;
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		// 1. Let HumanoidModel calculate walking, head looking, and crouching angles
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

		// 2. Adjust root head/body positions for crouching/riding if needed
		// (HumanoidModel updates head, body, rightArm, leftArm, rightLeg, leftLeg automatically)

		// 3. Keep hat outer layer hidden
		this.hat.visible = false;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		// 1. Required Hat layer (empty, prevents missing part crash)
		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

		// 2. Head
		PartDefinition head = partdefinition.addOrReplaceChild("head",
				CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.2F))
						.texOffs(32, 0).addBox(0.0F, -13.0F, -1.0F, 0.0F, 13.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.ZERO);

		head.addOrReplaceChild("visor_r1",
				CubeListBuilder.create().texOffs(0, 34).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.2F)),
				PartPose.offsetAndRotation(0.0F, -1.0F, -4.0F, 0.0F, -0.7854F, 0.0F));

		// 3. Body
		PartDefinition body = partdefinition.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(32, 23).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.2F))
						.texOffs(52, 14).addBox(-4.0F, 10.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.4F)),
				PartPose.ZERO);

		body.addOrReplaceChild("hippad2_r1",
				CubeListBuilder.create().texOffs(0, 63).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 6.0F, 4.0F, new CubeDeformation(0.3F)),
				PartPose.offsetAndRotation(3.0F, 14.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

		body.addOrReplaceChild("hippad1_r1",
				CubeListBuilder.create().texOffs(56, 45).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 6.0F, 4.0F, new CubeDeformation(0.3F)),
				PartPose.offsetAndRotation(-3.0F, 14.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

		body.addOrReplaceChild("chestplting2_r1",
				CubeListBuilder.create().texOffs(56, 36).addBox(-3.0F, -7.0F, 0.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.2F)),
				PartPose.offsetAndRotation(0.0F, 8.0F, -2.0F, 0.0873F, 0.0F, 0.0F));

		body.addOrReplaceChild("chestplating1_r1",
				CubeListBuilder.create().texOffs(16, 55).addBox(-4.0F, -7.0F, 0.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.2F)),
				PartPose.offsetAndRotation(0.0F, 11.0F, -2.0F, 0.0873F, 0.0F, 0.0F));

		// 4. Right Arm
		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm",
				CubeListBuilder.create().texOffs(0, 49).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.2F)),
				PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition rightarmstuff = right_arm.addOrReplaceChild("rightarmstuff",
				CubeListBuilder.create().texOffs(66, 66).addBox(7.0F, -15.0F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.3F))
						.texOffs(50, 55).addBox(7.0F, -25.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.3F))
						.texOffs(111, 62).addBox(7.0F, -21.0F, -2.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.4F)),
				PartPose.offset(-10.0F, 22.0F, 0.0F));

		rightarmstuff.addOrReplaceChild("left_arm_r1",
				CubeListBuilder.create().texOffs(111, 58).addBox(0.0F, 0.0F, -2.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.4F)),
				PartPose.offsetAndRotation(11.0F, -21.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

		// 5. Left Arm
		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm",
				CubeListBuilder.create().texOffs(52, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.2F)),
				PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition leftarmstuff = left_arm.addOrReplaceChild("leftarmstuff",
				CubeListBuilder.create().texOffs(113, 64).addBox(0.0F, 0.0F, -2.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.4F))
						.texOffs(34, 55).addBox(0.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.3F))
						.texOffs(66, 59).addBox(3.0F, 6.0F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.3F)),
				PartPose.offset(-1.0F, 1.0F, 0.0F));

		leftarmstuff.addOrReplaceChild("left_arm_r2",
				CubeListBuilder.create().texOffs(109, 62).addBox(0.0F, 0.0F, -2.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.4F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

		// 6. Right Leg
		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg",
				CubeListBuilder.create().texOffs(24, 39).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)),
				PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition right_boot = right_leg.addOrReplaceChild("right_boot",
				CubeListBuilder.create().texOffs(38, 67).addBox(-3.9F, -6.0F, -2.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.3F))
						.texOffs(56, 28).addBox(-3.9F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.3F)),
				PartPose.offset(1.9F, 12.0F, 0.0F));

		right_boot.addOrReplaceChild("right_legs_r1",
				CubeListBuilder.create().texOffs(68, 0).addBox(-2.9F, -1.0F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.2F)),
				PartPose.offsetAndRotation(-1.0F, 0.0F, -1.0F, 0.0F, -0.7854F, 0.0F));

		// 7. Left Leg
		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg",
				CubeListBuilder.create().texOffs(40, 39).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)),
				PartPose.offset(1.9F, 12.0F, 0.0F));

		PartDefinition left_boot = left_leg.addOrReplaceChild("left_boot",
				CubeListBuilder.create().texOffs(28, 67).addBox(-0.1F, -6.0F, -2.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.3F))
						.texOffs(56, 20).addBox(-0.1F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.3F)),
				PartPose.offset(-1.9F, 12.0F, 0.0F));

		left_boot.addOrReplaceChild("left_leg_r1",
				CubeListBuilder.create().texOffs(48, 67).addBox(-3.0F, -1.0F, -0.8F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.2F)),
				PartPose.offsetAndRotation(4.0F, 0.0F, -1.0F, 0.0F, -0.7854F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (this.slot == EquipmentSlot.HEAD) {
			this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		}
		else if (this.slot == EquipmentSlot.CHEST) {
			this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
			this.rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
			this.leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		}
		else if (this.slot == EquipmentSlot.LEGS) {
			// Hide boots while rendering leggings
			this.rightBoot.visible = false;
			this.leftBoot.visible = false;

			this.rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
			this.leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		}
		else if (this.slot == EquipmentSlot.FEET) {
			// Make boots visible, but render ONLY the boot parts (hides leggings)
			this.rightBoot.visible = true;
			this.leftBoot.visible = true;

			// Render boots directly using their parent transforms
			poseStack.pushPose();
			this.rightLeg.translateAndRotate(poseStack);
			this.rightBoot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
			poseStack.popPose();

			poseStack.pushPose();
			this.leftLeg.translateAndRotate(poseStack);
			this.leftBoot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
			poseStack.popPose();
		}
	}
}