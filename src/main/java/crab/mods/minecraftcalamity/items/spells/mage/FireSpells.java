package crab.mods.minecraftcalamity.items.spells.mage;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
            Vec3 lookVec = player.getLookAngle();
            double spawnX = player.getX() + lookVec.x * 1.5;
            double spawnY = player.getY() + player.getEyeHeight() + lookVec.y * 1.5;
            double spawnZ = player.getZ() + lookVec.z * 1.5;

            LargeFireball fireball = new LargeFireball(level, player, lookVec.x, lookVec.y, lookVec.z, 1);
            fireball.setPos(spawnX, spawnY, spawnZ);



            level.addFreshEntity(fireball);

        }
    }
}