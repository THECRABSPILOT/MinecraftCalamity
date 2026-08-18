package crab.mods.minecraftcalamity.network;

import crab.mods.minecraftcalamity.capability.AccessoryCapability;
import crab.mods.minecraftcalamity.items.ModItems;
import crab.mods.minecraftcalamity.items.accessory.SatchelItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AccessoriesC2SPacket {

    public AccessoriesC2SPacket() {}

    public AccessoriesC2SPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            ItemStack satchel = findSatchel(player);
            if (!satchel.isEmpty()) {
                ItemStackHandler satchelInv = SatchelItem.getContents(satchel);

                // Swap the 9 hotbar slots (0-8) with the 9 satchel slots
                for (int i = 0; i < 9; i++) {
                    ItemStack hotbarStack = player.getInventory().getItem(i);
                    ItemStack satchelStack = satchelInv.getStackInSlot(i);

                    player.getInventory().setItem(i, satchelStack.copy());
                    satchelInv.setStackInSlot(i, hotbarStack.copy());
                }

                SatchelItem.saveContents(satchel, satchelInv);

                // Toggle the "Active" flag so the restriction knows the state
                CompoundTag tag = satchel.getOrCreateTag();
                boolean currentlyActive = tag.getBoolean("Active");
                tag.putBoolean("Active", !currentlyActive);

                player.containerMenu.broadcastChanges();
            }
        });
        return true;
    }

    private ItemStack findSatchel(ServerPlayer player) {
        // 1. Check Accessory Inventory Capability
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

        // 2. Fallback: Search Player Main Inventory
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.SATCHEL.get())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}