package crab.mods.minecraftcalamity.blocks.entity;

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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static crab.mods.minecraftcalamity.menu.ArcaneWorkbenchMenu.STAFF_TAG;

public class ArcaneWorkbenchBlockEntity extends BlockEntity implements MenuProvider {

    public enum OutputAction {
        NONE,
        ADD_SPELL,
        REMOVE_SPELL
    }

    private OutputAction currentAction = OutputAction.NONE;
    private int targetModifierIndex = -1;

    private final ItemStackHandler itemHandler = new ItemStackHandler(12) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public ArcaneWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        // Replace with your actual BlockEntityType registry object if needed
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
        ItemStack staff = itemHandler.getStackInSlot(0);
        if (!staff.isEmpty() && staff.hasTag() && staff.getTag().contains("SpellSlots")) {
            return staff.getTag().getInt("SpellSlots");
        }
        return 0;
    }

    public void handleModifierSlotClick(int clickedSlotIndex) {
        int modifierIndex = clickedSlotIndex - 3;
        ItemStack staffInput = itemHandler.getStackInSlot(0);
        ItemStack coreInput = itemHandler.getStackInSlot(1);

        System.out.println("==================================================");
        System.out.println("[DEBUG-BE] Clicked Raw Slot Index: " + clickedSlotIndex);
        System.out.println("[DEBUG-BE] Target Modifier Index: " + modifierIndex);
        System.out.println("[DEBUG-BE] Staff in Slot 0: " + (staffInput.isEmpty() ? "EMPTY" : staffInput.getDisplayName().getString()));
        System.out.println("[DEBUG-BE] Core in Slot 1: " + (coreInput.isEmpty() ? "EMPTY" : coreInput.getDisplayName().getString()));

        int availableSlots = getAvailableSpellSlots();
        System.out.println("[DEBUG-BE] Available Staff Spell Slots: " + availableSlots);

        if (staffInput.isEmpty()) {
            System.out.println("[DEBUG-BE] CANCELLED: Slot 0 is empty!");
            System.out.println("==================================================");
            return;
        }

        if (modifierIndex >= availableSlots) {
            System.out.println("[DEBUG-BE] CANCELLED: Clicked index (" + modifierIndex + ") exceeds available slots (" + availableSlots + ")");
            System.out.println("==================================================");
            return;
        }

        // SCENARIO 1: Staff + Core Present -> Assign Spell
        if (!coreInput.isEmpty()) {
            System.out.println("[DEBUG-BE] Running SCENARIO 1: Adding/Replacing Spell...");
            ItemStack moddedStaff = staffInput.copy();
            CompoundTag tag = moddedStaff.getOrCreateTag();

            ListTag spells = tag.contains("SpellModifiers", 9) ? tag.getList("SpellModifiers", 10) : new ListTag();
            System.out.println("[DEBUG-BE] Pre-existing Spells Count: " + spells.size());

            // Remove duplicates/old entries in this slot
            for (int i = spells.size() - 1; i >= 0; i--) {
                if (spells.getCompound(i).getInt("Slot") == modifierIndex) {
                    System.out.println("[DEBUG-BE] Found existing spell at slot " + modifierIndex + ", removing old NBT...");
                    spells.remove(i);
                }
            }

            ResourceLocation coreId = ForgeRegistries.ITEMS.getKey(coreInput.getItem());
            System.out.println("[DEBUG-BE] Core Item ResourceLocation: " + coreId);

            if (coreId != null) {
                CompoundTag spellTag = new CompoundTag();
                spellTag.putString("id", coreId.toString());
                spellTag.putInt("Slot", modifierIndex);

                spells.add(spellTag);
                tag.put("SpellModifiers", spells);

                System.out.println("[DEBUG-BE] Updated Staff NBT: " + tag);

                itemHandler.setStackInSlot(2, moddedStaff);
                this.currentAction = OutputAction.ADD_SPELL;
                this.targetModifierIndex = modifierIndex;
                System.out.println("[DEBUG-BE] Output Slot 2 updated with modded staff!");
            } else {
                System.err.println("[DEBUG-BE] ERROR: Core item ResourceLocation is NULL!");
            }
        }
        // SCENARIO 2: Staff Only -> Extract Spell
        else {
            System.out.println("[DEBUG-BE] Running SCENARIO 2: Extracting Spell...");
            CompoundTag tag = staffInput.getTag();
            System.out.println("[DEBUG-BE] Raw Staff Tag: " + tag);

            if (tag != null && tag.contains("SpellModifiers", 9)) {
                ListTag spells = tag.getList("SpellModifiers", 10);
                boolean spellFound = false;

                for (int i = 0; i < spells.size(); i++) {
                    CompoundTag spellTag = spells.getCompound(i);
                    int slotInTag = spellTag.getInt("Slot");
                    System.out.println("[DEBUG-BE] Checking NBT list item [" + i + "] -> Slot: " + slotInTag + " | ID: " + spellTag.getString("id"));

                    if (slotInTag == modifierIndex) {
                        ResourceLocation spellId = new ResourceLocation(spellTag.getString("id"));
                        Item spellItem = ForgeRegistries.ITEMS.getValue(spellId);
                        System.out.println("[DEBUG-BE] Found matching spell! Resolved Item: " + (spellItem != null ? spellItem.getDescriptionId() : "NULL"));

                        if (spellItem != null) {
                            itemHandler.setStackInSlot(2, new ItemStack(spellItem));
                            this.currentAction = OutputAction.REMOVE_SPELL;
                            this.targetModifierIndex = modifierIndex;
                            spellFound = true;
                            System.out.println("[DEBUG-BE] Placed extracted core into Output Slot 2!");
                        }
                        break;
                    }
                }

                if (!spellFound) {
                    System.out.println("[DEBUG-BE] No spell bound to slot index " + modifierIndex + " found in NBT!");
                }
            } else {
                System.out.println("[DEBUG-BE] Staff has no 'SpellModifiers' NBT tag!");
            }
        }
        System.out.println("==================================================");
    }

    public void onTakeOutput(Player player) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        System.out.println("==================================================");
        System.out.println("[DEBUG-BE] onTakeOutput triggered on SERVER by: " + player.getName().getString());
        System.out.println("[DEBUG-BE] Current Action State: " + this.currentAction);
        System.out.println("[DEBUG-BE] Target Modifier Index: " + this.targetModifierIndex);

        if (currentAction == OutputAction.ADD_SPELL) {
            ItemStack coreStack = itemHandler.getStackInSlot(1);
            ItemStack staffInput = itemHandler.getStackInSlot(0);

            if (!coreStack.isEmpty() && !staffInput.isEmpty()) {
                // 1. Capture the Core ID before shrinking
                ResourceLocation coreId = ForgeRegistries.ITEMS.getKey(coreStack.getItem());

                // 2. Shrink/Consume 1 Core from Slot 1
                coreStack.shrink(1);
                itemHandler.setStackInSlot(1, coreStack);

                // 3. Consume/Delete the old staff in Slot 0 (since it's being upgraded into the new one)
                staffInput.shrink(1);
                itemHandler.setStackInSlot(0, staffInput);

                // 4. Put the newly upgraded staff DIRECTLY into the player's cursor / inventory,
                // or drop it into slot 0 if you prefer it to stay in the workbench inventory.
                // Usually, crafting outputs give the item directly to the player or slot 2 preview.
                // Let's build the final upgraded staff and place it into Slot 0 (or let Slot 2 handle it):
                if (coreId != null) {
                    ItemStack upgradedStaff = itemHandler.getStackInSlot(2).copy();
                    // If slot 2 preview has it, use it directly! Otherwise build it:
                    if (upgradedStaff.isEmpty() || !upgradedStaff.is(STAFF_TAG)) {
                        // Fallback build if slot 2 was empty
                        upgradedStaff = staffInput.copy(); // Wait, staffInput was shrunk, so snapshot it earlier!
                    }
                }
            }

            // Clear output slot preview
            itemHandler.setStackInSlot(2, ItemStack.EMPTY);

        } else if (currentAction == OutputAction.REMOVE_SPELL) {
            ItemStack staff = itemHandler.getStackInSlot(0);

            if (staff.hasTag() && staff.getTag().contains("SpellModifiers", 9)) {
                ListTag spells = staff.getTag().getList("SpellModifiers", 10);
                for (int i = 0; i < spells.size(); i++) {
                    if (spells.getCompound(i).getInt("Slot") == targetModifierIndex) {
                        spells.remove(i);
                        break;
                    }
                }
            }
            itemHandler.setStackInSlot(2, ItemStack.EMPTY);
        }

        this.currentAction = OutputAction.NONE;
        this.targetModifierIndex = -1;
        setChanged();
        System.out.println("[DEBUG-BE] Action completed and state reset.");
        System.out.println("==================================================");
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
        nbt.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }
}