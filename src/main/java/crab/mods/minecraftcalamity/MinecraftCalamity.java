package crab.mods.minecraftcalamity;

import crab.mods.minecraftcalamity.client.screen.AccessoryScreen;
import crab.mods.minecraftcalamity.config.CalamityConfig;


import crab.mods.minecraftcalamity.items.ModItems;
import crab.mods.minecraftcalamity.menu.AccessoryMenu;
import crab.mods.minecraftcalamity.menu.ModMenuTypes;
import crab.mods.minecraftcalamity.network.ModMessages;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(MinecraftCalamity.MODID)
public class MinecraftCalamity {

    public static final String MODID = "minecraftcalamity";

    // Deferred Registers
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public MinecraftCalamity(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // 1. Register Deferred Registers
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModItems.register(modEventBus);
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
}