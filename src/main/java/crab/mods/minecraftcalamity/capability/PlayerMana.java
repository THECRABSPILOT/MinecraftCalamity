package crab.mods.minecraftcalamity.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class PlayerMana {
    private int currentMana = 100;
    private int baseMaxMana = 100;

    public int getCurrentMana() {
        return this.currentMana;
    }

    public void setCurrentMana(int mana, Player player) {
        this.currentMana = Math.max(0, Math.min(mana, getMaxMana(player)));
    }

    public int getMaxMana(Player player) {
        int bonusMana = 0;
        if (player != null) {
            // Future Armor Loop Hooks:
            // for (ItemStack armor : player.getArmorSlots()) { ... }
        }
        return this.baseMaxMana + bonusMana;
    }

    public boolean consumeMana(Player player, int amount) {
        if (this.currentMana >= amount) {
            this.currentMana -= amount;
            this.sync(player); // <--- AUTOMATIC CAST SYNC HOOK
            return true;
        }
        return false;
    }

    public void regenMana(Player player, int amount) {
        this.currentMana = Math.min(getMaxMana(player), this.currentMana + amount);
        this.sync(player); // <--- AUTOMATIC REGEN SYNC HOOK
    }

    public void saveNBT(CompoundTag tag) {
        tag.putInt("CurrentMana", currentMana);
        tag.putInt("BaseMaxMana", baseMaxMana);
    }

    public void loadNBT(CompoundTag tag) {
        this.currentMana = tag.getInt("CurrentMana");
        this.baseMaxMana = tag.getInt("BaseMaxMana");
    }

    public void sync(net.minecraft.world.entity.player.Player player) {
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            crab.mods.minecraftcalamity.network.ModMessages.sendToPlayer(
                    new crab.mods.minecraftcalamity.network.SpellCastPacket(this.currentMana, getMaxMana(player)),
                    serverPlayer
            );
        }
    }
}
