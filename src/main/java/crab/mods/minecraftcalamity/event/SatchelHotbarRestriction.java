package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.accessory.AccessoryCapability;
import crab.mods.minecraftcalamity.items.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

@Mod.EventBusSubscriber(modid = "minecraftcalamity", bus = Mod.EventBusSubscriber.Bus.FORGE) // change modid if needed
public class SatchelHotbarRestriction {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        if (!isSatchelActive(player)) return;

        boolean changed = false;

        // Force every non-potion out of the hotbar
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && !(stack.getItem() instanceof PotionItem)) {
                ItemStack toMove = stack.copy();
                player.getInventory().setItem(i, ItemStack.EMPTY);

                // IMPORTANT: do NOT use inventory.add() — it prefers the hotbar!
                if (!tryPutInMainInventory(player, toMove)) {
                    player.drop(toMove, false);
                }
                changed = true;
            }
        }

        // Also force non-potions off the cursor
        ItemStack carried = player.containerMenu.getCarried();
        if (!carried.isEmpty() && !(carried.getItem() instanceof PotionItem)) {
            ItemStack toMove = carried.copy();
            player.containerMenu.setCarried(ItemStack.EMPTY);

            if (!tryPutInMainInventory(player, toMove)) {
                player.drop(toMove, false);
            }
            changed = true;
        }

        if (changed) {
            player.containerMenu.broadcastChanges();
            player.getInventory().setChanged();
        }
    }

    // Safe placement that NEVER touches the hotbar
    private static boolean tryPutInMainInventory(ServerPlayer player, ItemStack stack) {
        // First try to merge with existing stacks in main inventory (slots 9-35)
        for (int i = 9; i < 36; i++) {
            ItemStack inSlot = player.getInventory().getItem(i);
            if (!inSlot.isEmpty() && ItemStack.isSameItemSameTags(inSlot, stack)) {
                int space = inSlot.getMaxStackSize() - inSlot.getCount();
                if (space > 0) {
                    int move = Math.min(space, stack.getCount());
                    inSlot.grow(move);
                    stack.shrink(move);
                    if (stack.isEmpty()) return true;
                }
            }
        }

        // Then find empty slots in main inventory only
        for (int i = 9; i < 36; i++) {
            if (player.getInventory().getItem(i).isEmpty()) {
                player.getInventory().setItem(i, stack.copy());
                stack.setCount(0);
                return true;
            }
        }

        return false; // main inventory full
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!isSatchelActive(player)) return;

        ItemStack stack = event.getItem().getItem();
        if (stack.isEmpty() || stack.getItem() instanceof PotionItem) return;

        // Cancel normal pickup and force into main inventory only
        event.setCanceled(true);

        ItemStack remaining = stack.copy();
        if (tryPutInMainInventory(player, remaining)) {
            event.getItem().discard();
        } else {
            // put the leftover back on the ground
            event.getItem().setItem(remaining);
            event.setCanceled(false);
        }

        player.containerMenu.broadcastChanges();
    }

    private static boolean isSatchelActive(ServerPlayer player) {
        ItemStack satchel = findSatchel(player);
        return !satchel.isEmpty() && satchel.getOrCreateTag().getBoolean("Active");
    }

    private static ItemStack findSatchel(ServerPlayer player) {
        var capOpt = player.getCapability(AccessoryCapability.ACCESSORY_CAP).resolve();
        if (capOpt.isPresent()) {
            ItemStackHandler accInv = capOpt.get().getInventory();
            for (int i = 0; i < accInv.getSlots(); i++) {
                ItemStack stack = accInv.getStackInSlot(i);
                if (stack.is(ModItems.SATCHEL.get())) {
                    return stack;
                }
            }
        }

        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.SATCHEL.get())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}