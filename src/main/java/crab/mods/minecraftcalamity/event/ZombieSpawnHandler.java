package crab.mods.minecraftcalamity.event;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MinecraftCalamity.MODID)
public class ZombieSpawnHandler {

    // Cooldown duration in ticks (20 ticks = 1 second)
    private static final int MINING_COOLDOWN_TICKS = 15;
    private static final String MINING_TAG = "MinecraftCalamityMiningCooldown";

    @SubscribeEvent
    public static void onZombieSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getEntity() instanceof Zombie zombie) {
            // 15% chance for a zombie to spawn with a pickaxe
            if (zombie.getRandom().nextFloat() < 0.15f) {
                ItemStack pickaxe = new ItemStack(Items.IRON_PICKAXE);
                zombie.setItemSlot(EquipmentSlot.MAINHAND, pickaxe);
                zombie.setDropChance(EquipmentSlot.MAINHAND, 0.25f);
            }
        }
    }

    @SubscribeEvent
    public static void onZombieTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Zombie zombie && !zombie.level().isClientSide()) {
            ItemStack mainHand = zombie.getItemBySlot(EquipmentSlot.MAINHAND);

            boolean isHoldingPickaxe = mainHand.getItem().toString().contains("pickaxe");
            LivingEntity target = zombie.getTarget();

            if (isHoldingPickaxe && target != null) {
                CompoundTag persistentData = zombie.getPersistentData();
                int cooldown = persistentData.getInt(MINING_TAG);

                // If on cooldown, tick down and exit
                if (cooldown > 0) {
                    persistentData.putInt(MINING_TAG, cooldown - 1);
                    return;
                }

                Vec3 eyePos = zombie.getEyePosition(1.0F);
                Vec3 targetEyePos = target.getEyePosition(1.0F);
                Vec3 dirToTarget = targetEyePos.subtract(eyePos).normalize();
                Vec3 reachEnd = eyePos.add(dirToTarget.scale(7.0D));

                // 1. Raycast check (7-block reach)
                BlockHitResult hitResult = zombie.level().clip(new ClipContext(
                        eyePos,
                        reachEnd,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        zombie
                ));

                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos targetPos = hitResult.getBlockPos();
                    if (canMine(zombie, targetPos)) {
                        mineBlock(zombie, targetPos);
                        return;
                    }
                }

                // 2. Downward Mining (Digging down to player level)
                if (target.getY() < zombie.getY() - 0.5D) {
                    BlockPos frontPos = zombie.blockPosition().relative(zombie.getDirection());
                    int yDifference = (int) Math.ceil(zombie.getY() - target.getY());

                    for (int yOffset = 0; yOffset <= Math.min(yDifference, 3); yOffset++) {
                        BlockPos downPos = frontPos.below(yOffset);
                        if (canMine(zombie, downPos)) {
                            mineBlock(zombie, downPos);
                            return;
                        }
                    }

                    // Also clear ground directly under zombie if stuck above player
                    BlockPos underFeet = zombie.blockPosition().below();
                    if (canMine(zombie, underFeet)) {
                        mineBlock(zombie, underFeet);
                        return;
                    }
                }

                // 3. Upward & Horizontal Clearance
                BlockPos frontEyePos = BlockPos.containing(eyePos.add(zombie.getLookAngle().scale(1.5D)));
                BlockPos aboveFront = frontEyePos.above();

                if (canMine(zombie, aboveFront)) {
                    mineBlock(zombie, aboveFront);
                    return;
                }

                if (canMine(zombie, frontEyePos)) {
                    mineBlock(zombie, frontEyePos);
                }
            }
        }
    }

    private static boolean canMine(Zombie zombie, BlockPos pos) {
        BlockState state = zombie.level().getBlockState(pos);
        return !state.isAir()
                && state.getDestroySpeed(zombie.level(), pos) >= 0
                && state.getDestroySpeed(zombie.level(), pos) <= 5.0f;
    }

    private static void mineBlock(Zombie zombie, BlockPos pos) {
        zombie.swing(InteractionHand.MAIN_HAND);
        zombie.level().destroyBlock(pos, true, zombie);

        // Apply mining cooldown to entity persistent data
        zombie.getPersistentData().putInt(MINING_TAG, MINING_COOLDOWN_TICKS);
    }
}