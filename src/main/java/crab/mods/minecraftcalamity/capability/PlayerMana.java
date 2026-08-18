package crab.mods.minecraftcalamity.capability;

import crab.mods.minecraftcalamity.effect.ModEffects;
import crab.mods.minecraftcalamity.menu.AccessoryMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
            // Check accessory capability slots for Mana Star using its item tag
            bonusMana += player.getCapability(AccessoryCapability.ACCESSORY_CAP)
                    .map(cap -> {
                        int count = 0;
                        var inv = cap.getInventory();
                        for (int slot = 0; slot < inv.getSlots(); slot++) {
                            ItemStack stack = inv.getStackInSlot(slot);
                            // Checks if the equipped accessory is part of your mana star/accessory definition
                            if (stack.is(AccessoryMenu.ACCESSORIES_TAG)) {
                                // If you want specifically the Mana Star to give mana, check its registry name string instead:
                                if (stack.getItem().getDescriptionId().contains("mana_star")) {
                                    count++;
                                }
                            }
                        }
                        return count * 50;
                    }).orElse(0);
        }
        return this.baseMaxMana + bonusMana;
    }

    public boolean consumeMana(Player player, int amount) {
        if (this.currentMana >= amount) {
            this.currentMana -= amount;
            this.sync(player);
            return true;
        }
        return false;
    }

    public void regenMana(Player player, int amount) {
        if (player != null && player.hasEffect(ModEffects.MANA_REGEN.get())) {
            this.currentMana = Math.min(getMaxMana(player), this.currentMana + 10);
        } else {
            this.currentMana = Math.min(getMaxMana(player), this.currentMana + amount);
        }
        this.sync(player);
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