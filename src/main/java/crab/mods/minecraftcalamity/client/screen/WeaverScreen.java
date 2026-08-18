package crab.mods.minecraftcalamity.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import crab.mods.minecraftcalamity.menu.WeaverMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class WeaverScreen extends AbstractContainerScreen<WeaverMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("minecraftcalamity", "textures/gui/spellweaverbook.png");

    private int bookX;
    private int bookY;

    public WeaverScreen(WeaverMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        this.imageWidth  = 176;
        this.imageHeight = 166;

        super.init();


        this.leftPos = 10;
        this.topPos  = this.height - 162;


        this.bookX = this.width - 304 - 10;
        this.bookY = 10;

        this.titleLabelX     = -1000;
        this.inventoryLabelX = -1000;
    }

    private boolean isWeaverSlot(Slot slot) {
        return this.menu.slots.indexOf(slot) < 4;
    }

    private int getWeaverSlotAbsX(Slot slot) {
        return this.bookX + slot.x;
    }

    private int getWeaverSlotAbsY(Slot slot) {
        return this.bookY + slot.y;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        //book
        guiGraphics.blit(TEXTURE, this.bookX, this.bookY, 0, 0, 304, 180, 323, 256);

        //blocks
        int x = this.bookX + 165;
        int y = this.bookY + 15;
        int width = 40;
        int height = 10;
        int color = 0xFFFF8000;
        int outlinesize = 2;
        int txtColor = 0xFFFFFFFF;
        DrawBlock2(guiGraphics, this.bookX + 5, this.bookY  + 5, 135, 170, 0xFF0000, 0, " ", txtColor);

        DrawBlock(guiGraphics, x, y, width, height, color, outlinesize, "On Cast", txtColor);



        //guiGraphics.fill(x - 45, y - 45, x + 100, y + 200, 0xFF0000);
        //fug it, we ball

        //inventory
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        for (int i = 4; i < this.menu.slots.size(); i++) {
            Slot slot = this.menu.slots.get(i);
            int screenX = this.leftPos + slot.x;
            int screenY = this.topPos  + slot.y;

            guiGraphics.blit(InventoryScreen.INVENTORY_LOCATION,
                    screenX - 1, screenY - 1, 7, 83, 18, 18);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);


        super.render(guiGraphics, mouseX, mouseY, delta);


        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public void DrawBlock(GuiGraphics guiGraphics, int x, int y, int width, int height, int color, int outline, String text, int textColor) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        int darkerColor = darkenColor(color, 0.5f);


        guiGraphics.fill(x - outline, y - outline, x + width + outline, y + height + outline, darkerColor);


        guiGraphics.fill(x, y, x + width, y + height, color);


        int centerX = x + (width / 2);
        int centerY = y + (height / 2);
        int textX = centerX - (this.font.width(text) / 2);
        int textY = centerY - (9 / 2);


        guiGraphics.drawString(this.font, text, textX, textY, textColor, true);
    }

    public void DrawBlock2(GuiGraphics guiGraphics, int x, int y, int width, int height, int color, int outline, String text, int textColor) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        int darkerColor = darkenColor(color, 0.5f);


        guiGraphics.fill(x - outline, y - outline, x + width + outline, y + height + outline, darkerColor);


        guiGraphics.fill(x, y, x + width, y + height, color);


        int centerX = x + (width / 2);
        int centerY = y + (height / 2);
        int textX = centerX - (this.font.width(text) / 2);
        int textY = centerY - (9 / 2);


        guiGraphics.drawString(this.font, text, textX, textY, textColor, true);
    }

    private int darkenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        if (a == 0) a = 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 0; i < 4; i++) {
            Slot slot = this.menu.slots.get(i);
            int absX = getWeaverSlotAbsX(slot);
            int absY = getWeaverSlotAbsY(slot);

            if (mouseX >= absX && mouseY >= absY && mouseX < absX + 16 && mouseY < absY + 16) {
                this.hoveredSlot = slot;
                return super.mouseClicked(mouseX, mouseY, button);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
