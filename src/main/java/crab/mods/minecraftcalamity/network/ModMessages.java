package crab.mods.minecraftcalamity.network;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(MinecraftCalamity.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // 1. Menu Packet
        net.messageBuilder(OpenAccessoryMenuC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenAccessoryMenuC2SPacket::new)
                .encoder(OpenAccessoryMenuC2SPacket::toBytes)
                .consumerMainThread(OpenAccessoryMenuC2SPacket::handle)
                .add();

        // 2. Double Jump Packet
        net.messageBuilder(DoubleJumpPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(DoubleJumpPacket::new)
                .encoder(DoubleJumpPacket::encode)
                .consumerMainThread(DoubleJumpPacket::handle)
                .add();

        // 3. Accessory Sync Packet (Server -> Client)
        net.messageBuilder(SyncAccessoriesS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncAccessoriesS2CPacket::new)
                .encoder(SyncAccessoriesS2CPacket::encode)
                .consumerMainThread(SyncAccessoriesS2CPacket::handle)
                .add();

        // 4. Spell Cast & Mana Sync Packet (Bidirectional)
        // Notice: Removed NetworkDirection parameter so it can go C2S and S2C safely
        net.messageBuilder(SpellCastPacket.class, id())
                .decoder(SpellCastPacket::new)
                .encoder(SpellCastPacket::encode)
                .consumerMainThread(SpellCastPacket::handle)
                .add();
    }

    // Send packet to Server
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    // Send packet to specific Player (Fixes your compiler error!)
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
