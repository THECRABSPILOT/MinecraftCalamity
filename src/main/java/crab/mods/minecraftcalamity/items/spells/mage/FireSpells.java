package crab.mods.minecraftcalamity.items.spells.mage;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

//MANAGER FOR ALL FIRE SPELLS
public class FireSpells {
    Object[][] spelldata = {
            {"fire_flight", 50, 30},
            {"fireball", 50, 0.2},

    };

    public Object[][] getspelldata() {
        return spelldata;
    }

    public void fireball(Player player, Level level) {
        if (!level.isClientSide()) {
            player.sendSystemMessage(Component.literal("casted fireball"));
        }
    }
}