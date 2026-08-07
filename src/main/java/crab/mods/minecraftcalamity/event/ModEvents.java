package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.accessory.AccessoryCapability;
import crab.mods.minecraftcalamity.entity.ModEntityTypes;
import crab.mods.minecraftcalamity.items.ModItems;
import crab.mods.minecraftcalamity.items.potion.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID)
public class ModEvents {




    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverLevel) {
            BlockPos pos = event.getPos();

            // Find any slime marker at this exact broken block position and discard it
            serverLevel.getEntitiesOfClass(Slime.class, new AABB(pos),
                    slime -> slime.getPersistentData().getBoolean("OreSightMarker")).forEach(Slime::discard);
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(AccessoryCapability.ACCESSORY_CAP).isPresent()) {
                event.addCapability(
                        new ResourceLocation(MinecraftCalamity.MODID, "book"),
                        new AccessoryCapability()
                );
            }
        }
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(net.minecraftforge.event.entity.SpawnPlacementRegisterEvent event) {
        event.register(
                ModEntityTypes.CAVE_WIZARD.get(),
                net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
                net.minecraft.world.level.levelgen.Heightmap.Types.OCEAN_FLOOR,
                (entityType, level, spawnType, pos, random) ->
                        level.getRawBrightness(pos, 0) == 0 && net.minecraft.world.entity.monster.Monster.checkMonsterSpawnRules(entityType, level, spawnType, pos, random),
                net.minecraftforge.event.entity.SpawnPlacementRegisterEvent.Operation.REPLACE
        );
    }


    public static boolean hasAccessoryEquipped(Player player, Item targetAccessory) {
        // Fetch the accessory capability from the player instance
        return player.getCapability(AccessoryCapability.ACCESSORY_CAP).map(accessor -> {
            ItemStackHandler handler = accessor.getInventory();

            // Loop through all 8 accessory slots
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.is(targetAccessory)) {
                    return true; // Found the accessory equipped!
                }
            }
            return false;
        }).orElse(false); // Return false if capability is missing
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            replaceHotbarSlots(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            replaceHotbarSlots(player);
        }
    }

    private static void replaceHotbarSlots(ServerPlayer player) {
        var menu = player.inventoryMenu;

        for (int i = 0; i < menu.slots.size(); i++) {
            Slot old = menu.slots.get(i);

            // Only touch the real hotbar slots (player inventory, slot index 0-8)
            if (old.container == player.getInventory() && old.getSlotIndex() >= 0 && old.getSlotIndex() < 9) {
                menu.slots.set(i, new RestrictedHotbarSlot(
                        old.container,
                        old.getSlotIndex(),
                        old.x,
                        old.y,
                        player
                ));
            }
        }
    }



}
