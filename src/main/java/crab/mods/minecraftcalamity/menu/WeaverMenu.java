package crab.mods.minecraftcalamity.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import crab.mods.minecraftcalamity.menu.ModMenuTypes;

public class WeaverMenu extends AbstractContainerMenu {

    private final BlockEntity blockEntity;
    private final ContainerData data;

    // Client-side Constructor (reads network buffer safely)
    public WeaverMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory,
                playerInventory.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(4));
    }

    // Server-side Constructor (handles both physical blocks and portable items)
    public WeaverMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity, ContainerData data) {

        super(ModMenuTypes.WEAVER_MENU.get(), containerId);
        checkContainerDataCount(data, 4);
        this.blockEntity = blockEntity;
        this.data = data;
// Only register block inventory slots if the physical container exists
        if (this.blockEntity != null) {
            this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                // Slot 0: Top Left Input -> (34, 17)
                this.addSlot(new SlotItemHandler(handler, 0, 34, 17));

                // Slot 1: Top Right Input -> (56, 17)
                this.addSlot(new SlotItemHandler(handler, 1, 56, 17));

                // Slot 2: Fuel Slot
                this.addSlot(new SlotItemHandler(handler, 2, 45, 53) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return ForgeHooks.getBurnTime(stack, null) > 0;
                    }
                });

                // Slot 3: Output Slot
                this.addSlot(new SlotItemHandler(handler, 3, 116, 35));
            });
        } else {
            // Portable Fallback: Add 4 dummy slots if no block entity is present
            // This prevents index-out-of-bounds container synchronization crashes
            net.minecraft.world.SimpleContainer dummyInventory = new net.minecraft.world.SimpleContainer(4);
            this.addSlot(new net.minecraftforge.items.SlotItemHandler(new net.minecraftforge.items.ItemStackHandler(4), 0, 34, 17));
            this.addSlot(new net.minecraftforge.items.SlotItemHandler(new net.minecraftforge.items.ItemStackHandler(4), 1, 56, 17));
            this.addSlot(new net.minecraftforge.items.SlotItemHandler(new net.minecraftforge.items.ItemStackHandler(4), 2, 45, 53));
            this.addSlot(new net.minecraftforge.items.SlotItemHandler(new net.minecraftforge.items.ItemStackHandler(4), 3, 116, 35));
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addDataSlots(data);

    }

    // Getters for Screen progress arrows/flames
    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 24;
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;

    }

    public boolean isLit() {
        return data.get(2) > 0;
    }

    public int getScaledLit() {
        int litTime = this.data.get(2);
        int litDuration = this.data.get(3);
        int flameSize = 14;
        return litDuration != 0 ? litTime * flameSize / litDuration : 0;

    }

    @Override
    public boolean stillValid(Player player) {
// If opened portably via the WeaverBookItem, blockEntity is null.
        if (this.blockEntity == null) {
            return true;
        }
// If opened via physical block entity, check world boundaries normally
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());

    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();
            if (index < 4) {
                if (!this.moveItemStackTo(stackInSlot, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (ForgeHooks.getBurnTime(stackInSlot, null) > 0) {
                    if (!this.moveItemStackTo(stackInSlot, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(stackInSlot, 0, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;

    }

}