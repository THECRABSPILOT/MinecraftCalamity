package crab.mods.minecraftcalamity.network;

import crab.mods.minecraftcalamity.accessory.AccessoryCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncAccessoriesS2CPacket {
    private final CompoundTag nbt;

    public SyncAccessoriesS2CPacket(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SyncAccessoriesS2CPacket(FriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getCapability(AccessoryCapability.ACCESSORY_CAP).ifPresent(cap -> {
                    cap.deserializeNBT(nbt);
                });
            }
        });
        context.setPacketHandled(true);
    }
}