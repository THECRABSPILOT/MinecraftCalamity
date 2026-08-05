package crab.mods.minecraftcalamity.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class DrillItem extends PickaxeItem {
    private final int radius;

    public DrillItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, int radius, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
        this.radius = radius;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0F) {
            HitResult hit = miner.pick(5.0D, 0.0F, false);
            if (hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult blockHit) {
                Direction face = blockHit.getDirection();
                breakExtraBlocks(level, pos, face, miner, stack);
            }
        }
        return super.mineBlock(stack, level, state, pos, miner);
    }

    private void breakExtraBlocks(Level level, BlockPos centerPos, Direction face, LivingEntity miner, ItemStack stack) {
        int offset = radius / 2;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        int minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;

        switch (face) {
            case DOWN, UP -> {
                minX = -offset; maxX = offset;
                minZ = -offset; maxZ = offset;
            }
            case NORTH, SOUTH -> {
                minX = -offset; maxX = offset;
                minY = -offset; maxY = offset;
            }
            case WEST, EAST -> {
                minY = -offset; maxY = offset;
                minZ = -offset; maxZ = offset;
            }
        }

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    if (stack.isEmpty()) return;

                    mutablePos.set(centerPos.getX() + x, centerPos.getY() + y, centerPos.getZ() + z);
                    BlockState targetState = level.getBlockState(mutablePos);

                    if (!targetState.isAir() && targetState.getDestroySpeed(level, mutablePos) >= 0.0F) {
                        if (isCorrectToolForDrops(stack, targetState)) {
                            level.destroyBlock(mutablePos, true, miner);
                            stack.hurtAndBreak(1, miner, (entity) -> entity.broadcastBreakEvent(miner.getUsedItemHand()));
                        }
                    }
                }
            }
        }
    }
}