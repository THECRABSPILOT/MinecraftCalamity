package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FireballBounceHandler {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        // 1. HANDLE SPLINTER PROJECTILES (Small Fireballs)
        if (event.getEntity() instanceof SmallFireball smallFireball) {
            CompoundTag data = smallFireball.getPersistentData();

            if (data.getBoolean("IsSplinter")) {
                HitResult hit = event.getRayTraceResult();
                if (hit.getType() == HitResult.Type.ENTITY && hit instanceof EntityHitResult entityHit) {
                    if (entityHit.getEntity() instanceof LivingEntity target) {
                        CompoundTag targetData = target.getPersistentData();
                        targetData.putBoolean("CalamitySplintered", true);
                        targetData.putInt("SplinterTicksRemaining", 100); // 5 seconds of 2 damage/sec

                        // Prevent/extinguish fire immediately if applied by the small fireball impact
                        if (!target.level().isClientSide()) {
                            target.clearFire();
                        }
                    }
                }
                return;
            }
        }

        // 2. HANDLE CUSTOM WEAPON PROJECTILES (Large Fireballs - Bounce & Split)
        // Only run this if the fireball explicitly has our custom NBT tag (prevents affecting Cave Wizards and Ghasts)
        if (event.getEntity() instanceof LargeFireball fireball) {
            CompoundTag data = fireball.getPersistentData();

            if (!data.getBoolean("IsCalamityWeaponFireball")) {
                return; // Let Cave Wizards, Ghasts, and vanilla fireballs act completely normally!
            }

            boolean isBouncy = data.getBoolean("IsBouncy");
            int bounces = data.getInt("BouncesLeft");

            boolean isSplit = data.getBoolean("IsSplit");
            boolean hasAlreadySplit = data.getBoolean("HasAlreadySplit");

            HitResult hit = event.getRayTraceResult();

            if (hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult blockHit) {

                // Handle Split on Block Impact (Only if it hasn't split yet)
                if (isSplit && !hasAlreadySplit) {
                    data.putBoolean("HasAlreadySplit", true);

                    if (!fireball.level().isClientSide()) {
                        Vec3 pos = fireball.position();
                        Vec3 vel = fireball.getDeltaMovement();
                        LivingEntity owner = fireball.getOwner() instanceof LivingEntity liv ? liv : null;

                        for (int i = -1; i <= 1; i += 2) {
                            LargeFireball splitProj;

                            if (owner != null) {
                                splitProj = new LargeFireball(fireball.level(), owner, vel.x, vel.y, vel.z, 1);
                            } else {
                                splitProj = EntityType.FIREBALL.create(fireball.level());
                                if (splitProj != null) {
                                    splitProj.setPos(pos.x, pos.y, pos.z);
                                    splitProj.xPower = vel.x;
                                    splitProj.yPower = vel.y;
                                    splitProj.zPower = vel.z;
                                } else {
                                    continue;
                                }
                            }

                            splitProj.setPos(pos.x, pos.y, pos.z);

                            Vec3 rotatedVel = new Vec3(vel.x * 0.8 - (i * 0.3), vel.y, vel.z * 0.8 + (i * 0.3));
                            splitProj.setDeltaMovement(rotatedVel);

                            CompoundTag splitData = splitProj.getPersistentData();
                            splitData.putBoolean("IsCalamityWeaponFireball", true);
                            splitData.putBoolean("IsSplit", true);
                            splitData.putBoolean("HasAlreadySplit", true);

                            if (isBouncy) {
                                splitData.putBoolean("IsBouncy", true);
                                splitData.putInt("BouncesLeft", bounces);
                            }

                            fireball.level().addFreshEntity(splitProj);
                        }
                    }
                }

                // Handle Bounce
                if (isBouncy && bounces > 0) {
                    data.putInt("BouncesLeft", bounces - 1);

                    Vec3 normal = Vec3.atLowerCornerOf(blockHit.getDirection().getNormal());
                    Vec3 currentVel = fireball.getDeltaMovement();
                    double dot = currentVel.dot(normal);
                    Vec3 reflected = currentVel.subtract(normal.scale(2 * dot)).scale(0.85);

                    fireball.xPower = reflected.x * 0.1;
                    fireball.yPower = reflected.y * 0.1;
                    fireball.zPower = reflected.z * 0.1;
                    fireball.setDeltaMovement(reflected);

                    Vec3 hitPos = blockHit.getLocation();
                    fireball.setPos(
                            hitPos.x + (normal.x * 0.3),
                            hitPos.y + (normal.y * 0.3),
                            hitPos.z + (normal.z * 0.3)
                    );

                    fireball.level().playSound(null, fireball.getX(), fireball.getY(), fireball.getZ(),
                            SoundEvents.SLIME_BLOCK_FALL, SoundSource.PLAYERS, 1.0F, 1.2F);

                    event.setCanceled(true);
                }
            }
        }
    }

    // 3. TICK HANDLER FOR SPLINTER DOT DAMAGE (2 damage per second)
    @SubscribeEvent
    public static void onEntityLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        CompoundTag data = entity.getPersistentData();
        if (data.getBoolean("CalamitySplintered")) {
            int ticks = data.getInt("SplinterTicksRemaining");
            if (ticks > 0) {
                data.putInt("SplinterTicksRemaining", ticks - 1);

                // Deals 2 damage (1 full heart) every 20 ticks (1 second)
                if (ticks % 20 == 0) {
                    entity.hurt(entity.level().damageSources().generic(), 2.0F);
                }
            } else {
                data.putBoolean("CalamitySplintered", false);
            }
        }
    }
}