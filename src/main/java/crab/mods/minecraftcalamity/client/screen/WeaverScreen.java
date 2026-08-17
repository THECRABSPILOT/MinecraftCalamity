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
import net.minecraft.world.item.ItemStack;

public class WeaverScreen extends AbstractContainerScreen<WeaverMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("minecraftcalamity", "textures/gui/spellweaverbook.png");

    // Absolute position of the book
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

        // ===== Player inventory (bottom-left) =====
        // Move it higher by using a larger subtraction value
        this.leftPos = 10;
        this.topPos  = this.height - 162;   // ← adjust this number (try 100 ~ 120)

        // ===== Book (top-right) =====
        this.bookX = this.width - 304 - 10;
        this.bookY = 10;

        // Hide vanilla labels
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

        // 1. Book (top-right)
        guiGraphics.blit(TEXTURE, this.bookX, this.bookY, 0, 0, 304, 180, 323, 256);

        // 2. Draw player inventory slot frames (bottom-left)
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
        this.renderBg(guiGraphics, delta, mouseX, mouseY);

        // Manually render the 4 weaver/book slots
        for (int i = 0; i < 4; i++) {
            Slot slot = this.menu.slots.get(i);
            if (!slot.isActive()) continue;

            int absX = getWeaverSlotAbsX(slot);
            int absY = getWeaverSlotAbsY(slot);

            ItemStack stack = slot.getItem();
            if (!stack.isEmpty()) {
                guiGraphics.renderItem(stack, absX, absY);
                guiGraphics.renderItemDecorations(this.font, stack, absX, absY);
            }

            // Hover highlight
            if (mouseX >= absX && mouseY >= absY && mouseX < absX + 16 && mouseY < absY + 16) {
                guiGraphics.fillGradient(absX, absY, absX + 16, absY + 16, 0x80FFFFFF, 0x80FFFFFF);
            }
        }

        // Let superclass render the normal player inventory items
        super.render(guiGraphics, mouseX, mouseY, delta);

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Make weaver slots clickable
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