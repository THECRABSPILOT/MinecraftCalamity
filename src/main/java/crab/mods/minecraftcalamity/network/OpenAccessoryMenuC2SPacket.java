package crab.mods.minecraftcalamity.network;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenAccessoryMenuC2SPacket {

    public OpenAccessoryMenuC2SPacket() {}

    public OpenAccessoryMenuC2SPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                MinecraftCalamity.openAccessoryMenu(player);
            }
        });
        return true;
    }
}