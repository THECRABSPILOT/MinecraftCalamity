package crab.mods.minecraftcalamity.items.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class StaffSpells {

    private static boolean bounceModifierActive = false;

    // Track active bouncing fireballs and their remaining bounce counts
    private static final ConcurrentHashMap<LargeFireball, Integer> bouncingFireballs = new ConcurrentHashMap<>();

    private final Object[][] projectiles = {
            {"fireball_core", 5},
    };

    private final Object[][] modifiers = {
            {"bounce_modifier", 5},
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
                // Register for 3 bounces dynamically inside this class handler
                bouncingFireballs.put(fireball, 3);

            }

            level.addFreshEntity(fireball);
            bounceModifierActive = false;
        }
    }

    // --- ENTIRELY SELF-CONTAINED BOUNCE ENGINE (TICK EVENT) ---

    @SubscribeEvent
    public static void onEntityTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.level.isClientSide()) {
            return;
        }

        if (bouncingFireballs.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<LargeFireball, Integer>> iterator = bouncingFireballs.entrySet().iterator();
        while (iterator.hasNext()) {
            InjectorGroupInfo.Map.Entry<LargeFireball, Integer> entry = iterator.next();
            LargeFireball fireball = entry.getKey();
            int bouncesLeft = entry.getValue();

            if (fireball == null || !fireball.isAlive() || bouncesLeft <= 0) {
                iterator.remove();
                continue;
            }

            // Check if the fireball collided horizontally or vertically via delta movement changes
            Vec3 deltaMovement = fireball.getDeltaMovement();
            boolean bounced = false;

            // Simple velocity inversion simulation upon hitting blocks/obstacles
            Vec3 position = fireball.position();
            if (!event.level.noCollision(fireball, fireball.getBoundingBox().inflate(0.1))) {
                Vec3 motion = fireball.getDeltaMovement();
                double newX = motion.x;
                double newY = motion.y;
                double newZ = motion.z;

                // Detect axis obstruction and reverse vector component for a clean bounce
                if (event.level.getBlockState(BlockPos.containing(position.x + motion.x, position.y, position.z)).isSolid()) {
                    newX = -motion.x * 0.8; // 80% energy retention bounce
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
                    fireball.setDeltaMovement(newX, newY, newZ);
                    entry.setValue(bouncesLeft - 1);

                    if (bouncesLeft - 1 <= 0) {
                        iterator.remove();
                    }
                }
            }
        }
    }
}