package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.accessory.AccessoryCapability;
import crab.mods.minecraftcalamity.items.ModItems;
import crab.mods.minecraftcalamity.items.TestAccessoryItem;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

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

    // 3. Drop accessories on death unless keepInventory is enabled
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        // MUST verify the dying entity is actually a Player
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // If keepInventory is enabled, do not drop accessories
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            return;
        }

        // Drop equipped accessories into the death drops
        player.getCapability(AccessoryCapability.ACCESSORY_CAP).ifPresent(accessoryCap -> {
            ItemStackHandler inventory = accessoryCap.getInventory();

            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (!stack.isEmpty()) {
                    ItemEntity drop = new ItemEntity(
                            player.level(),
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            stack.copy()
                    );
                    drop.setPickUpDelay(40); // 2 second delay before pickup

                    event.getDrops().add(drop);

                    // Clear the slot so items aren't cloned on respawn
                    inventory.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        });
    }

    // 4. Preserve accessories on respawn if keepInventory is active or changing dimensions
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        // Skip copying if the player died and keepInventory is disabled
        if (event.isWasDeath() && !oldPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            return;
        }

        // Temporarily revive original player's capabilities to read NBT before cleanup
        oldPlayer.reviveCaps();

        oldPlayer.getCapability(AccessoryCapability.ACCESSORY_CAP).ifPresent(oldCap -> {
            newPlayer.getCapability(AccessoryCapability.ACCESSORY_CAP).ifPresent(newCap -> {
                newCap.deserializeNBT(oldCap.serializeNBT());
            });
        });

        oldPlayer.invalidateCaps();
    }
}