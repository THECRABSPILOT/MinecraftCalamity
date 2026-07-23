package crab.mods.minecraftcalamity.menu;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.accessory.AccessoryCapability;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class AccessoryMenu extends AbstractContainerMenu {

    // Define the Accessory Tag Key directly inside or reference your tag helper
    public static final TagKey<Item> ACCESSORIES_TAG = TagKey.create(
            Registries.ITEM,
            new ResourceLocation(MinecraftCalamity.MODID, "accessories")
    );

    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    // Client-side constructor for network packets
    public AccessoryMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory);
    }

    // Main constructor (Server & Client)
    public AccessoryMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.ACCESSORY_MENU.get(), containerId);

        Player player = playerInventory.player;

        // 1. ACCESSORY SLOTS (8 Slots restricted to #minecraftcalamity:accessories)
        player.getCapability(AccessoryCapability.ACCESSORY_CAP).ifPresent(cap -> {
            var inv = cap.getInventory();
            int slotIdx = 0;
            for (int row = 0; row < 4; ++row) {
                for (int col = 0; col < 2; ++col) {
                    if (slotIdx < inv.getSlots()) {
                        int x = 57 + (col * 18);
                        int y = 7 + (row * 18);

                        // Custom slot restricting items to the ACCESSORIES_TAG
                        this.addSlot(new SlotItemHandler(inv, slotIdx++, x, y) {
                            @Override
                            public boolean mayPlace(ItemStack stack) {
                                return stack.is(ACCESSORIES_TAG);
                            }
                        });
                    }
                }
            }
        });

        // 2. VANILLA ARMOR SLOTS (Helmet, Chestplate, Leggings, Boots)
        for (int i = 0; i < 4; ++i) {
            final EquipmentSlot slotType = ARMOR_SLOTS[i];
            int vanillaArmorIndex = 39 - i;

            int x = 95;
            int y = 7 + (i * 18);

            this.addSlot(new Slot(playerInventory, vanillaArmorIndex, x, y) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.canEquip(slotType, player);
                }
            });
        }

        // 3. MAIN PLAYER INVENTORY (3x9 Grid)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + (col * 18);
                int y = 84 + (row * 18);
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, x, y));
            }
        }

        // 4. HOTBAR (1x9 Grid)
        for (int col = 0; col < 9; ++col) {
            int x = 8 + (col * 18);
            int y = 142;
            this.addSlot(new Slot(playerInventory, col, x, y));
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