package crab.mods.minecraftcalamity;

import crab.mods.minecraftcalamity.accessory.AccessoryCapability;
import crab.mods.minecraftcalamity.blocks.ModBlocks;
import crab.mods.minecraftcalamity.blocks.entity.ModBlockEntities;
import crab.mods.minecraftcalamity.client.screen.AccessoryScreen;
import crab.mods.minecraftcalamity.config.CalamityConfig;
import crab.mods.minecraftcalamity.effect.ModEffects;
import crab.mods.minecraftcalamity.entity.ModEntityTypes;
import crab.mods.minecraftcalamity.entity.custom.CaveWizardEntity;
import crab.mods.minecraftcalamity.items.ModItems;
import crab.mods.minecraftcalamity.items.magicitems.SpellBookItem;
import crab.mods.minecraftcalamity.items.potion.ModPotions;
import crab.mods.minecraftcalamity.menu.AccessoryMenu;
import crab.mods.minecraftcalamity.menu.ModMenuTypes;
import crab.mods.minecraftcalamity.network.ModMessages;
import crab.mods.minecraftcalamity.network.SpellCastPacket;
import crab.mods.minecraftcalamity.network.SyncAccessoriesS2CPacket;
import crab.mods.minecraftcalamity.recipe.ModRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(MinecraftCalamity.MODID)
@Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MinecraftCalamity {

    public static final String MODID = "minecraftcalamity";

    // Deferred Registers
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Register Creative Tab
    public static final RegistryObject<CreativeModeTab> CALAMITY_TAB = CREATIVE_MODE_TABS.register("calamity_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.HELLFORGE.get())) // Icon displayed on tab header
                    .title(Component.translatable("creativetab.minecraftcalamity.tab"))
                    .displayItems((parameters, output) -> {
                        // Populates tab with all items registered in ModItems
                        ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    })
                    .build());

    public MinecraftCalamity(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        //so many registers broo i need to merge a few at some point
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModEffects.register(modEventBus);
        ModPotions.register(modEventBus);
        ModMessages.register();


        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::commonSetup);


        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CalamityConfig.SPEC);

        // 6. Register Forge Event Bus
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.ACCESSORY_MENU.get(), AccessoryScreen::new);
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Entity Spawn Placement
            SpawnPlacements.register(
                    ModEntityTypes.CAVE_WIZARD.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    CaveWizardEntity::checkCaveWizardSpawnRules
            );

            // Standard Forge Brewing Recipe
            BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
                    Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LEAPING)),
                    Ingredient.of(ModItems.SHULKER_MEAT.get()),
                    PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LEVITATION_POTION.get())
            ));
        });
    }

    // Server-side call to open the accessory GUI
    public static void openAccessoryMenu(ServerPlayer player) {
        NetworkHooks.openScreen(
                player,
                new SimpleMenuProvider(
                        (containerId, playerInventory, p) -> new AccessoryMenu(containerId, playerInventory),
                        Component.literal(" ")
                )
        );
    }

    // =========================================================
    // ACCESSORY SYNC EVENT LISTENERS
    // =========================================================

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

    @SubscribeEvent
    public static void onLeftClickMouse(InputEvent.MouseButton.Pre event) {
        // Button 0 = Left Click, action 1 = Pressed down
        if (event.getButton() == 0 && event.getAction() == 1) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.screen == null) {
                ItemStack mainHandItem = mc.player.getMainHandItem();

                if (mainHandItem.getItem() instanceof SpellBookItem) {
                    // Send packet to server to fire the active spell
                    ModMessages.sendToServer(new SpellCastPacket());

                    // Cancel normal left-click behavior
                    event.setCanceled(true);
                }
            }
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Check if the current tab being built is the vanilla Food & Drinks tab
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {

            // 1. Create a regular potion itemstack and apply your custom potion registry object
            ItemStack regularPotion = new ItemStack(Items.POTION);
            PotionUtils.setPotion(regularPotion, ModPotions.MANA_BREW.get());
            event.accept(regularPotion);

            // 2. Create a splash potion itemstack
            ItemStack splashPotion = new ItemStack(Items.SPLASH_POTION);
            PotionUtils.setPotion(splashPotion, ModPotions.MANA_BREW.get());
            event.accept(splashPotion);

            // 3. Create a lingering potion itemstack
            ItemStack lingeringPotion = new ItemStack(Items.LINGERING_POTION);
            PotionUtils.setPotion(lingeringPotion, ModPotions.MANA_BREW.get());
            event.accept(lingeringPotion);
        }
    }


}
