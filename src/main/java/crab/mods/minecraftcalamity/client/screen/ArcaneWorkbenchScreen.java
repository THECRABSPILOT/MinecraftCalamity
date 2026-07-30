package crab.mods.minecraftcalamity.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.menu.ArcaneWorkbenchMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ArcaneWorkbenchScreen extends AbstractContainerScreen<ArcaneWorkbenchMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MinecraftCalamity.MODID, "textures/gui/container/arcaneworkbench.png");

    public ArcaneWorkbenchScreen(ArcaneWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
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

        // 1. Draw main GUI window background
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // 2. Query active slot count directly from Block Entity (NBT check)
        int activeSlots = this.menu.blockEntity.getAvailableSpellSlots();

        // 3. Render slot background boxes dynamically from the spritesheet
        int modGridX = x + 57; // Align with SlotItemHandler (58 - 1 border offset)
        int modGridY = y + 21; // Align with SlotItemHandler (22 - 1 border offset)

        // UV coordinates for the slot box on your spritesheet (Adjust U/V to match your texture)
        int slotTextureU = 176;
        int slotTextureV = 0;

        for (int i = 0; i < activeSlots; i++) {
            int row = i / 3;
            int col = i % 3;
            int slotX = modGridX + (col * 18);
            int slotY = modGridY + (row * 18);

            // Draw slot background box
            guiGraphics.blit(TEXTURE, slotX, slotY, slotTextureU, slotTextureV, 18, 18);
        }

        // 4. Render assigned spell item icons inside the modifier slots if present in the staff's NBT
        ItemStack staffStack = this.menu.getSlot(0).getItem();
        if (!staffStack.isEmpty() && staffStack.hasTag() && staffStack.getTag().contains("Spells")) {
            CompoundTag spellsTag = staffStack.getTag().getCompound("Spells");

            for (int i = 0; i < activeSlots; i++) {
                String spellId = spellsTag.getString("Slot_" + i);
                if (!spellId.isEmpty() && !spellId.equals("Empty")) {
                    Item spellItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraftcalamity", spellId));
                    if (spellItem != null) {
                        int row = i / 3;
                        int col = i % 3;
                        int slotX = modGridX + (col * 18) + 1; // +1 to center inside 18x18 slot box
                        int slotY = modGridY + (row * 18) + 1;

                        guiGraphics.renderItem(new ItemStack(spellItem), slotX, slotY);
                    }
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}