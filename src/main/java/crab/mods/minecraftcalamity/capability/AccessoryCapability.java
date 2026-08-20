package crab.mods.minecraftcalamity.capability;

import crab.mods.minecraftcalamity.menu.AccessoryMenu;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AccessoryCapability implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final Capability<IAccessoryInventory> ACCESSORY_CAP =
            CapabilityManager.get(new CapabilityToken<IAccessoryInventory>() {});

    private final ItemStackHandler inventory = new ItemStackHandler(8) {
        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            if (this.getSlots() < 8) {
                this.setSize(8);
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(AccessoryMenu.ACCESSORIES_TAG);
        }
    };

    private final LazyOptional<IAccessoryInventory> optional = LazyOptional.of(() -> new IAccessoryInventory() {
        @Override
        public ItemStackHandler getInventory() {
            return inventory;
        }

        @Override
        public CompoundTag serializeNBT() {
            return inventory.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            inventory.deserializeNBT(nbt);
        }
    });

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ACCESSORY_CAP) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return inventory.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        inventory.deserializeNBT(nbt);
    }
}