package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.capability.AccessoryCapability;
import crab.mods.minecraftcalamity.items.accessory.CloudInAJar;
import crab.mods.minecraftcalamity.network.DoubleJumpPacket;
import crab.mods.minecraftcalamity.network.ModMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientDoubleJumpHandler {

    private static boolean canDoubleJump = false;
    private static boolean lastJumpPressed = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        if (player.onGround() || player.isInWater() || player.onClimbable()) {
            canDoubleJump = true;
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.onGround() || player.isInWater() || player.onClimbable()) return;

        boolean jumpPressed = Minecraft.getInstance().options.keyJump.isDown();

        if (jumpPressed && !lastJumpPressed && canDoubleJump) {
            player.getCapability(AccessoryCapability.ACCESSORY_CAP).ifPresent(cap -> {
                var inv = cap.getInventory();
                for (int slot = 0; slot < inv.getSlots(); slot++) {
                    ItemStack stack = inv.getStackInSlot(slot);
                    if (stack.getItem() instanceof CloudInAJar) {
                        performDoubleJump(player);
                        canDoubleJump = false;
                        break;
                    }
                }
            });
        }

        lastJumpPressed = jumpPressed;
    }

    private static void performDoubleJump(LocalPlayer player) {
        Vec3 currentDelta = player.getDeltaMovement();
        double jumpY = 0.45D;

        if (player.isSprinting()) {

            Vec3 look = player.getLookAngle();
            double sprintBoostMultiplier = 1.35D;

            player.setDeltaMovement(
                    currentDelta.x * sprintBoostMultiplier + (look.x * 0.15D),
                    jumpY,
                    currentDelta.z * sprintBoostMultiplier + (look.z * 0.15D)
            );
        } else {
            player.setDeltaMovement(currentDelta.x, jumpY, currentDelta.z);
        }


        for (int i = 0; i < 18; i++) {
            double xSpeed = (player.getRandom().nextDouble() - 0.5D) * 0.25D;
            double ySpeed = player.getRandom().nextDouble() * 0.05D;
            double zSpeed = (player.getRandom().nextDouble() - 0.5D) * 0.25D;

            player.level().addParticle(
                    ParticleTypes.CLOUD,
                    player.getX(), player.getY(), player.getZ(),
                    xSpeed, ySpeed, zSpeed
            );
        }

        ModMessages.sendToServer(new DoubleJumpPacket());
    }
}