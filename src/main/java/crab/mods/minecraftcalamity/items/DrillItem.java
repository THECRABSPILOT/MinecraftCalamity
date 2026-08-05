package crab.mods.minecraftcalamity.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DrillItem extends PickaxeItem {
    private final int radius;
    private static boolean isAoeMining = false;

    public DrillItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, int radius, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
        this.radius = radius;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0F && !isAoeMining) {
            Direction face = Direction.orderedByNearest(miner)[0];
            if (face != null) {
                isAoeMining = true;
                try {
                    breakExtraBlocks(level, pos, face, miner, stack);
                } finally {
                    isAoeMining = false;
                }
            }
        }
        return super.mineBlock(stack, level, state, pos, miner);
    }

    private void breakExtraBlocks(Level level, BlockPos centerPos, Direction face, LivingEntity miner, ItemStack stack) {
        int offset = radius / 2;
        int minX = -offset, maxX = offset;
        int minY = -offset, maxY = offset;
        int minZ = -offset, maxZ = offset;

        switch (face) {
            case DOWN, UP -> {
                minY = 0;
                maxY = 0;
            }
            case NORTH, SOUTH -> {
                minZ = 0;
                maxZ = 0;
            }
            case WEST, EAST -> {
                minX = 0;
                maxX = 0;
            }
        }

        int blocksBroken = 0;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    if (stack.isEmpty()) return;

                    BlockPos targetPos = centerPos.offset(x, y, z);
                    BlockState targetState = level.getBlockState(targetPos);

                    if (!targetState.isAir() && targetState.getDestroySpeed(level, targetPos) >= 0.0F) {
                        level.destroyBlock(targetPos, true, miner);
                        blocksBroken++;
                    }
                }
            }
        }

        if (blocksBroken > 0) {
            stack.hurtAndBreak(1, miner, (entity) -> entity.broadcastBreakEvent(miner.getUsedItemHand()));
        }
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        // Returning true here suppresses the default arm-swing animation entirely when using/swinging the item
        return true;
    }
}