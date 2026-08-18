package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.capability.AccessoryCapability;
import crab.mods.minecraftcalamity.items.accessory.TestAccessoryItem;
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

        if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.FIRE) {
            Player player = Minecraft.getInstance().player;

            if (player != null) {

                player.getCapability(AccessoryCapability.ACCESSORY_CAP).ifPresent(cap -> {
                    var inv = cap.getInventory();
                    for (int slot = 0; slot < inv.getSlots(); slot++) {
                        ItemStack stack = inv.getStackInSlot(slot);
                        if (stack.getItem() instanceof TestAccessoryItem) {

                            event.setCanceled(true);
                            break;
                        }
                    }
                });
            }
        }
    }
}