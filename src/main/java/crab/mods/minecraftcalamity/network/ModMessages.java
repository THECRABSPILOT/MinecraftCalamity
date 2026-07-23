package crab.mods.minecraftcalamity.network;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
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

        net.messageBuilder(OpenAccessoryMenuC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenAccessoryMenuC2SPacket::new)
                .encoder(OpenAccessoryMenuC2SPacket::toBytes)
                .consumerMainThread(OpenAccessoryMenuC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}