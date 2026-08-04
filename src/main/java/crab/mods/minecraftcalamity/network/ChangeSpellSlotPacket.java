package crab.mods.minecraftcalamity.network;

import crab.mods.minecraftcalamity.items.magicitems.SpellBookItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeSpellSlotPacket {
    private final int slotIndex;

    public ChangeSpellSlotPacket(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    public ChangeSpellSlotPacket(FriendlyByteBuf buf) {
        this.slotIndex = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.slotIndex);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (mainHand.getItem() instanceof SpellBookItem spellBook) {
                    // Update NBT on the SERVER side so it doesn't revert
                    SpellBookItem.setSelectedSlot(mainHand, this.slotIndex, spellBook.getSpellSlots());
                }
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}