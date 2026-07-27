package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NightRaidHandler {

    private static boolean hasCheckedTonight = false;
    private static final Random RANDOM = new Random();

    // Register a debug command /trigger_raid to test anytime
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("trigger_raid")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    ServerLevel level = context.getSource().getLevel();
                    context.getSource().sendSuccess(() -> Component.literal("§a[Debug] Forcing Raid Trigger..."), true);
                    triggerVillageRaid(level);
                    return 1;
                }));
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !(event.level instanceof ServerLevel level)) {
            return;
        }

        long dayTime = level.getDayTime() % 24000;

        // Reset check during daytime (0 to 12000)
        if (dayTime < 12000) {
            hasCheckedTonight = false;
            return;
        }

        // Check at dusk (between 12500 and 13000)
        if (!hasCheckedTonight && dayTime >= 12500 && dayTime < 13000) {
            hasCheckedTonight = true;

            // 1 in 4 chance (25%)
            if (RANDOM.nextInt(4) == 0) {
                triggerVillageRaid(level);
            }
        }
    }

    public static void triggerVillageRaid(ServerLevel level) {
        if (level.players().isEmpty()) return;

        for (ServerPlayer player : level.players()) {
            BlockPos playerPos = player.blockPosition();

            // Find village structure
            BlockPos villagePos = level.findNearestMapStructure(
                    StructureTags.VILLAGE, playerPos, 100, true);

            // Fallback: Use player position directly if null
            if (villagePos == null) {
                villagePos = playerPos;
            }

            boolean isIllagerRaid = RANDOM.nextBoolean();

            if (isIllagerRaid) {
                spawnIllagerRaid(level, villagePos);
                player.sendSystemMessage(Component.literal("§c[Warning] An Illager raid is attacking a nearby village!"));
            } else {
                spawnZombieRaid(level, villagePos);
                player.sendSystemMessage(Component.literal("§4[Warning] A massive Zombie siege has begun at a nearby village!"));
            }

            break; // Stop after executing once per check
        }
    }

    private static void spawnIllagerRaid(ServerLevel level, BlockPos center) {
        int pillagers = 8 + RANDOM.nextInt(5);
        int vindicators = 4 + RANDOM.nextInt(4);
        int evokers = 1 + RANDOM.nextInt(2);
        int ravagers = RANDOM.nextInt(2);

        spawnMobsAround(level, center, EntityType.PILLAGER, pillagers);
        spawnMobsAround(level, center, EntityType.VINDICATOR, vindicators);
        spawnMobsAround(level, center, EntityType.EVOKER, evokers);
        spawnMobsAround(level, center, EntityType.RAVAGER, ravagers);
    }

    private static void spawnZombieRaid(ServerLevel level, BlockPos center) {
        int zombies = 20 + RANDOM.nextInt(10);
        spawnMobsAround(level, center, EntityType.ZOMBIE, zombies);
    }

    private static <T extends net.minecraft.world.entity.Mob> void spawnMobsAround(
            ServerLevel level, BlockPos center, EntityType<T> entityType, int count) {

        for (int i = 0; i < count; i++) {
            int radius = 12 + RANDOM.nextInt(12);
            double angle = RANDOM.nextDouble() * Math.PI * 2;

            int x = center.getX() + (int) (Math.cos(angle) * radius);
            int z = center.getZ() + (int) (Math.sin(angle) * radius);

            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

            BlockPos spawnPos = new BlockPos(x, y, z);

            T mob = entityType.create(level);
            if (mob != null) {
                mob.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, RANDOM.nextFloat() * 360F, 0.0F);
                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.EVENT, null, null);

                level.addFreshEntity(mob);
            }
        }
    }
}