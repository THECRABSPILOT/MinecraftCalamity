package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.accessory.AccessoryCapability;
import crab.mods.minecraftcalamity.items.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.items.ItemStackHandler;

public class RestrictedHotbarSlot extends Slot {

    private final Player player;

    public RestrictedHotbarSlot(net.minecraft.world.Container container, int index, int x, int y, Player player) {
        super(container, index, x, y);
        this.player = player;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        // While the satchel is Active → only potions (or empty) are allowed
        if (isSatchelActive(player)) {
            return stack.isEmpty() || stack.getItem() instanceof PotionItem;
        }
        // Normal behaviour when satchel is inactive
        return super.mayPlace(stack);
    }

    private static boolean isSatchelActive(Player player) {
        ItemStack satchel = findSatchel(player);
        return !satchel.isEmpty() && satchel.getOrCreateTag().getBoolean("Active");
    }

    private static ItemStack findSatchel(Player player) {
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