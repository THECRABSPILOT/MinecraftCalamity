package crab.mods.minecraftcalamity.items.accessory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SatchelItem extends Item {

    public SatchelItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static ItemStackHandler getContents(ItemStack satchel) {
        // Custom handler that ONLY accepts potions (or empty)
        ItemStackHandler handler = new ItemStackHandler(9) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.isEmpty() || stack.getItem() instanceof PotionItem;
            }
        };

        if (satchel.hasTag() && satchel.getTag().contains("Inventory")) {
            handler.deserializeNBT(satchel.getTag().getCompound("Inventory"));
        }
        return handler;
    }

    public static void saveContents(ItemStack satchel, ItemStackHandler handler) {
        CompoundTag tag = satchel.getOrCreateTag();
        tag.put("Inventory", handler.serializeNBT());
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack satchel, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY) {
            return false;
        }

        ItemStack heldStack = slot.getItem();

        // Hard reject non-potions
        if (!heldStack.isEmpty() && !(heldStack.getItem() instanceof PotionItem)) {
            return false;
        }

        ItemStackHandler handler = getContents(satchel);

        if (heldStack.isEmpty()) {
            // Take an item out (from the end)
            for (int i = 8; i >= 0; i--) {
                ItemStack stored = handler.getStackInSlot(i);
                if (!stored.isEmpty()) {
                    slot.set(stored.copy());
                    handler.setStackInSlot(i, ItemStack.EMPTY);
                    saveContents(satchel, handler);
                    return true;
                }
            }
        } else {
            // Put the potion into the first empty slot
            for (int i = 0; i < 9; i++) {
                if (handler.getStackInSlot(i).isEmpty()) {
                    handler.setStackInSlot(i, heldStack.copy());
                    slot.set(ItemStack.EMPTY);
                    saveContents(satchel, handler);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("§7Can Hold 9 Potions and switch the hotbar to these potions"));
        boolean active = stack.hasTag() && stack.getTag().getBoolean("Active");
        tooltipComponents.add(Component.literal(active ? "§a§lACTIVE – Hotbar locked to potions only" : "§7Inactive"));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}