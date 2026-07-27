package crab.mods.minecraftcalamity.menu;

import crab.mods.minecraftcalamity.blocks.entity.ArcaneWorkbenchBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class ArcaneWorkbenchMenu extends AbstractContainerMenu {

    public static final TagKey<Item> STAFF_TAG = ItemTags.create(new ResourceLocation("minecraftcalamity", "staff"));
    public static final TagKey<Item> CORE_TAG = ItemTags.create(new ResourceLocation("minecraftcalamity", "spellcore"));

    public final ArcaneWorkbenchBlockEntity blockEntity;

    public ArcaneWorkbenchMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ArcaneWorkbenchMenu(int containerId, Inventory playerInv, BlockEntity entity) {
        super(ModMenuTypes.ARCANE_WORKBENCH_MENU.get(), containerId);
        checkContainerSize(playerInv, 12);
        this.blockEntity = (ArcaneWorkbenchBlockEntity) entity;

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // 0: Input Staff Slot (Shifted: -4X, +6Y)
            this.addSlot(new SlotItemHandler(handler, 0, 16, 26) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.is(STAFF_TAG);
                }
            });

            // 1: Core / Inscription Scroll Slot (Shifted: -4X, +6Y)
            this.addSlot(new SlotItemHandler(handler, 1, 16, 56) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.is(CORE_TAG);
                }
            });

            // 2: Output Slot (Shifted: +4Y)
            this.addSlot(new SlotItemHandler(handler, 2, 140, 39) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }

                @Override
                public void onTake(Player player, ItemStack stack) {
                    blockEntity.onTakeOutput(player);
                    super.onTake(player, stack);
                }
            });

            // 3-11: Dynamic Spell Modifier Slots
            int modGridX = 58;
            int modGridY = 22;
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    int slotIndex = 3 + (row * 3 + col);
                    int modifierIndex = row * 3 + col;

                    this.addSlot(new SlotItemHandler(handler, slotIndex, modGridX + col * 18, modGridY + row * 18) {
                        @Override
                        public boolean isActive() {
                            return modifierIndex < blockEntity.getAvailableSpellSlots();
                        }

                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return false; // Dynamic grid click assignment
                        }
                    });
                }
            }
        });

        addPlayerInventory(playerInv);
        addPlayerHotbar(playerInv);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        System.out.println("[DEBUG-MENU] Clicked Slot ID: " + slotId + " | Button: " + button + " | Type: " + clickType + " | Side: " + (player.level().isClientSide() ? "CLIENT" : "SERVER"));

        if (slotId >= 3 && slotId <= 11) {
            int modifierIndex = slotId - 3;
            int activeSlots = blockEntity.getAvailableSpellSlots();
            System.out.println("[DEBUG-MENU] Grid Slot Clicked (" + modifierIndex + "). Active Staff Slots: " + activeSlots);

            if (modifierIndex < activeSlots) {
                if (!player.level().isClientSide()) {
                    System.out.println("[DEBUG-MENU] Calling handleModifierSlotClick on Server...");
                    blockEntity.handleModifierSlotClick(slotId);
                } else {
                    System.out.println("[DEBUG-MENU] Ignored on Client (handled on Server).");
                }
                return;
            } else {
                System.out.println("[DEBUG-MENU] Slot index " + modifierIndex + " is locked/inactive!");
            }
        }

        super.clicked(slotId, button, clickType, player);
    }

    private void addPlayerInventory(Inventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInv) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 12) {
                if (index == 2) {
                    blockEntity.onTakeOutput(player);
                }
                if (!this.moveItemStackTo(stackInSlot, 12, 48, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stackInSlot.is(STAFF_TAG)) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stackInSlot.is(CORE_TAG)) {
                    if (!this.moveItemStackTo(stackInSlot, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return itemstack;
    }
}