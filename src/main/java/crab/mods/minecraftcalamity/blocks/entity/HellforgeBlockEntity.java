package crab.mods.minecraftcalamity.blocks.entity;

import crab.mods.minecraftcalamity.menu.HellforgeMenu;
import crab.mods.minecraftcalamity.recipe.HellforgeRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class HellforgeBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0, 1 -> true;
                case 2 -> ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
                case 3 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    // Smelting properties
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200; // 10 seconds total smelting time per item
    private int litTime = 0;
    private int litDuration = 0;

    public HellforgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HELLFORGE_BE.get(), pos, state);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> HellforgeBlockEntity.this.progress;
                    case 1 -> HellforgeBlockEntity.this.maxProgress;
                    case 2 -> HellforgeBlockEntity.this.litTime;
                    case 3 -> HellforgeBlockEntity.this.litDuration;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> HellforgeBlockEntity.this.progress = value;
                    case 1 -> HellforgeBlockEntity.this.maxProgress = value;
                    case 2 -> HellforgeBlockEntity.this.litTime = value;
                    case 3 -> HellforgeBlockEntity.this.litDuration = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.minecraftcalamity.hellforge");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HellforgeMenu(containerId, playerInventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("hellforge.progress", progress);
        tag.putInt("hellforge.litTime", litTime);
        tag.putInt("hellforge.litDuration", litDuration);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        progress = tag.getInt("hellforge.progress");
        litTime = tag.getInt("hellforge.litTime");
        litDuration = tag.getInt("hellforge.litDuration");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        boolean isLit = this.litTime > 0;

        if (this.litTime > 0) {
            this.litTime--;
        }

        if (hasRecipe()) {
            // Consume fuel if not already burning
            if (!isLit && canBurnFuel()) {
                ItemStack fuelStack = itemHandler.getStackInSlot(2);
                this.litTime = ForgeHooks.getBurnTime(fuelStack, RecipeType.SMELTING);
                this.litDuration = this.litTime;
                if (this.litTime > 0) {
                    fuelStack.shrink(1);
                    setChanged(level, pos, state);
                }
            }

            // Increment progress if burning
            if (this.litTime > 0) {
                this.progress++;
                if (this.progress >= this.maxProgress) {
                    craftItem();
                    this.progress = 0;
                }
                setChanged(level, pos, state);
            }
        } else {
            this.progress = 0;
            setChanged(level, pos, state);
        }
    }

    private boolean canBurnFuel() {
        return ForgeHooks.getBurnTime(itemHandler.getStackInSlot(2), RecipeType.SMELTING) > 0;
    }

    private boolean hasRecipe() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Optional<HellforgeRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(HellforgeRecipe.Type.INSTANCE, inventory, level);

        return recipe.isPresent()
                && canInsertAmountIntoOutput(inventory)
                && canInsertItemIntoOutput(inventory, recipe.get().getResultItem(level.registryAccess()));
    }

    private void craftItem() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Optional<HellforgeRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(HellforgeRecipe.Type.INSTANCE, inventory, level);

        if (recipe.isPresent()) {
            this.itemHandler.extractItem(0, 1, false);
            this.itemHandler.extractItem(1, 1, false);
            ItemStack result = recipe.get().getResultItem(level.registryAccess());
            this.itemHandler.setStackInSlot(3, new ItemStack(result.getItem(),
                    this.itemHandler.getStackInSlot(3).getCount() + result.getCount()));
        }
    }

    private boolean canInsertItemIntoOutput(SimpleContainer inventory, ItemStack item) {
        return inventory.getItem(3).getItem() == item.getItem() || inventory.getItem(3).isEmpty();
    }

    private boolean canInsertAmountIntoOutput(SimpleContainer inventory) {
        return inventory.getItem(3).getMaxStackSize() >= inventory.getItem(3).getCount() + 1;
    }
}