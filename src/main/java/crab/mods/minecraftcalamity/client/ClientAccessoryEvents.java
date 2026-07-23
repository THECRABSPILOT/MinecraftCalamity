package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.accessory.AccessoryCapability;
import crab.mods.minecraftcalamity.items.TestAccessoryItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderBlockScreenEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientAccessoryEvents {

    @SubscribeEvent
    public static void onRenderScreenOverlay(RenderBlockScreenEffectEvent event) {
        // Check if the screen overlay being rendered is FIRE
        if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.FIRE) {
            Player player = Minecraft.getInstance().player;

            if (player != null) {
                // Check if the player has the accessory equipped
                player.getCapability(AccessoryCapability.ACCESSORY_CAP).ifPresent(cap -> {
                    var inv = cap.getInventory();
                    for (int slot = 0; slot < inv.getSlots(); slot++) {
                        ItemStack stack = inv.getStackInSlot(slot);
                        if (stack.getItem() instanceof TestAccessoryItem) {
                            // Cancel rendering the fire screen overlay
                            event.setCanceled(true);
                            break;
                        }
                    }
                });
            }
        }
    }
}