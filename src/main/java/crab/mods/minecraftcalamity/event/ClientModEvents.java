package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.client.model.CustomArmorModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    // 1. Define the layer location for your 3D armor model
    public static final ModelLayerLocation CUSTOM_ARMOR_LAYER = new ModelLayerLocation(
            new ResourceLocation(MinecraftCalamity.MODID, "custom_armor"), "main");

    // 2. Register the layer definition with Forge during client setup
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CUSTOM_ARMOR_LAYER, CustomArmorModel::createBodyLayer);
    }
}