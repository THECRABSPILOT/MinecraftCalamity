package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.capability.AccessoryCapability;
import crab.mods.minecraftcalamity.entity.ModEntityTypes;
import crab.mods.minecraftcalamity.entity.custom.CaveWizardEntity;
import crab.mods.minecraftcalamity.items.ModItems;
import crab.mods.minecraftcalamity.items.alchemy.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.items.ItemStackHandler;

@Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.CAVE_WIZARD.get(), CaveWizardEntity.createAttributes().build());
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


    //public static boolean hasAccessoryEquipped(Player player, Item targetAccessory) {
    //    return player.getCapability(AccessoryCapability.ACCESSORY_CAP).map(accessor -> {
    //        ItemStackHandler handler = accessor.getInventory();
    //       for (int i = 0; i < handler.getSlots(); i++) {
    //          ItemStack stack = handler.getStackInSlot(i);
    //            if (!stack.isEmpty() && stack.is(targetAccessory)) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }).orElse(false);
    // }
    //says never used but ill test that

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


    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

            SpawnPlacements.register(
                    ModEntityTypes.CAVE_WIZARD.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    CaveWizardEntity::checkCaveWizardSpawnRules
            );


            BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
                    Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LEAPING)),
                    Ingredient.of(ModItems.SHULKER_MEAT.get()),
                    PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LEVITATION_POTION.get())
            ));

            BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
                    Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.REGENERATION)),
                    Ingredient.of(ModItems.MANA_STAR.get()),
                    PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.MANA_BREW.get())
            ));
        });
    }



}
