package crab.mods.minecraftcalamity.items.spells.mage;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "minecraftcalamity")
public class FireSpells {

    private static final ConcurrentHashMap<UUID, FlightTracker> activeFlights = new ConcurrentHashMap<>();

    private static class FlightTracker {
        public Vec3 flythatwayVec;
        public int flighttime = 0;
        public boolean shouldibeflyingrn = true;

        FlightTracker(Vec3 lookAngle) {
            this.flythatwayVec = lookAngle;
        }
    }

    Object[][] spelldata = {
            {"fire_flight", 50, 0.1},
            {"fireball", 50, 0.2}
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

    public void fire_flight(Player player, Level level) {
        if (!level.isClientSide()) {
            Vec3 initialVec = player.getDeltaMovement();
            Vec3 flightVec = new Vec3(initialVec.x, 1.2D, initialVec.z);
            player.setDeltaMovement(flightVec);

            activeFlights.put(player.getUUID(), new FlightTracker(player.getLookAngle()));

            player.hurtMarked = true;
        }
    }

    @SubscribeEvent
    public static void fly(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() || event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;
        UUID playerUUID = player.getUUID();

        if (activeFlights.containsKey(playerUUID)) {
            FlightTracker tracker = activeFlights.get(playerUUID);

            if (tracker.shouldibeflyingrn) {
                if (tracker.flighttime < 200) {
                    Vec3 gothatwaybozo = new Vec3(player.getLookAngle().x * 1.5, player.getLookAngle().y * 1.5, player.getLookAngle().z * 1.5);
                    player.setDeltaMovement(gothatwaybozo);
                    player.hurtMarked = true;

                    player.startFallFlying();
                    ((ServerLevel)player.level()).sendParticles(ParticleTypes.FLAME, player.getX(), player.getY(), player.getZ(), 5, 0.2D, 0.2D, 0.2D, 0.05D);

                    tracker.flighttime++;
                } else {
                    tracker.shouldibeflyingrn = false;
                    player.stopFallFlying();
                    activeFlights.remove(playerUUID);
                }
            }
        }
    }
}
