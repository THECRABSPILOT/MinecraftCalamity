package crab.mods.minecraftcalamity.network;

import crab.mods.minecraftcalamity.capability.ManaCapabilityProvider;
import crab.mods.minecraftcalamity.items.magicitems.SpellBookItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpellCastPacket {
    private final boolean isSyncPacket;
    private int currentMana = 0;
    private int maxMana = 0;

    // Standard client -> server cast request constructor
    public SpellCastPacket() {
        this.isSyncPacket = false;
    }

    // Server -> client data sync constructor
    public SpellCastPacket(int currentMana, int maxMana) {
        this.isSyncPacket = true;
        this.currentMana = currentMana;
        this.maxMana = maxMana;
    }

    // Decoder
    public SpellCastPacket(FriendlyByteBuf buf) {
        this.isSyncPacket = buf.readBoolean();
        if (this.isSyncPacket) {
            this.currentMana = buf.readInt();
            this.maxMana = buf.readInt();
        }
    }

    // Encoder
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.isSyncPacket);
        if (this.isSyncPacket) {
            buf.writeInt(this.currentMana);
            buf.writeInt(this.maxMana);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (this.isSyncPacket) {
                // CLIENT SIDE LOGIC: Read numbers and update the HUD data holder
                if (FMLEnvironment.dist == Dist.CLIENT) {
                    ClientPayloadHandler.handleManaSync(this.currentMana);
                }
            } else {
                // SERVER SIDE LOGIC: Fire the spell invocation checks
                ServerPlayer player = context.getSender();
                if (player != null) {
                    ItemStack mainHandItem = player.getMainHandItem();
                    if (mainHandItem.getItem() instanceof SpellBookItem spellBook) {
                        spellBook.castActiveSpell(mainHandItem, player, player.level());
                    }
                }
            }
        });
        context.setPacketHandled(true);
        return true;
    }

    // Inner class prevents client-only Minecraft crashes when loading on dedicated servers
    private static class ClientPayloadHandler {
        private static void handleManaSync(int currentMana) {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getCapability(ManaCapabilityProvider.PLAYER_MANA).ifPresent(mana -> {
                    mana.setCurrentMana(currentMana, Minecraft.getInstance().player);
                });
            }
        }
    }
}
