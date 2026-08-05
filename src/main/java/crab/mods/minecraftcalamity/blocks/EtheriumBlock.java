package crab.mods.minecraftcalamity.blocks;

import crab.mods.minecraftcalamity.blocks.entity.EtheriumBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EtheriumBlock extends BaseEntityBlock {

    public EtheriumBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        // Hides normal texture rendering so the BlockEntityRenderer can draw the Gateway effect
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EtheriumBlockEntity(pos, state);
    }
}