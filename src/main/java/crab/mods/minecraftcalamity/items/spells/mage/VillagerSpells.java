package crab.mods.minecraftcalamity.items.spells.mage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "minecraftcalamity")
public class VillagerSpells {

    // Keep data clean and structured
    private static final Object[][] SPELL_DATA = {
            {"summon_golem", 50, 30},
            {"heal", 100, 60}
    };

    public Object[][] getspelldata() {
        return SPELL_DATA;
    }

    public void summon_golem(Player player, Level level) {
        // 1. Guard check for server level execution
        if (level.isClientSide()) {
            level.playSound(
                    null,                                      // Player to exclude (null plays it for everyone)
                    player.getX(), player.getY(), player.getZ(), // Coordinates where the sound originates
                    net.minecraft.sounds.SoundEvents.WITHER_SPAWN, // The sound event
                    net.minecraft.sounds.SoundSource.HOSTILE,   // Sound category
                    1.0F,                                      // Volume
                    1.0F                                       // Pitch
            );
            return;
        }

        // 2. Vector distance offset calculation
        Vec3 lookDirection = player.getLookAngle();
        double distanceBehind = 2.0;

        double targetX = player.getX() - (lookDirection.x * distanceBehind);
        double targetY = player.getY();
        double targetZ = player.getZ() - (lookDirection.z * distanceBehind);

        // 3. Create the entity instance safely
        IronGolem ironGolem = EntityType.IRON_GOLEM.create(level);

        if (ironGolem != null) {
            // Position the golem facing the exact same orientation as the caster
            ironGolem.moveTo(targetX, targetY, targetZ, player.getYRot(), player.getXRot());

            // CRITICAL: Tells vanilla AI logic to guard the player and ignore friendly fire
            ironGolem.setPlayerCreated(true);

            // Finalize attribute maps, localized dynamic health modifiers, and gear configurations
            ironGolem.finalizeSpawn(
                    (ServerLevel) level,
                    level.getCurrentDifficultyAt(player.blockPosition()),
                    MobSpawnType.MOB_SUMMONED,
                    null,
                    null
            );

            // Inject the finalized golem into the world pipeline
            level.addFreshEntity(ironGolem);

            CompoundTag customData = ironGolem.getPersistentData();
            long despawnTick = level.getGameTime() + 1200; // 60 seconds * 20 ticks
            customData.putLong("DespawnTick", despawnTick);

// Show custom name tag above its head
            ironGolem.setCustomName(Component.literal("§eGolem §7(60s)"));
            ironGolem.setCustomNameVisible(true);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof IronGolem golem && !golem.level().isClientSide()) {
            CompoundTag data = golem.getPersistentData();

            if (data.contains("DespawnTick")) {
                long despawnTick = data.getLong("DespawnTick");
                long currentTick = golem.level().getGameTime();
                long ticksRemaining = despawnTick - currentTick;

                // 1. Despawn if time runs out
                if (ticksRemaining <= 0) {
                    golem.discard(); // or golem.kill()
                    return;
                }

                // 2. Update nametag once per second (every 20 ticks) to prevent unnecessary updates
                if (currentTick % 20 == 0) {
                    long secondsLeft = ticksRemaining / 20;

                    // Color code changes to red when under 10 seconds remaining
                    String colorCode = secondsLeft <= 10 ? "§c" : "§e";

                    golem.setCustomName(Component.literal(colorCode + "Golem §7(" + secondsLeft + "s)"));
                    golem.setCustomNameVisible(true);
                }
            }
        }
    }

    public void heal(Player player, Level level) {
        if (!player.level().isClientSide()) {
            player.setHealth(player.getMaxHealth());
        }
    }
}
