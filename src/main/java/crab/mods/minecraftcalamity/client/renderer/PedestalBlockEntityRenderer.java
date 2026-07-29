package crab.mods.minecraftcalamity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import crab.mods.minecraftcalamity.blocks.entity.PedestalBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PedestalBlockEntityRenderer implements BlockEntityRenderer<PedestalBlockEntity> {
    private final ItemRenderer itemRenderer;

    public PedestalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(PedestalBlockEntity pBlockEntity, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.getLevel() == null) return;

        ItemStack stack = pBlockEntity.getDisplayedItem();
        if (stack.isEmpty()) {
            return;
        }

        pPoseStack.pushPose();

        pPoseStack.translate(0.5D, 1.25D, 0.5D);

        long time = pBlockEntity.getLevel().getGameTime();
        float bobbing = (float) Math.sin((time + pPartialTick) / 12.0) * 0.04F;
        pPoseStack.translate(0.0D, bobbing, 0.0D);

        float rotation = (time + pPartialTick) * 2.5F;
        pPoseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        pPoseStack.scale(0.6F, 0.6F, 0.6F);

        this.itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.GROUND,
                pPackedLight,
                pPackedOverlay,
                pPoseStack,
                pBuffer,
                pBlockEntity.getLevel(),
                (int) pBlockEntity.getBlockPos().asLong()
        );

        pPoseStack.popPose();
    }
}