package crab.mods.minecraftcalamity.items;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkHooks;

import crab.mods.minecraftcalamity.menu.WeaverMenu;

public class WeaverBookItem extends Item {

    public WeaverBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            if (player instanceof ServerPlayer serverPlayer && !(player instanceof FakePlayer)) {

                final BlockPos clickPos = player.blockPosition();

                // Use the standard vanilla MenuProvider interface
                MenuProvider menuProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("screen.minecraftcalamity.weaver_book");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                        return new WeaverMenu(
                                containerId,
                                playerInventory,
                                playerInventory.player.level().getBlockEntity(clickPos),
                                new SimpleContainerData(4)
                        );
                    }
                };

                // FIX: Pass the extra buffer data directly into the third parameter of openScreen
                // Forge automatically handles syncing this to the client-side constructor
                NetworkHooks.openScreen(serverPlayer, menuProvider, buf -> buf.writeBlockPos(clickPos));
            }
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());

    }

}