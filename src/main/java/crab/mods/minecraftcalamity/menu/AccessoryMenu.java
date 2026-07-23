package crab.mods.minecraftcalamity.menu;

import crab.mods.minecraftcalamity.accessory.AccessoryCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class AccessoryMenu extends AbstractContainerMenu {

    // Constructor for Network Syncing (Client-side)
    public AccessoryMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory);
    }

    // Main Constructor (Server & Client)
    public AccessoryMenu(int containerId, Inventory playerInventory) {
        // FIX: Pass ModMenuTypes.ACCESSORY_MENU.get() instead of null
        super(ModMenuTypes.ACCESSORY_MENU.get(), containerId);

        Player player = playerInventory.player;
        player.getCapability(AccessoryCapability.ACCESSORY_CAP).ifPresent(cap -> {
            // Add custom accessory slots
            for (int i = 0; i < cap.getInventory().getSlots(); i++) {
                this.addSlot(new SlotItemHandler(cap.getInventory(), i, 8 + (i * 18), 8));
            }
        });

        // Player Inventory slots
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}