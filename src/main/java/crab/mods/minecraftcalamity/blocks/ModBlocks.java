package crab.mods.minecraftcalamity.blocks;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MinecraftCalamity.MODID);

    // DropExperienceBlock handles dropping XP when mined (like Coal or Diamond ore)
    public static final RegistryObject<Block> CALAMITITE_ORE = BLOCKS.register("calamitite_ore",
            () -> new DropExperienceBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .requiresCorrectToolForDrops()
                            .strength(3.0F, 3.0F), // Hardness & Resistance
                    ConstantInt.of(2) // XP dropped on mining
            ));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}