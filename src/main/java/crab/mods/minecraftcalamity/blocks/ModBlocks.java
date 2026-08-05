package crab.mods.minecraftcalamity.blocks;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MinecraftCalamity.MODID);

    public static final RegistryObject<Block> CALAMITITE_ORE = BLOCKS.register("calamitite_ore",
            () -> new DropExperienceBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .requiresCorrectToolForDrops()
                            .strength(3.0F, 3.0F),
                    ConstantInt.of(2)
            ));

    // Updated to use HellforgeBlock
    public static final RegistryObject<Block> HELLFORGE = BLOCKS.register("hellforge",
            () -> new HellforgeBlock(BlockBehaviour.Properties.of()
                    .strength(3.5F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final RegistryObject<Block> ARCANE_WORKBENCH = BLOCKS.register("arcane_workbench",
            () -> new ArcaneWorkbenchBlock(BlockBehaviour.Properties.of()
                    .strength(3.5F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final RegistryObject<Block> PEDESTAL_BLOCK = BLOCKS.register("pedestal",
            () -> new crab.mods.minecraftcalamity.block.PedestalBlock(Block.Properties.of()
                    .strength(3.0f, 6.0f)
                    .noOcclusion()));

    public static final RegistryObject<Block> ETHERIUM_BLOCK = BLOCKS.register("etherium_block",
            () -> new EtheriumBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .noOcclusion()));

    public static final RegistryObject<Block> DIVINIUM_BLOCK = BLOCKS.register("divinium_block",
            () -> new DiviniumBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .noOcclusion()));

    public static final RegistryObject<Block> SWORD_IN_STONE = BLOCKS.register("sword_in_stone",
            () -> new SwordInStoneBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(4.0f, 6.0f) // Adjust hardness/resistance as desired
                    .requiresCorrectToolForDrops()

            ));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}