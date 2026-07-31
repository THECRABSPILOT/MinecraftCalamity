package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.accessory.AccessoryCapability;
import crab.mods.minecraftcalamity.items.ModItems;
import crab.mods.minecraftcalamity.items.TestAccessoryItem;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AccessoryEventHandler {

    // Helper method to check if the player has a specific accessory item equipped
    private static boolean hasAccessoryEquipped(Player player, Item targetItem) {
        return player.getCapability(AccessoryCapability.ACCESSORY_CAP)
                .map(cap -> {
                    var inv = cap.getInventory();
                    for (int slot = 0; slot < inv.getSlots(); slot++) {
                        ItemStack stack = inv.getStackInSlot(slot);
                        if (stack.is(targetItem)) {
                            return true;
                        }
                    }
                    return false;
                }).orElse(false);
    }

    // 1. Cancel attacks based on specific accessory checks
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player) {
            // Nullify fire damage for TestAccessoryItem
            if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
                if (hasAccessoryEquipped(player, ModItems.TEST_ACCESSORY.get())) {
                    event.setCanceled(true);
                }
            }

            // Nullify wither damage for cross_ring item
            if (event.getSource().is(DamageTypes.WITHER)) {
                if (hasAccessoryEquipped(player, ModItems.CROSS_RING.get())) {
                    event.setCanceled(true);
                }
            }
        }
    }

    // 2. Instantly extinguish visual fire on the player every tick
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            Player player = event.player;
            if (player.isOnFire() && hasAccessoryEquipped(player, ModItems.TEST_ACCESSORY.get())) {
                player.clearFire();
            }
        }
    }
}