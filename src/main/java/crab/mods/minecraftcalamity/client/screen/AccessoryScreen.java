package crab.mods.minecraftcalamity.client.screen;

import crab.mods.minecraftcalamity.menu.AccessoryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AccessoryScreen extends AbstractContainerScreen<AccessoryMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("minecraftcalamity", "textures/gui/container/accinv.png");

    public AccessoryScreen(AccessoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // FIX: Pass explicit textureWidth and textureHeight (256, 256) at the end
        guiGraphics.blit(
                TEXTURE,
                x, y,                   // On-screen position
                0, 0,                   // Texture UV offset (top-left)
                this.imageWidth,        // Width to render on screen (e.g., 176)
                this.imageHeight,       // Height to render on screen (e.g., 222)
                176, 166                // FULL texture file size
        );

        // Render 3D player model
        if (this.minecraft != null && this.minecraft.player != null) {
            int modelX = x + 140;   // Positioned over the right side box
            int modelY = y + 75;
            int scale = 30;

            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    guiGraphics,
                    modelX,
                    modelY,
                    scale,
                    (float)(modelX) - mouseX,
                    (float)(modelY - 50) - mouseY,
                    this.minecraft.player
            );
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}