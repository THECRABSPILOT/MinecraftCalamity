package crab.mods.minecraftcalamity.blocks.entity;

import crab.mods.minecraftcalamity.blocks.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PedestalBlockEntity extends BlockEntity {
    private final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            PedestalBlockEntity.this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    public PedestalBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.PEDESTAL_BE.get(), pPos, pBlockState);
    }

    public ItemStack getDisplayedItem() {
        return inventory.getItem(0);
    }

    public void setDisplayedItem(ItemStack stack) {
        inventory.setItem(0, stack == null ? ItemStack.EMPTY : stack);
        setChanged();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        // FIX: Always put the tag into NBT, even if it's empty, so the client receives the update!
        pTag.put("DisplayedItem", inventory.getItem(0).save(new CompoundTag()));
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("DisplayedItem")) {
            inventory.setItem(0, ItemStack.of(pTag.getCompound("DisplayedItem")));
        } else {
            inventory.setItem(0, ItemStack.EMPTY);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void drops() {
        if (level != null && !level.isClientSide()) {
            Containers.dropContents(level, worldPosition, inventory);
        }
    }
}
