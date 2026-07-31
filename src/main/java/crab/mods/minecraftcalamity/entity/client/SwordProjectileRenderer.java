package crab.mods.minecraftcalamity.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.entity.custom.SwordProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SwordProjectileRenderer extends EntityRenderer<SwordProjectileEntity> {
    private final SwordModel<SwordProjectileEntity> model;

    public SwordProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SwordModel<>(context.bakeLayer(SwordModel.LAYER_LOCATION));
    }

    @Override
    public void render(SwordProjectileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);

        poseStack.pushPose();

        // Position adjustments
        poseStack.translate(0.0D, 2.1D, 0.0D);

        // Dynamically calculate rotation from actual movement velocity, supporting straight up/down
        Vec3 movement = entity.getDeltaMovement();
        if (movement.lengthSqr() > 1.0E-7) {
            float xRot = (float)(Mth.atan2(movement.horizontalDistance(), movement.y) * (double)(180F / (float)Math.PI)) - 90.0F;
            float yRot = (float)(Mth.atan2(movement.z, movement.x) * (double)(180F / (float)Math.PI)) + 90.0F;

            poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
            poseStack.mulPose(Axis.XP.rotationDegrees(-xRot));
        }

        // Flipped 180 degrees so the tip faces away from the player
        poseStack.mulPose(Axis.YP.rotationDegrees(360.0F));

        // Scale kept at your settings
        poseStack.scale(-2.0F, -2.0F, 2.0F);

        int color = entity.getProjectileColor();
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;

        ResourceLocation textureLocation = this.getTextureLocation(entity);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(textureLocation));

        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(SwordProjectileEntity entity) {
        return new ResourceLocation(MinecraftCalamity.MODID, "textures/entity/sword.png");
    }
}