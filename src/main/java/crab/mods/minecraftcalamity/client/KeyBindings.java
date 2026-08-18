package crab.mods.minecraftcalamity.client;

import com.mojang.blaze3d.platform.InputConstants;
import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.network.AccessoriesC2SPacket;
import crab.mods.minecraftcalamity.network.ModMessages;
import crab.mods.minecraftcalamity.network.OpenAccessoryMenuC2SPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static final String KEY_CATEGORY = "key.categories." + MinecraftCalamity.MODID;


    public static final String KEY_OPEN_ACCESSORIES = "key." + MinecraftCalamity.MODID + ".open_accessories";
    public static final String KEY_CHECK_SATCHEL = "key." + MinecraftCalamity.MODID + ".open_satchel";


    public static final KeyMapping ACCESSORY_KEY = new KeyMapping(
            KEY_OPEN_ACCESSORIES,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            KEY_CATEGORY
    );


    public static final KeyMapping CHECK_SATCHEL_KEY = new KeyMapping(
            KEY_CHECK_SATCHEL,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            KEY_CATEGORY
    );

    @Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModClientEvents {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(ACCESSORY_KEY);
            event.register(CHECK_SATCHEL_KEY);
        }
    }

    @Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID, value = Dist.CLIENT)
    public static class ForgeClientEvents {
        @SubscribeEvent
        public static void onKeyInput(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {

                // Original Keybind Handling
                while (ACCESSORY_KEY.consumeClick()) {
                    if (Minecraft.getInstance().player != null) {
                        ModMessages.sendToServer(new OpenAccessoryMenuC2SPacket());
                    }
                }


                while (CHECK_SATCHEL_KEY.consumeClick()) {
                    if (Minecraft.getInstance().player != null) {
                        ModMessages.sendToServer(new AccessoriesC2SPacket());
                    }
                }
            }
        }
    }
}
