package crab.mods.minecraftcalamity.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DiviniumBlockEntity extends BlockEntity {
    public DiviniumBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIVINIUM_BE.get(), pos, state);
    }
}