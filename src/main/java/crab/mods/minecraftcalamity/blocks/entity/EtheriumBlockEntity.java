package crab.mods.minecraftcalamity.blocks.entity;

import crab.mods.minecraftcalamity.blocks.entity.ModBlockEntities; // Replace with your BlockEntity registration
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EtheriumBlockEntity extends BlockEntity {
    public EtheriumBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ETHERIUM_BE.get(), pos, state);
    }
}