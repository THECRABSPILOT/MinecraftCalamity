package crab.mods.minecraftcalamity.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity; // FIXED: Correct Minecraft Entity import

public class CaveWizardModel<T extends Entity> extends EntityModel<T> {
	// Remember to change "modid" to your actual registered Mod ID
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("minecraftcalamity", "cavewizard"), "main");

	private final ModelPart Waist;
	private final ModelPart Head;
	private final ModelPart Body;
	private final ModelPart Right_Arm;
	private final ModelPart Left_Arm;
	private final ModelPart Right_Leg;
	private final ModelPart Left_Leg;

	public CaveWizardModel(ModelPart root) {
		this.Waist = root.getChild("Waist");
		this.Head = this.Waist.getChild("Head");
		this.Body = this.Waist.getChild("Body");
		this.Right_Arm = this.Waist.getChild("Right Arm");
		this.Left_Arm = this.Waist.getChild("Left Arm");
		this.Right_Leg = this.Waist.getChild("Right Leg");
		this.Left_Leg = this.Waist.getChild("Left Leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Waist = partdefinition.addOrReplaceChild("Waist", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition Head = Waist.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 36).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(42, 20).addBox(-4.0F, -2.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		// FIXED: Removed duplicate type styling from Blockbench naming bugs
		PartDefinition Head_r1 = Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(58, 49).addBox(-2.0F, -3.0F, -2.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 4.0F, -0.9163F, 0.0F, 0.0F));

		PartDefinition Head_r2 = Head.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(32, 36).addBox(-4.0F, -3.0F, -2.0F, 9.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.0F, 1.0F, -0.6109F, 0.0F, 0.0F));

		PartDefinition Head_r3 = Head.addOrReplaceChild("Head_r3", CubeListBuilder.create().texOffs(0, 20).addBox(-5.0F, -5.0F, -4.0F, 11.0F, 6.0F, 10.0F, new CubeDeformation(0.1F))
				.texOffs(0, 0).addBox(-9.0F, -1.0F, -8.0F, 18.0F, 2.0F, 18.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, -5.0F, -1.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition Body = Waist.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(32, 49).addBox(-4.0F, 0.0F, 1.0F, 8.0F, 12.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(74, 71).addBox(-3.0F, 6.0F, 6.0F, 6.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 52).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 13.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition Right_Arm = Waist.addOrReplaceChild("Right Arm", CubeListBuilder.create().texOffs(58, 60).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 69).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, -10.0F, 0.0F));

		PartDefinition Left_Arm = Waist.addOrReplaceChild("Left Arm", CubeListBuilder.create().texOffs(24, 66).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(72, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(5.0F, -10.0F, 0.0F));

		PartDefinition Right_Leg = Waist.addOrReplaceChild("Right Leg", CubeListBuilder.create().texOffs(66, 30).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(74, 14).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-1.9F, 0.0F, 0.0F));

		// FIXED: Cleaned up variable name type mismatch
		PartDefinition Right_Leg_Layer_r1 = Right_Leg.addOrReplaceChild("Right Leg Layer_r1", CubeListBuilder.create().texOffs(54, 30).addBox(-0.9F, -1.0F, -1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1F, 12.0F, -3.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition Left_Leg = Waist.addOrReplaceChild("Left Leg", CubeListBuilder.create().texOffs(40, 66).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(74, 60).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(1.9F, 0.0F, 0.0F));

		// FIXED: Cleaned up variable name type mismatch
		PartDefinition Left_Leg_Layer_r1 = Left_Leg.addOrReplaceChild("Left Leg Layer_r1", CubeListBuilder.create().texOffs(42, 30).addBox(-1.1F, -1.0F, -1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 12.0F, -3.0F, 0.0F, -0.7854F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		// Use this space to apply animations like leg/arm swing mechanics
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Waist.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
