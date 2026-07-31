package crab.mods.minecraftcalamity.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SwordInStoneBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);

    public SwordInStoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Server-side logic only to prevent duplicate execution/client desync
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Only trigger the check for the main hand to avoid firing twice (once per hand)
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }

        // Check player health pool (max health, current health, or absorption hearts)
        float currentHealth = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float absorption = player.getAbsorptionAmount();

        boolean isStronger = maxHealth > 20.0F || absorption > 0.0F || currentHealth > 20.0F;

        if (isStronger) {
            // Give them the enchanted sword item
            boolean givenSuccess = player.getInventory().add(crab.mods.minecraftcalamity.items.ModItems.ENCHANTED_SWORD.get().getDefaultInstance());

            if (!givenSuccess) {
                // Drop it on the ground if inventory is full
                player.drop(crab.mods.minecraftcalamity.items.ModItems.ENCHANTED_SWORD.get().getDefaultInstance(), false);
            }

            // Break/remove the block (set to air)
            level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);

            // Play level-up sound effect
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);

        } else {
            // Display the Breath of the Wild style rejection message above the hotbar
            player.displayClientMessage(Component.literal("Come back when you're a bit stronger..."), true);
        }

        return InteractionResult.CONSUME;
    }
}