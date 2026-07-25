package crab.mods.minecraftcalamity.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.menu.HellforgeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class HellforgeScreen extends AbstractContainerScreen<HellforgeMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MinecraftCalamity.MODID, "textures/gui/container/hellforge.png");

    public HellforgeScreen(HellforgeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Render Flame (Moved down 2, right 2 -> x + 46, y + 38)
        if (menu.isLit()) {
            int litHeight = menu.getScaledLit();
            guiGraphics.blit(TEXTURE, x + 46, y + 38 + 12 - litHeight, 176, 12 - litHeight, 14, litHeight);
        }

// Render Progress Arrow (Moved down 1, left 1 -> x + 79, y + 35)
        if (menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x + 79, y + 35, 176, 14, menu.getScaledProgress(), 16);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}