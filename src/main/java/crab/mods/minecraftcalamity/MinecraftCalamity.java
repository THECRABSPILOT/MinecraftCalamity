package crab.mods.minecraftcalamity;

import crab.mods.minecraftcalamity.accessory.AccessoryCapability;
import crab.mods.minecraftcalamity.blocks.ModBlocks;
import crab.mods.minecraftcalamity.blocks.entity.ModBlockEntities;
import crab.mods.minecraftcalamity.client.screen.AccessoryScreen;
import crab.mods.minecraftcalamity.config.CalamityConfig;
import crab.mods.minecraftcalamity.items.ModItems;
import crab.mods.minecraftcalamity.menu.AccessoryMenu;
import crab.mods.minecraftcalamity.menu.ModMenuTypes;
import crab.mods.minecraftcalamity.network.ModMessages;
import crab.mods.minecraftcalamity.network.SyncAccessoriesS2CPacket;
import crab.mods.minecraftcalamity.recipe.ModRecipes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
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

        // 1. Register Deferred Registers
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModRecipes.register(modEventBus);

        // 2. Register Mod Class Modules
        ModMenuTypes.register(modEventBus);

        // 3. Register Network Channel
        ModMessages.register();

        // 4. Client Setup Listener
        modEventBus.addListener(this::clientSetup);

        // 5. Config Registration
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CalamityConfig.SPEC);

        // 6. Register Forge Event Bus
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.ACCESSORY_MENU.get(), AccessoryScreen::new);
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
}