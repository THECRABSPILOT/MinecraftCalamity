package crab.mods.minecraftcalamity.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ManaCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<PlayerMana> PLAYER_MANA = CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerMana instance = null;
    private final LazyOptional<PlayerMana> optional = LazyOptional.of(this::getOrCreateInstance);

    private PlayerMana getOrCreateInstance() {
        if (this.instance == null) {
            this.instance = new PlayerMana();
        }
        return this.instance;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == PLAYER_MANA ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        getOrCreateInstance().saveNBT(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        getOrCreateInstance().loadNBT(nbt);
    }
}
