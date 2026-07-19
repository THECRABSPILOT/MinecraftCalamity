package crab.mods.minecraftcalamity;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CalamityCraftingMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final Container inputContainer = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            CalamityCraftingMenu.this.slotsChanged(this);
        }
    };
    private final ResultContainer resultContainer = new ResultContainer();

    // Client-side initialization
    public CalamityCraftingMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    // Server-side initialization
    public CalamityCraftingMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenuTypes.CALAMITY_CRAFTING_MENU.get(), containerId);
        this.access = access;

        // Top Input Slot (Matches your top-left box)
        this.addSlot(new Slot(this.inputContainer, 0, 20, 24));

        // Bottom Input Slot (Matches your bottom-left box)
        this.addSlot(new Slot(this.inputContainer, 1, 20, 48));

        // Single Output Slot (Matches your far-right result box)
        this.addSlot(new Slot(this.resultContainer, 2, 143, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
            @Override
            public void onTake(Player player, ItemStack stack) {
                CalamityCraftingMenu.this.inputContainer.getItem(0).shrink(1);
                CalamityCraftingMenu.this.inputContainer.getItem(1).shrink(1);
                CalamityCraftingMenu.this.slotsChanged(CalamityCraftingMenu.this.inputContainer);
                super.onTake(player, stack);
            }
        });

        // Add Main Player Inventory Grids (Matches your grey item box grid layout)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Add Player Hotbar Slots
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        this.access.execute((level, pos) -> {
            ItemStack slot1 = this.inputContainer.getItem(0);
            ItemStack slot2 = this.inputContainer.getItem(1);

            // Temporary baseline recipe check: Change these targets as you see fit
            if (slot1.is(net.minecraft.world.item.Items.GLASS) && slot2.is(net.minecraft.world.item.Items.IRON_INGOT)) {
                this.resultContainer.setItem(0, new ItemStack(MinecraftCalamity.GLASS_INGOT.get(), 4));
            } else {
                this.resultContainer.removeItem(0, 1);
            }
        });
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.clearContainer(player, this.inputContainer));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (index == 2) {
                if (!this.moveItemStackTo(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (index == 0 || index == 1) {
                if (!this.moveItemStackTo(itemStack2, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 3 && index < 39) {
                if (!this.moveItemStackTo(itemStack2, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemStack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack2);
        }
        return itemStack;
    }
}
