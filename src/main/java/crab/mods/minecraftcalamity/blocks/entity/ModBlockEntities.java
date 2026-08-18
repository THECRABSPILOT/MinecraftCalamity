package crab.mods.minecraftcalamity.blocks.entity;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.blocks.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MinecraftCalamity.MODID);

    public static final RegistryObject<BlockEntityType<HellforgeBlockEntity>> HELLFORGE_BE =
            BLOCK_ENTITIES.register("hellforge_be", () ->
                    BlockEntityType.Builder.of(HellforgeBlockEntity::new,
                            ModBlocks.HELLFORGE.get()).build(null));

    public static final RegistryObject<BlockEntityType<ArcaneWorkbenchBlockEntity>> ARCANE_WORKBENCH_BE =
            BLOCK_ENTITIES.register("arcane_workbench_be", () ->
                    BlockEntityType.Builder.of(ArcaneWorkbenchBlockEntity::new,
                            ModBlocks.ARCANE_WORKBENCH.get()).build(null));


    public static final RegistryObject<BlockEntityType<PedestalBlockEntity>> PEDESTAL_BE =
            BLOCK_ENTITIES.register("pedestal_be", () ->
                    BlockEntityType.Builder.of(PedestalBlockEntity::new,
                            ModBlocks.PEDESTAL_BLOCK.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}