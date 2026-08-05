package crab.mods.minecraftcalamity.items.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class StaffSpells {

    private static boolean bounceModifierActive = false;
    private static boolean splitModifierActive = false;

    // Helper class to store individual entity bounce state and split capability
    private static class TrackerData {
        int bouncesLeft;
        boolean canSplit;

        TrackerData(int bouncesLeft, boolean canSplit) {
            this.bouncesLeft = bouncesLeft;
            this.canSplit = canSplit;
        }
    }

    // Track bouncing/splitting entities
    private static final ConcurrentHashMap<Entity, TrackerData> trackedEntities = new ConcurrentHashMap<>();

    private final Object[][] projectiles = {
            {"fireball_core", 5},

    };

    private final Object[][] modifiers = {
            {"bounce_modifier", 5},
            {"split_modifier", 5}
    };

    public Object[][] getSpelldata() {
        return projectiles;
    }

    public Object[][] getModifiers() {
        return modifiers;
    }

    // --- MODIFIERS ---

    public void bounce_modifier(Player player, Level level) {
        if (!level.isClientSide()) {
            bounceModifierActive = true;
        }
    }

    public void split_modifier(Player player, Level level) {
        if (!level.isClientSide()) {
            splitModifierActive = true;
        }
    }

    // --- PROJECTILES ---

    public void fireball_core(Player player, Level level) {
        if (!level.isClientSide()) {
            Vec3 lookVec = player.getLookAngle();
            double spawnX = player.getX() + lookVec.x * 1.5;
            double spawnY = player.getY() + player.getEyeHeight() + lookVec.y * 1.5;
            double spawnZ = player.getZ() + lookVec.z * 1.5;

            LargeFireball fireball = new LargeFireball(level, player, lookVec.x, lookVec.y, lookVec.z, 1);
            fireball.setPos(spawnX, spawnY, spawnZ);

            if (bounceModifierActive) {
                trackedEntities.put(fireball, new TrackerData(3, splitModifierActive));
            }

            level.addFreshEntity(fireball);
            resetModifiers();
        }
    }



    private static void resetModifiers() {
        bounceModifierActive = false;
        splitModifierActive = false;
    }

    @SubscribeEvent
    public static void onEntityTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.level.isClientSide()) {
            return;
        }

        if (trackedEntities.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<Entity, TrackerData>> iterator = trackedEntities.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Entity, TrackerData> entry = iterator.next();
            Entity entity = entry.getKey();
            TrackerData data = entry.getValue();

            if (entity == null || !entity.isAlive() || data.bouncesLeft <= 0) {
                iterator.remove();
                continue;
            }

            Vec3 position = entity.position();
            Vec3 motion = entity.getDeltaMovement();

            if (!event.level.noCollision(entity, entity.getBoundingBox().inflate(0.1))) {
                double newX = motion.x;
                double newY = motion.y;
                double newZ = motion.z;
                boolean bounced = false;

                // Detect axis collisions
                if (event.level.getBlockState(BlockPos.containing(position.x + motion.x, position.y, position.z)).isSolid()) {
                    newX = -motion.x * 0.8;
                    bounced = true;
                }
                if (event.level.getBlockState(BlockPos.containing(position.x, position.y + motion.y, position.z)).isSolid()) {
                    newY = -motion.y * 0.8;
                    bounced = true;
                }
                if (event.level.getBlockState(BlockPos.containing(position.x, position.y, position.z + motion.z)).isSolid()) {
                    newZ = -motion.z * 0.8;
                    bounced = true;
                }

                if (bounced) {
                    Vec3 reboundedVelocity = new Vec3(newX, newY, newZ);
                    entity.setDeltaMovement(reboundedVelocity);

                    // Handle splitting logic on FIRST bounce
                    if (data.canSplit) {
                        spawnSplitProjectiles(event.level, entity, reboundedVelocity, data.bouncesLeft - 1);
                        data.canSplit = false; // Split only triggers once
                    }

                    data.bouncesLeft--;
                    if (data.bouncesLeft <= 0) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    private static void spawnSplitProjectiles(Level level, Entity original, Vec3 baseVelocity, int remainingBounces) {
        // Angled velocities (+30 and -30 degrees yaw rotation)
        Vec3 leftVel = baseVelocity.yRot((float) Math.toRadians(30));
        Vec3 rightVel = baseVelocity.yRot((float) Math.toRadians(-30));

         if (original instanceof LargeFireball oldFB) {
            spawnChildFireball(level, oldFB, leftVel, remainingBounces);
            spawnChildFireball(level, oldFB, rightVel, remainingBounces);
        }
    }



    private static void spawnChildFireball(Level level, LargeFireball parent, Vec3 velocity, int remainingBounces) {
        LargeFireball child = new LargeFireball(level, (Player) parent.getOwner(), velocity.x, velocity.y, velocity.z, 1);
        child.setPos(parent.getX(), parent.getY(), parent.getZ());
        child.setDeltaMovement(velocity);

        if (remainingBounces > 0) {
            trackedEntities.put(child, new TrackerData(remainingBounces, false));
        }
        level.addFreshEntity(child);
    }
}