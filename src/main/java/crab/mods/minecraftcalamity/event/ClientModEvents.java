package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.client.model.CalamititeArmorModel;
import crab.mods.minecraftcalamity.items.CalamitieArmorItem;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import crab.mods.minecraftcalamity.client.gui.HellforgeScreen;
import crab.mods.minecraftcalamity.menu.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

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
        });
    }


    // 3. Forge Bus Listener for Player Layer Hiding
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
    }
}