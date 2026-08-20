package crab.mods.minecraftcalamity.items.spells;

import crab.mods.minecraftcalamity.entity.custom.SwordProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
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

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class StaffSpells {

    private static boolean bounceModifierActive = false;
    private static boolean splitModifierActive = false;


    private static class TrackerData {
        int bouncesLeft;
        boolean canSplit;
        double magicBoost;

        TrackerData(int bouncesLeft, boolean canSplit, double magicBoost) {
            this.bouncesLeft = bouncesLeft;
            this.canSplit = canSplit;
            this.magicBoost = magicBoost;
        }
    }


    private static final ConcurrentHashMap<Entity, TrackerData> trackedEntities = new ConcurrentHashMap<>();

    private final Object[][] projectiles = {
            {"fireball_core", 5},
            {"soul_bolt", 5},
            {"lightning_core", 5}
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

    //MODIFIERS

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

    //PROJECTILES

    public void fireball_core(Player player, Level level) {
        if (!level.isClientSide()) {
            Vec3 lookVec = player.getLookAngle();
            double spawnX = player.getX() + lookVec.x * 1.5;
            double spawnY = player.getY() + player.getEyeHeight() + lookVec.y * 1.5;
            double spawnZ = player.getZ() + lookVec.z * 1.5;


            double magicBoostVal = 0.0;
            if (player.getMainHandItem().getItem() instanceof crab.mods.minecraftcalamity.items.magicitems.ModularStaffItem staff) {
               try {
                    java.lang.reflect.Field boostField = staff.getClass().getDeclaredField("magicboost");
                    boostField.setAccessible(true);
                    magicBoostVal = boostField.getDouble(staff);
                } catch (Exception e) {
                    magicBoostVal = 0.0;
                }
            }

            int explosionPower = (int) Math.max(1, 1 + Math.round(magicBoostVal));

            LargeFireball fireball = new LargeFireball(level, player, lookVec.x, lookVec.y, lookVec.z, explosionPower);
            fireball.setPos(spawnX, spawnY, spawnZ);

            if (bounceModifierActive) {
                trackedEntities.put(fireball, new TrackerData(3, splitModifierActive, magicBoostVal));
            }

            level.addFreshEntity(fireball);
            resetModifiers();
        }
    }

    public void soul_bolt(Player player, Level level) {
        if (!level.isClientSide()) {
            Vec3 lookVec = player.getLookAngle();
            double spawnX = player.getX() + lookVec.x * 1.5;
            double spawnY = player.getY() + player.getEyeHeight() + lookVec.y * 1.5;
            double spawnZ = player.getZ() + lookVec.z * 1.5;

            double magicBoostVal = 0.0;
            if (player.getMainHandItem().getItem() instanceof crab.mods.minecraftcalamity.items.magicitems.ModularStaffItem staff) {
                try {
                    java.lang.reflect.Field boostField = staff.getClass().getDeclaredField("magicboost");
                    boostField.setAccessible(true);
                    magicBoostVal = boostField.getDouble(staff);
                } catch (Exception e) {
                    magicBoostVal = 0.0;
                }
            }

            EntityType<SwordProjectileEntity> projectileType = (EntityType<SwordProjectileEntity>) (EntityType<?>) net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(new net.minecraft.resources.ResourceLocation("minecraftcalamity", "dynamic_projectile"));

            SwordProjectileEntity projectile = new SwordProjectileEntity(projectileType, level);
            projectile.setPos(spawnX, spawnY, spawnZ);


            projectile.setYRot(player.getYRot());
            projectile.setXRot(player.getXRot());
            projectile.yRotO = player.getYRot();
            projectile.xRotO = player.getXRot();


            projectile.setProjectileColor(0x00ffe1);


            projectile.setParticleConfig("minecraft:soul", 0.1D, false, false);

            double baseDamage = 5.0D;
            double finalDamage = baseDamage * (1.0D + magicBoostVal);
            projectile.setStats(finalDamage, 0.0D, 0.0D, false, 10.0F, null, 0, 0);


            Vec3 motion = lookVec.scale(2.0);
            projectile.setDeltaMovement(motion);

            if (bounceModifierActive) {
                trackedEntities.put(projectile, new TrackerData(3, splitModifierActive, magicBoostVal));
            }

            level.addFreshEntity(projectile);
            resetModifiers();
        }
    }

    public void lightning_core(Player player, Level level) {if (level.isClientSide()) {return;}ServerLevel serverLevel = (ServerLevel) level;
        int maxRange = 32;HitResult hitResult = player.pick(maxRange, 0.0f, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos targetPos = blockHit.getBlockPos().relative(blockHit.getDirection());
        Vec3 centerVec = Vec3.atBottomCenterOf(targetPos);LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
        if (lightningBolt != null) {
            lightningBolt.moveTo(centerVec);if (player instanceof ServerPlayer serverPlayer) {
                lightningBolt.setCause(serverPlayer);
            }

            serverLevel.addFreshEntity(lightningBolt);
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

            AABB inflatedBox = entity.getBoundingBox().expandTowards(motion).inflate(0.8D);

            if (!event.level.noCollision(entity, inflatedBox)) {
                double newX = motion.x;
                double newY = motion.y;
                double newZ = motion.z;
                boolean bounced = false;

                 if (event.level.getBlockState(BlockPos.containing(position.x + motion.x * 1.2, position.y, position.z)).isSolid()) {
                    newX = -motion.x * 0.8;
                    bounced = true;
                }
                if (event.level.getBlockState(BlockPos.containing(position.x, position.y + motion.y * 1.2, position.z)).isSolid()) {
                    newY = -motion.y * 0.8;
                    bounced = true;
                }
                if (event.level.getBlockState(BlockPos.containing(position.x, position.y, position.z + motion.z * 1.2)).isSolid()) {
                    newZ = -motion.z * 0.8;
                    bounced = true;
                }

                if (bounced) {
                    Vec3 reboundedVelocity = new Vec3(newX, newY, newZ);
                    entity.setDeltaMovement(reboundedVelocity);

                    if (data.canSplit) {
                        spawnSplitProjectiles(event.level, entity, reboundedVelocity, data.bouncesLeft - 1, data.magicBoost);
                        data.canSplit = false;
                    }

                    data.bouncesLeft--;
                    if (data.bouncesLeft <= 0) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    private static void spawnSplitProjectiles(Level level, Entity original, Vec3 baseVelocity, int remainingBounces, double magicBoost) {
        Vec3 leftVel = baseVelocity.yRot((float) Math.toRadians(30));
        Vec3 rightVel = baseVelocity.yRot((float) Math.toRadians(-30));

        if (original instanceof LargeFireball oldFB) {
            spawnChildFireball(level, oldFB, leftVel, remainingBounces, magicBoost);
            spawnChildFireball(level, oldFB, rightVel, remainingBounces, magicBoost);
        } else if (original instanceof SwordProjectileEntity oldSoul) {
            spawnChildSoulBolt(level, oldSoul, leftVel, remainingBounces, magicBoost);
            spawnChildSoulBolt(level, oldSoul, rightVel, remainingBounces, magicBoost);
        }
    }

    private static void spawnChildFireball(Level level, LargeFireball parent, Vec3 velocity, int remainingBounces, double magicBoost) {
        int explosionPower = (int) Math.max(1, 1 + Math.round(magicBoost));
        LargeFireball child = new LargeFireball(level, (Player) parent.getOwner(), velocity.x, velocity.y, velocity.z, explosionPower);
        child.setPos(parent.getX(), parent.getY(), parent.getZ());
        child.setDeltaMovement(velocity);

        if (remainingBounces > 0) {
            trackedEntities.put(child, new TrackerData(remainingBounces, false, magicBoost));
        }
        level.addFreshEntity(child);
    }

    private static void spawnChildSoulBolt(Level level, SwordProjectileEntity parent, Vec3 velocity, int remainingBounces, double magicBoost) {
        EntityType<SwordProjectileEntity> projectileType = (EntityType<SwordProjectileEntity>) (EntityType<?>) net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(new net.minecraft.resources.ResourceLocation("minecraftcalamity", "dynamic_projectile"));

        SwordProjectileEntity child = new SwordProjectileEntity(projectileType, level);
        child.setPos(parent.getX(), parent.getY(), parent.getZ());

        child.setYRot(parent.getYRot());
        child.setXRot(parent.getXRot());
        child.yRotO = parent.getYRot();
        child.xRotO = parent.getXRot();

        child.setProjectileColor(0x00ffe1);
        child.setParticleConfig("minecraft:soul", 0.1D, false, false);

        double baseDamage = 5.0D;
        double finalDamage = baseDamage * (1.0D + magicBoost);
        child.setStats(finalDamage, 0.0D, 0.0D, false, 10.0F, null, 0, 0);

        child.setDeltaMovement(velocity);

        if (remainingBounces > 0) {
            trackedEntities.put(child, new TrackerData(remainingBounces, false, magicBoost));
        }
        level.addFreshEntity(child);
    }

}