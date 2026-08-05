package crab.mods.minecraftcalamity.items;

import crab.mods.minecraftcalamity.items.potion.ModPotions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "minecraftcalamity", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AlchemyShenanigans {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof Creeper creeper) {
            ItemStack heldItem = event.getItemStack();

            if (heldItem.is(Items.GLASS_BOTTLE)) {
                Player player = event.getEntity();

                player.level().playSound(
                        null,
                        creeper.blockPosition(),
                        SoundEvents.COW_MILK,
                        SoundSource.PLAYERS,
                        1.0F, 1.0F
                );
                player.level().playSound(
                        null,
                        creeper.blockPosition(),
                        SoundEvents.CREEPER_PRIMED,
                        SoundSource.HOSTILE,
                        0.5F, 1.5F
                );

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }

                // Create a potion item stack and apply your custom Potion to it
                ItemStack creeperMilk = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.CREEPER_MILK.get());

                if (heldItem.isEmpty()) {
                    player.setItemInHand(event.getHand(), creeperMilk);
                } else if (!player.getInventory().add(creeperMilk)) {
                    player.drop(creeperMilk, false);
                }

                player.swing(event.getHand());
                event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
                event.setCanceled(true);
            }
        }
    }
}