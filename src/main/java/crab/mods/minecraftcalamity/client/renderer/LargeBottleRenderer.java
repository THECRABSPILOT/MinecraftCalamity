package crab.mods.minecraftcalamity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import crab.mods.minecraftcalamity.items.LargeBottleItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LargeBottleRenderer extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation FLUID_TEXTURE =
            new ResourceLocation("minecraftcalamity", "textures/item/fluid.png");

    private static final ResourceLocation BOTTLE_MODEL =
            new ResourceLocation("minecraftcalamity", "item/large_bottle_stati");

    public LargeBottleRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context,
                             PoseStack poseStack, MultiBufferSource buffer,
                             int light, int overlay) {

        // Always re-fetch – BakedModels become invalid after resource reload (F3+T)
        BakedModel bottleModel = Minecraft.getInstance()
                .getModelManager()
                .getModel(BOTTLE_MODEL);

        poseStack.pushPose();

        // Render the static glass bottle
        Minecraft.getInstance().getItemRenderer()
                .render(stack, context, false, poseStack, buffer, light, overlay, bottleModel);

        int amount = LargeBottleItem.getFluidAmount(stack);
        if (amount > 0) {
            float fill = amount / (float) LargeBottleItem.CAPACITY;
            poseStack.pushPose();
            renderFluid(poseStack, buffer, light, fill);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private void renderFluid(PoseStack poseStack, MultiBufferSource buffer, int light, float fill) {
        // Use a proper translucent entity render type, not RenderType.text
        VertexConsumer vertex = buffer.getBuffer(RenderType.entityTranslucent(FLUID_TEXTURE));
        PoseStack.Pose pose = poseStack.last();

        float min = 2.0f;
        float max = 14.0f;
        float yMin = 0.5f;
        float yMax = 15.0f;

        float height = yMin + (yMax - yMin) * fill;

        cube(vertex, pose, min, yMin, min, max, height, max, light);
    }

    private void cube(VertexConsumer vertex, PoseStack.Pose pose,
                      float x1, float y1, float z1,
                      float x2, float y2, float z2, int light) {
        // bottom
        quad(vertex, pose, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2, 0, -1, 0, light);
        // top
        quad(vertex, pose, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1, 0, 1, 0, light);
        // north
        quad(vertex, pose, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, 0, 0, -1, light);
        // south
        quad(vertex, pose, x2, y1, z2, x2, y2, z2, x1, y2, z2, x1, y1, z2, 0, 0, 1, light);
        // west
        quad(vertex, pose, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1, -1, 0, 0, light);
        // east
        quad(vertex, pose, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, 1, 0, 0, light);
    }

    private void quad(VertexConsumer vertex, PoseStack.Pose pose,
                      float x1, float y1, float z1,
                      float x2, float y2, float z2,
                      float x3, float y3, float z3,
                      float x4, float y4, float z4,
                      float nx, float ny, float nz, int light) {
        vertex(vertex, pose, x1, y1, z1, 0, 0, nx, ny, nz, light);
        vertex(vertex, pose, x2, y2, z2, 1, 0, nx, ny, nz, light);
        vertex(vertex, pose, x3, y3, z3, 1, 1, nx, ny, nz, light);
        vertex(vertex, pose, x4, y4, z4, 0, 1, nx, ny, nz, light);
    }

    private void vertex(VertexConsumer vertex, PoseStack.Pose pose,
                        float x, float y, float z,
                        float u, float v,
                        float nx, float ny, float nz, int light) {
        vertex.vertex(pose.pose(), x / 16f, y / 16f, z / 16f)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)          // was hardcoded fullbright – now uses the real light
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }
}