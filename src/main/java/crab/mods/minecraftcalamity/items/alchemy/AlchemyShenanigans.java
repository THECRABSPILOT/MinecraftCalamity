package crab.mods.minecraftcalamity.items.alchemy;

import crab.mods.minecraftcalamity.items.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "minecraftcalamity", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AlchemyShenanigans {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Level level = event.getLevel();

        if (event.getTarget() instanceof Creeper creeper) {
            ItemStack heldItem = event.getItemStack();

            if (heldItem.is(Items.GLASS_BOTTLE)) {
                Player player = event.getEntity();

                level.playSound(
                        null,
                        creeper.blockPosition(),
                        SoundEvents.COW_MILK,
                        SoundSource.PLAYERS,
                        1.0F, 1.0F
                );
                level.playSound(
                        null,
                        creeper.blockPosition(),
                        SoundEvents.CREEPER_PRIMED,
                        SoundSource.HOSTILE,
                        0.5F, 1.5F
                );

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }

                ItemStack creeperMilk = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.CREEPER_MILK.get());

                if (heldItem.isEmpty()) {
                    player.setItemInHand(event.getHand(), creeperMilk);
                } else if (!player.getInventory().add(creeperMilk)) {
                    player.drop(creeperMilk, false);
                }

                player.swing(event.getHand());
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                event.setCanceled(true);
            }
        } else if (event.getTarget() instanceof Shulker shulker) {
            ItemStack heldItem = event.getItemStack();

            // Fixed: Added .get() to extract the Item from the RegistryObject container
            if (heldItem.is(ModItems.SCOOPER.get())) {
                Player player = event.getEntity();

                level.playSound(
                        null,
                        shulker.blockPosition(),
                        SoundEvents.BEEHIVE_SHEAR,
                        SoundSource.PLAYERS,
                        1.0F, 1.0F
                );
                level.playSound(
                        null,
                        shulker.blockPosition(),
                        SoundEvents.SHULKER_HURT,
                        SoundSource.HOSTILE,
                        0.5F, 1.5F
                );

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }

                ItemStack shulkerMeat = ModItems.SHULKER_MEAT.get().getDefaultInstance();

                if (heldItem.isEmpty()) {
                    player.setItemInHand(event.getHand(), shulkerMeat);
                } else if (!player.getInventory().add(shulkerMeat)) {
                    player.drop(shulkerMeat, false);
                }

                // Execute block replacement and entity removal on the server thread
                if (!level.isClientSide()) {
                    // Fixed: Inverted the attachment face direction so the box orientation doesn't flip
                    Direction shulkerFacing = shulker.getAttachFace().getOpposite();

                    BlockState boxState = Blocks.SHULKER_BOX.defaultBlockState()
                            .setValue(ShulkerBoxBlock.FACING, shulkerFacing);

                    level.setBlockAndUpdate(shulker.blockPosition(), boxState);

                    // Fixed: Moved outside the conditional to ensure entity cleanup runs reliably
                    shulker.discard();
                }

                player.swing(event.getHand());
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                event.setCanceled(true);
            }
        }
    }
}
