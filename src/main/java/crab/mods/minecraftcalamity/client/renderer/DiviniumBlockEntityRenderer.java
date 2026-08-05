package crab.mods.minecraftcalamity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.blocks.entity.DiviniumBlockEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class DiviniumBlockEntityRenderer implements BlockEntityRenderer<DiviniumBlockEntity> {

    public static final ResourceLocation DIVINIUM_TEXTURE =
            new ResourceLocation(MinecraftCalamity.MODID, "textures/block/divinium_sky.png");

    public DiviniumBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(DiviniumBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        BlockPos pos = blockEntity.getBlockPos();

        long time = Minecraft.getInstance().level.getGameTime();
        float timeOffset = (time + partialTick) * 0.00015f;

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.text(DIVINIUM_TEXTURE));

        float[][] scrollDirections = {
                {  1.0f,  0.3f },
                { -0.5f,  0.8f },
                {  0.2f, -0.6f }
        };

        Matrix4f poseMatrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();

        for (int layer = 0; layer < 3; layer++) {
            float dirX = scrollDirections[layer][0];
            float dirY = scrollDirections[layer][1];
            float layerTime = timeOffset * (1.0f + layer * 0.25f);

            float scale = 0.08f / (layer + 1);
            float inset = layer * 0.001f;
            int alpha = layer == 0 ? 255 : 110;

            renderMaskedCube(consumer, poseMatrix, normalMatrix, pos, camera, scale, dirX * layerTime, dirY * layerTime, alpha, inset);
        }
    }

    private void renderMaskedCube(VertexConsumer consumer, Matrix4f poseMatrix, Matrix3f normalMatrix,
                                  BlockPos pos, Camera camera, float scale, float scrollX, float scrollY, int alpha, float inset) {

        float min = 0.0f + inset;
        float max = 1.0f - inset;

        Vec3 camPos = camera.getPosition();
        double relX = pos.getX() - camPos.x;
        double relY = pos.getY() - camPos.y;
        double relZ = pos.getZ() - camPos.z;

        // TOP / BOTTOM: Map X and Z to UV space
        renderAxisQuad(consumer, poseMatrix, normalMatrix, relX, relY, relZ, min, max, max,  max, max, max,  max, max, min,  min, max, min, 0, 1, 0, scale, scrollX, scrollY, alpha, 0);
        renderAxisQuad(consumer, poseMatrix, normalMatrix, relX, relY, relZ, min, min, min,  max, min, min,  max, min, max,  min, min, max, 0, -1, 0, scale, scrollX, scrollY, alpha, 0);

        // NORTH / SOUTH: Map X and Y to UV space
        renderAxisQuad(consumer, poseMatrix, normalMatrix, relX, relY, relZ, min, max, min,  max, max, min,  max, min, min,  min, min, min, 0, 0, -1, scale, scrollX, scrollY, alpha, 1);
        renderAxisQuad(consumer, poseMatrix, normalMatrix, relX, relY, relZ, min, min, max,  max, min, max,  max, max, max,  min, max, max, 0, 0, 1, scale, scrollX, scrollY, alpha, 1);

        // WEST / EAST: Map Z and Y to UV space
        renderAxisQuad(consumer, poseMatrix, normalMatrix, relX, relY, relZ, min, min, min,  min, min, max,  min, max, max,  min, max, min, -1, 0, 0, scale, scrollX, scrollY, alpha, 2);
        renderAxisQuad(consumer, poseMatrix, normalMatrix, relX, relY, relZ, max, max, min,  max, max, max,  max, min, max,  max, min, min, 1, 0, 0, scale, scrollX, scrollY, alpha, 2);
    }

    private void renderAxisQuad(VertexConsumer consumer, Matrix4f poseMatrix, Matrix3f normalMatrix,
                                double relX, double relY, double relZ,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float x4, float y4, float z4,
                                float nx, float ny, float nz,
                                float scale, float scrollX, float scrollY, int alpha, int planeAxis) {

        float u1, v1, u2, v2, u3, v3, u4, v4;

        if (planeAxis == 0) { // Top/Bottom (XZ projection)
            u1 = (float) ((relX + x1) * scale + scrollX); v1 = (float) ((relZ + z1) * scale + scrollY);
            u2 = (float) ((relX + x2) * scale + scrollX); v2 = (float) ((relZ + z2) * scale + scrollY);
            u3 = (float) ((relX + x3) * scale + scrollX); v3 = (float) ((relZ + z3) * scale + scrollY);
            u4 = (float) ((relX + x4) * scale + scrollX); v4 = (float) ((relZ + z4) * scale + scrollY);
        } else if (planeAxis == 1) { // North/South (XY projection)
            u1 = (float) ((relX + x1) * scale + scrollX); v1 = (float) ((relY + y1) * scale + scrollY);
            u2 = (float) ((relX + x2) * scale + scrollX); v2 = (float) ((relY + y2) * scale + scrollY);
            u3 = (float) ((relX + x3) * scale + scrollX); v3 = (float) ((relY + y3) * scale + scrollY);
            u4 = (float) ((relX + x4) * scale + scrollX); v4 = (float) ((relY + y4) * scale + scrollY);
        } else { // East/West (ZY projection)
            u1 = (float) ((relZ + z1) * scale + scrollX); v1 = (float) ((relY + y1) * scale + scrollY);
            u2 = (float) ((relZ + z2) * scale + scrollX); v2 = (float) ((relY + y2) * scale + scrollY);
            u3 = (float) ((relZ + z3) * scale + scrollX); v3 = (float) ((relY + y3) * scale + scrollY);
            u4 = (float) ((relZ + z4) * scale + scrollX); v4 = (float) ((relY + y4) * scale + scrollY);
        }

        addVertex(consumer, poseMatrix, normalMatrix, x1, y1, z1, u1, v1, nx, ny, nz, alpha);
        addVertex(consumer, poseMatrix, normalMatrix, x2, y2, z2, u2, v2, nx, ny, nz, alpha);
        addVertex(consumer, poseMatrix, normalMatrix, x3, y3, z3, u3, v3, nx, ny, nz, alpha);
        addVertex(consumer, poseMatrix, normalMatrix, x4, y4, z4, u4, v4, nx, ny, nz, alpha);
    }

    private void addVertex(VertexConsumer consumer, Matrix4f poseMatrix, Matrix3f normalMatrix,
                           float x, float y, float z, float u, float v,
                           float nx, float ny, float nz, int alpha) {

        consumer.vertex(poseMatrix, x, y, z)
                .color(255, 255, 255, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(0x00F000F0)
                .normal(normalMatrix, nx, ny, nz)
                .endVertex();
    }
}