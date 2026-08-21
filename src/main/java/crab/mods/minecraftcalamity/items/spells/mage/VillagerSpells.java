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

    private static final Object[][] SPELL_DATA = {
            {"summon_golem", 50, 30},
            {"heal", 100, 60}
    };

    public Object[][] getspelldata() {
        return SPELL_DATA;
    }

    public void summon_golem(Player player, Level level, int spellLevel) {
        if (level.isClientSide()) {
            level.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.WITHER_SPAWN,
                    net.minecraft.sounds.SoundSource.HOSTILE,
                    1.0F,
                    1.0F
            );
            return;
        }

        Vec3 lookDirection = player.getLookAngle();
        double distanceBehind = 2.0;

        double targetX = player.getX() - (lookDirection.x * distanceBehind);
        double targetY = player.getY();
        double targetZ = player.getZ() - (lookDirection.z * distanceBehind);

        IronGolem ironGolem = EntityType.IRON_GOLEM.create(level);

        if (ironGolem != null) {
            ironGolem.moveTo(targetX, targetY, targetZ, player.getYRot(), player.getXRot());
            ironGolem.setPlayerCreated(true);

            ironGolem.finalizeSpawn(
                    (ServerLevel) level,
                    level.getCurrentDifficultyAt(player.blockPosition()),
                    MobSpawnType.MOB_SUMMONED,
                    null,
                    null
            );

            level.addFreshEntity(ironGolem);

            // Base lifetime: 60s (1200 ticks). Adds +10s (+200 ticks) per level above level 1.
            int durationSeconds = 60 + (Math.max(1, spellLevel) - 1) * 10;
            int durationTicks = durationSeconds * 20;

            CompoundTag customData = ironGolem.getPersistentData();
            long despawnTick = level.getGameTime() + durationTicks;
            customData.putLong("DespawnTick", despawnTick);

            ironGolem.setCustomName(Component.literal("§eGolem §7(" + durationSeconds + "s)"));
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


                if (ticksRemaining <= 0) {
                    golem.discard();
                    return;
                }


                if (currentTick % 20 == 0) {
                    long secondsLeft = ticksRemaining / 20;

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
