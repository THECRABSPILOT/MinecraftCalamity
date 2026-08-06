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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.ArrayList;
import java.util.List;

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

        BakedModel bottleModel = Minecraft.getInstance()
                .getModelManager()
                .getModel(BOTTLE_MODEL);

        poseStack.pushPose();

        // Bottle
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        Minecraft.getInstance().getItemRenderer()
                .render(stack, context, false, poseStack, buffer, light, overlay, bottleModel);
        poseStack.popPose();

        // Fluid
        int amount = LargeBottleItem.getFluidAmount(stack);
        if (amount > 0) {
            float fill = amount / (float) LargeBottleItem.CAPACITY;
            int color = getPotionColor(stack);

            poseStack.pushPose();
            renderFluid(poseStack, buffer, light, fill, color);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private int getPotionColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("Effects", 9)) {
            return 0x3F76E4; // water blue
        }

        ListTag effectsTag = tag.getList("Effects", 10);
        if (effectsTag.isEmpty()) {
            return 0x3F76E4;
        }

        List<MobEffectInstance> effects = new ArrayList<>();
        for (int i = 0; i < effectsTag.size(); i++) {
            MobEffectInstance effect = MobEffectInstance.load(effectsTag.getCompound(i));
            if (effect != null) {
                effects.add(effect);
            }
        }

        if (effects.isEmpty()) {
            return 0x3F76E4;
        }

        return PotionUtils.getColor(effects);
    }

    private void renderFluid(PoseStack poseStack, MultiBufferSource buffer,
                             int light, float fill, int color) {

        VertexConsumer vertex = buffer.getBuffer(RenderType.entityTranslucent(FLUID_TEXTURE));
        PoseStack.Pose pose = poseStack.last();

        float min = 2.0f;
        float max = 14.0f;
        float yMin = 0.5f;
        float yMax = 15.0f;
        float height = yMin + (yMax - yMin) * fill;

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8)  & 0xFF;
        int b =  color        & 0xFF;
        int a = 255;

        cube(vertex, pose, min, yMin, min, max, height, max, light, r, g, b, a);
    }

    private void cube(VertexConsumer vertex, PoseStack.Pose pose,
                      float x1, float y1, float z1,
                      float x2, float y2, float z2,
                      int light, int r, int g, int b, int a) {

        quad(vertex, pose, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2, 0, -1, 0, light, r, g, b, a);
        quad(vertex, pose, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1, 0,  1, 0, light, r, g, b, a);
        quad(vertex, pose, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, 0,  0,-1, light, r, g, b, a);
        quad(vertex, pose, x2, y1, z2, x2, y2, z2, x1, y2, z2, x1, y1, z2, 0,  0, 1, light, r, g, b, a);
        quad(vertex, pose, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1,-1,  0, 0, light, r, g, b, a);
        quad(vertex, pose, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, 1,  0, 0, light, r, g, b, a);
    }

    private void quad(VertexConsumer vertex, PoseStack.Pose pose,
                      float x1, float y1, float z1,
                      float x2, float y2, float z2,
                      float x3, float y3, float z3,
                      float x4, float y4, float z4,
                      float nx, float ny, float nz,
                      int light, int r, int g, int b, int a) {

        vertex(vertex, pose, x1, y1, z1, 0, 0, nx, ny, nz, light, r, g, b, a);
        vertex(vertex, pose, x2, y2, z2, 1, 0, nx, ny, nz, light, r, g, b, a);
        vertex(vertex, pose, x3, y3, z3, 1, 1, nx, ny, nz, light, r, g, b, a);
        vertex(vertex, pose, x4, y4, z4, 0, 1, nx, ny, nz, light, r, g, b, a);
    }

    private void vertex(VertexConsumer vertex, PoseStack.Pose pose,
                        float x, float y, float z,
                        float u, float v,
                        float nx, float ny, float nz,
                        int light, int r, int g, int b, int a) {

        vertex.vertex(pose.pose(), x / 16f, y / 16f, z / 16f)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }
}