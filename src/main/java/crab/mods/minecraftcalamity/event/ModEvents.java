package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.accessory.AccessoryCapability;
import crab.mods.minecraftcalamity.entity.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID)
public class ModEvents {


    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverLevel) {
            BlockPos pos = event.getPos();

            // Find any slime marker at this exact broken block position and discard it
            serverLevel.getEntitiesOfClass(Slime.class, new AABB(pos),
                    slime -> slime.getPersistentData().getBoolean("OreSightMarker")).forEach(Slime::discard);
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(AccessoryCapability.ACCESSORY_CAP).isPresent()) {
                event.addCapability(
                        new ResourceLocation(MinecraftCalamity.MODID, "book"),
                        new AccessoryCapability()
                );
            }
        }
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(net.minecraftforge.event.entity.SpawnPlacementRegisterEvent event) {
        event.register(
                ModEntityTypes.CAVE_WIZARD.get(),
                net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
                net.minecraft.world.level.levelgen.Heightmap.Types.OCEAN_FLOOR,
                (entityType, level, spawnType, pos, random) ->
                        level.getRawBrightness(pos, 0) == 0 && net.minecraft.world.entity.monster.Monster.checkMonsterSpawnRules(entityType, level, spawnType, pos, random),
                net.minecraftforge.event.entity.SpawnPlacementRegisterEvent.Operation.REPLACE
        );
    }

}