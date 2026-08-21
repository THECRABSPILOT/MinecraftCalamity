package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.capability.AccessoryCapability;
import crab.mods.minecraftcalamity.items.ModItems;
import crab.mods.minecraftcalamity.network.ModMessages;
import crab.mods.minecraftcalamity.network.SyncAccessoriesS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

@Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AccessoryEventHandler {


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

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (event.getLevel() instanceof Level level) {
            if (player != null && hasAccessoryEquipped(player, ModItems.HEATER_GLOVE.get())) {
                //freaking die - Gabriel ultrakill but censored
                //im bored
                //please work
                if (level instanceof ServerLevel serverLevel) {
                    BlockPos pos = event.getPos();
                    BlockState state = event.getState();
                    Block block = state.getBlock();

                    var grabrecipes = level.getRecipeManager();
                    var meltyrecipies = grabrecipes.getAllRecipesFor(RecipeType.SMELTING);
                    ItemStack blockstak = new ItemStack(block.asItem()); //blehhha

                    for (SmeltingRecipe recipe : meltyrecipies) {
                        if (recipe.getIngredients().get(0).test(blockstak)) {
                            ItemStack melted = recipe.getResultItem(level.registryAccess()).copy();

                            serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

                            ItemEntity itemEntity = new ItemEntity(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, melted);
                            serverLevel.addFreshEntity(itemEntity);


                            break;
                        }
                    }

                }
            }
        }
    }

    public static void MeltStuff(Level level, Block block) {
        if (level.isClientSide) return; //freaking die
        var grabrecipes = level.getRecipeManager();
        var meltyrecipies = grabrecipes.getAllRecipesFor(RecipeType.SMELTING);
        ItemStack blockstak = new ItemStack(block.asItem()); //blehhha

        for (SmeltingRecipe recipe : meltyrecipies) {
            if (recipe.getIngredients().get(0).test(blockstak)) {
                ItemStack melted = recipe.getResultItem(level.registryAccess()).copy();



                break;
            }
        }

    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player) {
            //ctrl+C
            if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
                if (hasAccessoryEquipped(player, ModItems.TEST_ACCESSORY.get())) {
                    event.setCanceled(true);
                }
            }

            //ctrl+V
            if (event.getSource().is(DamageTypes.WITHER)) {
                if (hasAccessoryEquipped(player, ModItems.CROSS_RING.get())) {
                    event.setCanceled(true);
                }
            }
        }
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            Player player = event.player;
            if (player.isOnFire() && hasAccessoryEquipped(player, ModItems.TEST_ACCESSORY.get())) {
                player.clearFire();
            }
        }
    }


    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }


        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            return;
        }


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
                    drop.setPickUpDelay(40);

                    event.getDrops().add(drop);


                    inventory.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        });
    }


    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();


        if (event.isWasDeath() && !oldPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            return;
        }


        oldPlayer.reviveCaps();

        oldPlayer.getCapability(AccessoryCapability.ACCESSORY_CAP).ifPresent(oldCap -> {
            newPlayer.getCapability(AccessoryCapability.ACCESSORY_CAP).ifPresent(newCap -> {
                newCap.deserializeNBT(oldCap.serializeNBT());
            });
        });

        oldPlayer.invalidateCaps();
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            syncAccessories(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncAccessories(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncAccessories(player);
        }
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncAccessories(player);
        }
    }

    public static void syncAccessories(ServerPlayer player) {
        player.getCapability(AccessoryCapability.ACCESSORY_CAP).ifPresent(cap -> {
            ModMessages.sendToPlayer(new SyncAccessoriesS2CPacket(cap.serializeNBT()), player);
        });
    }


}