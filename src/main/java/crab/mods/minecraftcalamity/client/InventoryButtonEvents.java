package crab.mods.minecraftcalamity.client;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.network.ModMessages;
import crab.mods.minecraftcalamity.network.OpenAccessoryMenuC2SPacket;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID, value = Dist.CLIENT)
public class InventoryButtonEvents {


    private static final ResourceLocation BUTTON_TEX =
            new ResourceLocation("minecraftcalamity", "textures/gui/container/invic.png");

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {

        if (event.getScreen() instanceof InventoryScreen gui) {
            int left = gui.getGuiLeft();
            int top = gui.getGuiTop();


            ImageButton accessoryButton = new ImageButton(
                    left + 27, top + 62, 9, 9,
                    0, 0, 0,
                    BUTTON_TEX, 256, 256,
                    button -> {
                        ModMessages.sendToServer(new OpenAccessoryMenuC2SPacket());
                    }

            );

            event.addListener(accessoryButton);
        }
    }
}