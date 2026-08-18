package crab.mods.minecraftcalamity.menu;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.capability.AccessoryCapability;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class AccessoryMenu extends AbstractContainerMenu {

    public static final TagKey<Item> ACCESSORIES_TAG = TagKey.create(
            Registries.ITEM,
            new ResourceLocation(MinecraftCalamity.MODID, "accessories")
    );

    // Vanilla Armor Slots order: Helmet (39), Chestplate (38), Leggings (37), Boots (36)
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public AccessoryMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory);
    }

    public AccessoryMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.ACCESSORY_MENU.get(), containerId);

        Player player = playerInventory.player;

        // =========================================================
        // 1. ACCESSORY SLOTS (Menu Slots 0 - 7)
        // =========================================================
        player.getCapability(AccessoryCapability.ACCESSORY_CAP).ifPresent(cap -> {
            var inv = cap.getInventory();
            for (int row = 0; row < 4; ++row) {
                for (int col = 0; col < 2; ++col) {
                    int slotIdx = row * 2 + col; // Guaranteed 0, 1, 2, 3, 4, 5, 6, 7
                    int x = 57 + (col * 18);
                    int y = 7 + (row * 18);

                    this.addSlot(new SlotItemHandler(inv, slotIdx, x, y) {
                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return stack.is(ACCESSORIES_TAG);
                        }
                    });
                }
            }
        });

        // =========================================================
        // 2. VANILLA ARMOR SLOTS (Menu Slots 8 - 11)
        // =========================================================
        for (int i = 0; i < 4; ++i) {
            final EquipmentSlot slotType = ARMOR_SLOTS[i];
            int vanillaArmorSlotIndex = 39 - i; // 39=Head, 38=Chest, 37=Legs, 36=Feet

            int x = 95;
            int y = 7 + (i * 18);

            this.addSlot(new Slot(playerInventory, vanillaArmorSlotIndex, x, y) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    // MUST strictly match equipment slot type
                    return Mob.getEquipmentSlotForItem(stack) == slotType;
                }
            });
        }

        // =========================================================
        // 3. MAIN PLAYER INVENTORY (Menu Slots 12 - 38)
        // =========================================================
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + (col * 18);
                int y = 84 + (row * 18);
                // Player inv slots 9..35
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, x, y));
            }
        }

        // =========================================================
        // 4. HOTBAR (Menu Slots 39 - 47)
        // =========================================================
        for (int col = 0; col < 9; ++col) {
            int x = 8 + (col * 18);
            int y = 142;
            // Player hotbar slots 0..8
            this.addSlot(new Slot(playerInventory, col, x, y));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // Accessories (0-7) or Armor (8-11) -> Move to Inventory (12-47)
            if (index < 12) {
                if (!this.moveItemStackTo(itemstack1, 12, 48, true)) {
                    return ItemStack.EMPTY;
                }
            }
            // Player Inventory / Hotbar (12-47) -> Accessories, Armor, or Swap
            else {
                if (itemstack1.is(ACCESSORIES_TAG)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 8, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (Mob.getEquipmentSlotForItem(itemstack1).getType() == EquipmentSlot.Type.ARMOR) {
                    EquipmentSlot slotType = Mob.getEquipmentSlotForItem(itemstack1);
                    int targetArmorSlot = 8 + (39 - slotType.getIndex());
                    if (!this.moveItemStackTo(itemstack1, targetArmorSlot, targetArmorSlot + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 12 && index < 39) { // Inventory -> Hotbar
                    if (!this.moveItemStackTo(itemstack1, 39, 48, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 39 && index < 48) { // Hotbar -> Inventory
                    if (!this.moveItemStackTo(itemstack1, 12, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }
}