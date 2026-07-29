package crab.mods.minecraftcalamity.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.entity.custom.DynamicProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class DynamicProjectileRenderer extends EntityRenderer<DynamicProjectileEntity> {
    private final DynamicProjectileModel<DynamicProjectileEntity> model;

    public DynamicProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new DynamicProjectileModel<>(context.bakeLayer(DynamicProjectileModel.LAYER_LOCATION));
    }

    @Override
    public void render(DynamicProjectileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);

        poseStack.pushPose();

        // Moved up by an additional 3 blocks (1.2D + 3.0D = 4.2D)
        poseStack.translate(0.0D, 2.1D, 0.0D);

        // Scale kept at 5x
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
    public ResourceLocation getTextureLocation(DynamicProjectileEntity entity) {
        return new ResourceLocation(MinecraftCalamity.MODID, "textures/entity/projectilebase.png");
    }
}