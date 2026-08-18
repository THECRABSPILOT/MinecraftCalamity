package crab.mods.minecraftcalamity.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;

public interface IAccessoryInventory extends INBTSerializable<CompoundTag> {
    ItemStackHandler getInventory();
}