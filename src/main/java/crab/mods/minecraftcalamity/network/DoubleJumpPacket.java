package crab.mods.minecraftcalamity.network;

import crab.mods.minecraftcalamity.accessory.AccessoryCapability;
import crab.mods.minecraftcalamity.items.CloudInAJar;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DoubleJumpPacket {

    public DoubleJumpPacket() {}

    public DoubleJumpPacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            player.getCapability(AccessoryCapability.ACCESSORY_CAP).ifPresent(cap -> {
                var inv = cap.getInventory();
                boolean hasCloud = false;

                for (int slot = 0; slot < inv.getSlots(); slot++) {
                    ItemStack stack = inv.getStackInSlot(slot);
                    if (stack.getItem() instanceof CloudInAJar) {
                        hasCloud = true;
                        break;
                    }
                }

                if (hasCloud) {
                    player.fallDistance = 0;

                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.BIG_DRIPLEAF_TILT_DOWN, SoundSource.PLAYERS, 1.0F, 1.2F);

                    for (int i = 0; i < 20; i++) {
                        double xSpeed = (player.getRandom().nextDouble() - 0.5) * 0.3;
                        double ySpeed = player.getRandom().nextDouble() * 0.1;
                        double zSpeed = (player.getRandom().nextDouble() - 0.5) * 0.3;

                        player.serverLevel().sendParticles(
                                ParticleTypes.CLOUD,
                                player.getX(), player.getY(), player.getZ(),
                                1, xSpeed, ySpeed, zSpeed, 0.1
                        );
                    }
                }
            });
        });
        context.setPacketHandled(true);
    }
}