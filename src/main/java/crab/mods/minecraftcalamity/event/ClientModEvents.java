package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.client.gui.HellforgeScreen;
import crab.mods.minecraftcalamity.client.model.CalamititeArmorModel;
import crab.mods.minecraftcalamity.client.renderer.PedestalBlockEntityRenderer;
import crab.mods.minecraftcalamity.client.screen.ArcaneWorkbenchScreen;
import crab.mods.minecraftcalamity.entity.ModEntityTypes;
import crab.mods.minecraftcalamity.entity.client.*;
import crab.mods.minecraftcalamity.items.CalamitieArmorItem;
import crab.mods.minecraftcalamity.items.magicitems.SpellBookItem;
import crab.mods.minecraftcalamity.menu.ModMenuTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import crab.mods.minecraftcalamity.capability.ManaCapabilityProvider;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

import static crab.mods.minecraftcalamity.blocks.ModBlocks.SWORD_IN_STONE;

@Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    // 1. Define the layer location for your 3D armor model
    public static final ModelLayerLocation CALAMITITE_ARMOR_LAYER = new ModelLayerLocation(
            new ResourceLocation(MinecraftCalamity.MODID, "calamitite_armor"), "main");

    // 2. Register the layer definition with Forge during client setup
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CALAMITITE_ARMOR_LAYER, CalamititeArmorModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.HELLFORGE_MENU.get(), HellforgeScreen::new);
            MenuScreens.register(ModMenuTypes.ARCANE_WORKBENCH_MENU.get(), ArcaneWorkbenchScreen::new);

            if (SWORD_IN_STONE != null && SWORD_IN_STONE.get() != null) {
                ItemBlockRenderTypes.setRenderLayer(SWORD_IN_STONE.get(), RenderType.cutout());
            }

            BlockEntityRenderers.register(
                    crab.mods.minecraftcalamity.blocks.entity.ModBlockEntities.PEDESTAL_BE.get(),
                    PedestalBlockEntityRenderer::new
            );
        });
    }


    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CaveWizardModel.LAYER_LOCATION, CaveWizardModel::createBodyLayer);
        event.registerLayerDefinition(DynamicProjectileModel.LAYER_LOCATION, DynamicProjectileModel::createBodyLayer);
        event.registerLayerDefinition(SwordModel.LAYER_LOCATION, SwordModel::createBodyLayer); // Add this line
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.CAVE_WIZARD.get(), CaveWizardRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.DYNAMIC_PROJECTILE.get(), DynamicProjectileRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.SWORD_PROJECTILE.get(), SwordProjectileRenderer::new);
    }

    // 3. Forge Bus Listener for Player Layer Hiding & Input Events
    @Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeClientEvents {

        @SubscribeEvent
        public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
            Player player = event.getEntity();
            PlayerModel<?> model = event.getRenderer().getModel();

            // Hide hat layer when wearing Calamitite Helmet
            ItemStack headStack = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!headStack.isEmpty() && headStack.getItem() instanceof CalamitieArmorItem) {
                model.hat.visible = false;
            }

            // Hide jacket/coat layer when wearing Calamitite Chestplate
            ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (!chestStack.isEmpty() && chestStack.getItem() instanceof CalamitieArmorItem) {
                model.jacket.visible = false;
                model.leftSleeve.visible = false;
                model.rightSleeve.visible = false;
            }

            // Hide pants layers when wearing Calamitite Leggings
            ItemStack legsStack = player.getItemBySlot(EquipmentSlot.LEGS);
            if (!legsStack.isEmpty() && legsStack.getItem() instanceof CalamitieArmorItem) {
                model.leftPants.visible = false;
                model.rightPants.visible = false;
            }
        }

        private static int customSpellIndex = 0;

        @SubscribeEvent
        public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            if (player == null) return;

            // Check if the player is holding the specific item and actively holding right-click
            ItemStack mainHandItem = player.getMainHandItem();
            if (mainHandItem.getItem() instanceof SpellBookItem spellBook && SpellBookItem.isHoldingRightClick()) {

                // Determine scroll direction (positive = up, negative = down)
                double scrollDelta = event.getScrollDelta();
                int currentSlot = SpellBookItem.getSelectedSlot(mainHandItem);

                if (scrollDelta > 0) {
                    currentSlot++;
                } else if (scrollDelta < 0) {
                    currentSlot--;
                }

                // Update the slot using NBT-backed bounds checking from your SpellBookItem class
                SpellBookItem.setSelectedSlot(mainHandItem, currentSlot, spellBook.getSpellSlots());

                // Display feedback message
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("Selected Spell Slot: " + (SpellBookItem.getSelectedSlot(mainHandItem) + 1)), true);

                // CRITICAL: Cancel the event so the hotbar slot doesn't change
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
            if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) {
                return;
            }

            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.options.hideGui) {
                return;
            }

            Player player = mc.player;

            // Check if holding either the SpellBookItem or the ModularStaffItem
            boolean holdingMagicItem = player.getMainHandItem().getItem() instanceof SpellBookItem
                    || player.getOffhandItem().getItem() instanceof SpellBookItem
                    || player.getMainHandItem().getItem() instanceof crab.mods.minecraftcalamity.items.magicitems.ModularStaffItem
                    || player.getOffhandItem().getItem() instanceof crab.mods.minecraftcalamity.items.magicitems.ModularStaffItem;

            if (holdingMagicItem) {
                player.getCapability(ManaCapabilityProvider.PLAYER_MANA).ifPresent(mana -> {
                    int currentMana = mana.getCurrentMana();
                    int maxMana = mana.getMaxMana(player);

                    String text = "Mana: " + currentMana + "/" + maxMana;

                    GuiGraphics graphics = event.getGuiGraphics();
                    Font font = mc.font;

                    int screenWidth = event.getWindow().getGuiScaledWidth();
                    int screenHeight = event.getWindow().getGuiScaledHeight();
                    int textWidth = font.width(text);

                    int x = (screenWidth / 2) - (textWidth / 2);
                    int y = screenHeight - 55;

                    graphics.drawString(font, text, x, y, 0x55FFFF, true);
                });
            }
        }

    }
}