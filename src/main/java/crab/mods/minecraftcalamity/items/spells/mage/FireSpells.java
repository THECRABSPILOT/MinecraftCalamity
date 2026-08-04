package crab.mods.minecraftcalamity.items.spells.mage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "minecraftcalamity")
public class FireSpells {

    private static final ConcurrentHashMap<UUID, FlightTracker> activeFlights = new ConcurrentHashMap<>();

    private static class FlightTracker {
        public int flighttime = 0;
        public boolean shouldibeflyingrn = true;

        FlightTracker() {}
    }

    private static final Object[][] SPELL_DATA = {
            {"fire_flight", 50, 0.1},
            {"fireball", 50, 0.2},
            {"infernal_column", 30, 5.0}
    };

    public Object[][] getspelldata() {
        return SPELL_DATA;
    }

    // --- Spell 1: Fireball ---
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

    // --- Spell 2: Fire Flight ---
    public void fire_flight(Player player, Level level) {
        if (!level.isClientSide()) {
            Vec3 initialVec = player.getDeltaMovement();
            Vec3 flightVec = new Vec3(initialVec.x, 1.2D, initialVec.z);
            player.setDeltaMovement(flightVec);

            activeFlights.put(player.getUUID(), new FlightTracker());
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
                    Vec3 look = player.getLookAngle();
                    Vec3 motion = new Vec3(look.x * 1.5, look.y * 1.5, look.z * 1.5);
                    player.setDeltaMovement(motion);
                    player.hurtMarked = true;

                    // Reset fall distance during flight so they don't take instant fall damage
                    player.resetFallDistance();

                    player.startFallFlying();
                    ((ServerLevel) player.level()).sendParticles(
                            ParticleTypes.FLAME,
                            player.getX(), player.getY(), player.getZ(),
                            5, 0.2D, 0.2D, 0.2D, 0.05D
                    );

                    tracker.flighttime++;
                } else {
                    tracker.shouldibeflyingrn = false;
                    player.stopFallFlying();
                    activeFlights.remove(playerUUID);
                }
            }
        }
    }

    // --- Spell 3: Infernal Column ---
    public void infernal_column(Player player, Level level) {
        if (level.isClientSide()) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        int maxRange = 32;

        HitResult hitResult = player.pick(maxRange, 0.0f, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos targetPos = blockHit.getBlockPos().relative(blockHit.getDirection());
        Vec3 centerVec = Vec3.atBottomCenterOf(targetPos);

        triggerColumnEffects(serverLevel, centerVec, player);
    }

    public static void triggerColumnEffects(ServerLevel level, Vec3 center, Player caster) {
        int columnHeight = 64;
        double columnRadius = 2.5;
        float damagePerTick = 2.0f; // 1 heart per trigger

        // 🟢 1. Spawn DENSE Vertical Cylinder Particles
        // We iterate vertically, but we'll also slightly offset the circles horizontally
        // to fill in gaps.
        for (double yOffset = 0; yOffset < columnHeight; yOffset += 0.25) { // Vertical step (thicker stack)

            // Double the horizontal particle density (16 steps instead of 8)
            int circleDensity = 64;

            for (int i = 0; i < circleDensity; i++) {
                double angle = (2 * Math.PI / circleDensity) * i;
                double px = center.x() + columnRadius * Math.cos(angle);
                double pz = center.z() + columnRadius * Math.sin(angle);
                double py = center.y() + yOffset;

                // Outer Flame Wall: Low speed, small offset
                level.sendParticles(ParticleTypes.FLAME, px, py, pz, 1, 0.01, 0.05, 0.01, 0.005);
            }

            // Fill the inside with occasional dense lava pops
            if (yOffset % 1.0 == 0) {
                level.sendParticles(ParticleTypes.LAVA, center.x(), center.y() + yOffset, center.z(), 3, 0.75, 0.5, 0.75, 0.01);
            }
        }

        // 2. Locate entities inside column area
        AABB columnBox = new AABB(
                center.x() - columnRadius, center.y(), center.z() - columnRadius,
                center.x() + columnRadius, center.y() + columnHeight, center.z() + columnRadius
        );

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, columnBox, e -> e != caster);
        DamageSource damageSource = level.damageSources().playerAttack(caster);

        // 3. Apply Vacuum Pull & Damage
        for (LivingEntity target : targets) {
            Vec3 pullDirection = center.subtract(target.position());

            if (pullDirection.length() > 0.1) {
                Vec3 velocityModifier = pullDirection.normalize().scale(0.15);
                target.setDeltaMovement(velocityModifier.x(), target.getDeltaMovement().y(), velocityModifier.z());
                target.hurtMarked = true;
            }

            target.hurt(damageSource, damagePerTick);
            target.setSecondsOnFire(5);
        }
    }
}