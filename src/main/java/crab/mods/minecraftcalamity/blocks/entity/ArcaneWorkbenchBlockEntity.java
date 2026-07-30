package crab.mods.minecraftcalamity.blocks.entity;

import crab.mods.minecraftcalamity.items.magicitems.ModularStaffItem;
import crab.mods.minecraftcalamity.items.magicitems.SpellBookItem;
import crab.mods.minecraftcalamity.menu.ArcaneWorkbenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArcaneWorkbenchBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(12) {
        @Override
        protected void onContentsChanged(int slot) {
            if (slot == 0 || slot == 1) {
                ItemStack output = getStackInSlot(2);
                if (!output.isEmpty()) {
                    setStackInSlot(2, ItemStack.EMPTY);
                    isAddingSpell = false;
                }
            }
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    // Flag to track whether we are adding a spell or extracting one
    private boolean isAddingSpell = false;

    public ArcaneWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ARCANE_WORKBENCH_BE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.minecraftcalamity.arcane_workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ArcaneWorkbenchMenu(containerId, playerInventory, this);
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

    public int getAvailableSpellSlots() {
        ItemStack item = itemHandler.getStackInSlot(0);
        if (!item.isEmpty()) {
            if (item.hasTag() && item.getTag().contains("SpellSlots")) {
                return item.getTag().getInt("SpellSlots");
            }
            if (item.getItem() instanceof ModularStaffItem) {
                return 2;
            }
            if (item.getItem() instanceof SpellBookItem book) {
                return book.getSpellSlots();
            }
        }
        return 0;
    }

    public void handleModifierSlotClick(int clickedSlotIndex) {
        int modifierIndex = clickedSlotIndex - 3;
        ItemStack magicItemInput = itemHandler.getStackInSlot(0);
        ItemStack spellInput = itemHandler.getStackInSlot(1);

        int availableSlots = getAvailableSpellSlots();

        if (magicItemInput.isEmpty() || modifierIndex >= availableSlots) {
            return;
        }

        // SCENARIO 1: Assign Spell (Preview Staff with new spell in Output Slot 2)
        if (!spellInput.isEmpty()) {
            isAddingSpell = true;
            ItemStack modifiedItem = magicItemInput.copy();
            CompoundTag tag = modifiedItem.getOrCreateTag();

            ListTag spells = tag.contains("SpellModifiers", 9) ? tag.getList("SpellModifiers", 10) : new ListTag();

            for (int i = spells.size() - 1; i >= 0; i--) {
                if (spells.getCompound(i).getInt("Slot") == modifierIndex) {
                    spells.remove(i);
                }
            }

            ResourceLocation spellIdKey = ForgeRegistries.ITEMS.getKey(spellInput.getItem());
            if (spellIdKey != null) {
                CompoundTag spellTag = new CompoundTag();
                spellTag.putString("id", spellIdKey.toString());
                spellTag.putInt("Slot", modifierIndex);

                spells.add(spellTag);
                tag.put("SpellModifiers", spells);

                CompoundTag spellsTag = tag.contains("Spells", 10) ? tag.getCompound("Spells") : new CompoundTag();
                spellsTag.putString("Slot_" + modifierIndex, spellIdKey.getPath());
                tag.put("Spells", spellsTag);

                // Place preview into output slot 2
                itemHandler.setStackInSlot(2, modifiedItem);
                setChanged();
            }
        }
        // SCENARIO 2: Extract Spell (Put ONLY the extracted Spell item in Output Slot 2, and update Slot 0 in-place)
        else {
            isAddingSpell = false;
            CompoundTag tag = magicItemInput.getTag();
            if (tag != null && tag.contains("SpellModifiers", 9)) {
                ListTag spells = tag.getList("SpellModifiers", 10);
                for (int i = 0; i < spells.size(); i++) {
                    CompoundTag spellTag = spells.getCompound(i);
                    if (spellTag.getInt("Slot") == modifierIndex) {
                        ResourceLocation spellId = new ResourceLocation(spellTag.getString("id"));
                        Item spellItem = ForgeRegistries.ITEMS.getValue(spellId);

                        if (spellItem != null) {
                            // 1. Put ONLY the extracted spell item into Output Slot 2
                            itemHandler.setStackInSlot(2, new ItemStack(spellItem));

                            // 2. Modify Slot 0's item directly (remove the spell from its NBT) without moving it out of Slot 0
                            CompoundTag modTag = magicItemInput.getOrCreateTag();
                            ListTag newSpells = new ListTag();
                            for (int j = 0; j < spells.size(); j++) {
                                if (j != i) {
                                    newSpells.add(spells.getCompound(j).copy());
                                }
                            }
                            modTag.put("SpellModifiers", newSpells);

                            if (modTag.contains("Spells", 10)) {
                                modTag.getCompound("Spells").remove("Slot_" + modifierIndex);
                            }

                            setChanged();
                        }
                        break;
                    }
                }
            }
        }
    }

    public void onTakeOutput(Player player) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        ItemStack spellInput = itemHandler.getStackInSlot(1);

        if (isAddingSpell) {
            // When adding a spell, consume the input staff/book (slot 0) and the spell core (slot 1)
            ItemStack magicInput = itemHandler.getStackInSlot(0);
            if (!magicInput.isEmpty()) {
                magicInput.shrink(1);
                itemHandler.setStackInSlot(0, magicInput);
            }
            if (!spellInput.isEmpty()) {
                spellInput.shrink(1);
                itemHandler.setStackInSlot(1, spellInput);
            }
        } else {
            // When extracting a spell, Slot 0 was already updated in-place. We do NOT consume the staff/book.
            // We just let the player take their extracted spell out of slot 2.
        }

        itemHandler.setStackInSlot(2, ItemStack.EMPTY);
        isAddingSpell = false;
        setChanged();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putBoolean("IsAddingSpell", isAddingSpell);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        if (nbt.contains("IsAddingSpell")) {
            isAddingSpell = nbt.getBoolean("IsAddingSpell");
        }
    }
}