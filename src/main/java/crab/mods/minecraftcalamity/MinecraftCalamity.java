package crab.mods.minecraftcalamity;

import crab.mods.minecraftcalamity.capability.AccessoryCapability;
import crab.mods.minecraftcalamity.blocks.ModBlocks;
import crab.mods.minecraftcalamity.blocks.entity.ModBlockEntities;
import crab.mods.minecraftcalamity.client.screen.AccessoryScreen;
import crab.mods.minecraftcalamity.effect.ModEffects;
import crab.mods.minecraftcalamity.entity.ModEntityTypes;
import crab.mods.minecraftcalamity.entity.custom.CaveWizardEntity;
import crab.mods.minecraftcalamity.items.ModItems;
import crab.mods.minecraftcalamity.items.magicitems.SpellBookItem;
import crab.mods.minecraftcalamity.items.alchemy.ModPotions;
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


    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public static final RegistryObject<CreativeModeTab> CALAMITY_TAB = CREATIVE_MODE_TABS.register("calamity_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.HELLFORGE.get())) // Icon displayed on tab header
                    .title(Component.translatable("creativetab.minecraftcalamity.tab"))
                    .displayItems((parameters, output) -> {
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



        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CalamityConfig.SPEC);


        MinecraftForge.EVENT_BUS.register(this);
    }




    public static void openAccessoryMenu(ServerPlayer player) {
        NetworkHooks.openScreen(
                player,
                new SimpleMenuProvider(
                        (containerId, playerInventory, p) -> new AccessoryMenu(containerId, playerInventory),
                        Component.literal(" ")
                )
        );
    }


    @SubscribeEvent
    public static void onLeftClickMouse(InputEvent.MouseButton.Pre event) {

        if (event.getButton() == 0 && event.getAction() == 1) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.screen == null) {
                ItemStack mainHandItem = mc.player.getMainHandItem();

                if (mainHandItem.getItem() instanceof SpellBookItem) {
                    ModMessages.sendToServer(new SpellCastPacket());

                    event.setCanceled(true);
                }
            }
        }
    }
}
