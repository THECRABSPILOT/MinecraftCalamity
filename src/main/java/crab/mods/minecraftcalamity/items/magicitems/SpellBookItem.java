package crab.mods.minecraftcalamity.items.magicitems;

import crab.mods.minecraftcalamity.capability.ManaCapabilityProvider;
import crab.mods.minecraftcalamity.capability.PlayerMana;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

public class SpellBookItem extends Item {
    private final int SpellSlots;
    private static boolean isHoldingRightClick = false;

    public SpellBookItem(Properties properties, int SpellSlots) {
        super(properties);
        this.SpellSlots = SpellSlots;
    }

    @Override
    public void verifyTagAfterLoad(CompoundTag nbt) {
        super.verifyTagAfterLoad(nbt);
        if (!nbt.contains("SpellSlots")) {
            nbt.putInt("SpellSlots", this.SpellSlots);
        }
    }

    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag nbt = super.getShareTag(stack);
        if (nbt != null && !nbt.contains("SpellSlots")) {
            nbt.putInt("SpellSlots", this.SpellSlots);
        }
        return nbt;
    }

    public int getSpellSlots() {
        return this.SpellSlots;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.getOrCreateTag().contains("SpellSlots")) {
            stack.getTag().putInt("SpellSlots", this.SpellSlots);
        }

        if (level.isClientSide()) {
            isHoldingRightClick = true;
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (level.isClientSide() && entity instanceof Player) {
            isHoldingRightClick = false;
        }
        super.releaseUsing(stack, level, entity, timeLeft);
    }

    public static boolean isHoldingRightClick() {
        return isHoldingRightClick;
    }

    public static int getSelectedSlot(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("SelectedSlot")) {
            return stack.getTag().getInt("SelectedSlot");
        }
        return 0;
    }

    public static void setSelectedSlot(ItemStack stack, int slot, int maxSlots) {
        int clampedSlot = Math.max(0, Math.min(slot, maxSlots - 1));
        stack.getOrCreateTag().putInt("SelectedSlot", clampedSlot);
    }

    // --- Dynamic Per-Slot Hotbar Cooldown Graphics Overrides ---

    @Override
    public boolean isBarVisible(ItemStack stack) {
        // Look up the active slot's cooldown using NBT tags
        if (stack.hasTag() && stack.getTag().contains("SlotCooldowns")) {
            CompoundTag cooldownsTag = stack.getTag().getCompound("SlotCooldowns");
            int activeSlot = getSelectedSlot(stack);

            if (cooldownsTag.contains("Max_" + activeSlot)) {
                long maxTicks = cooldownsTag.getLong("Max_" + activeSlot);
                // Return true only if a valid cooldown target exists
                return maxTicks > 0;
            }
        }
        return super.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("SlotCooldowns")) {
            CompoundTag cooldownsTag = stack.getTag().getCompound("SlotCooldowns");
            int activeSlot = getSelectedSlot(stack);

            if (cooldownsTag.contains("Slot_" + activeSlot) && cooldownsTag.contains("Max_" + activeSlot)) {
                long readyAtTick = cooldownsTag.getLong("Slot_" + activeSlot);
                long maxTicks = cooldownsTag.getLong("Max_" + activeSlot);

                // Use placeholder value if world reference isn't loaded inside rendering phase
                long currentWorldTicks = net.minecraft.client.Minecraft.getInstance().level != null
                        ? net.minecraft.client.Minecraft.getInstance().level.getGameTime() : 0;

                long remainingTicks = Math.max(0, readyAtTick - currentWorldTicks);

                if (remainingTicks == 0 || maxTicks <= 0) {
                    return 0;
                }

                // Standard Minecraft hotbar bar is 13 pixels wide total
                float progress = (float) remainingTicks / (float) maxTicks;
                return Math.round(13.0F * (1.0F - progress));
            }
        }
        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // Returns a distinct light aqua blue color (0x36ebab) for spell cooldown bars
        return 0x36EBAB;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        int maxSlots = stack.hasTag() && stack.getTag().contains("SpellSlots") ? stack.getTag().getInt("SpellSlots") : this.SpellSlots;
        int activeSlot = getSelectedSlot(stack);
        String activeSpell = getSpellInSlot(stack, activeSlot);

        tooltipComponents.add(Component.literal("§bSpell Slots: " + maxSlots));

        long currentTicks = level != null ? level.getGameTime() : 0;
        long remainingTicks = getRemainingCooldownTicks(stack, activeSlot, currentTicks);
        if (remainingTicks > 0) {
            tooltipComponents.add(Component.literal("§eSelected Spell: §f" + activeSpell + " §c(" + String.format("%.1f", remainingTicks / 20.0f) + "s)"));
        } else {
            tooltipComponents.add(Component.literal("§eSelected Spell: §f" + activeSpell));
        }

        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            tooltipComponents.add(Component.literal("§7--- Spell Slots ---"));
            for (int i = 0; i < maxSlots; i++) {
                String assignedSpell = getSpellInSlot(stack, i);
                long slotRemaining = getRemainingCooldownTicks(stack, i, currentTicks);
                String cooldownText = slotRemaining > 0 ? " §c[" + String.format("%.1f", slotRemaining / 20.0f) + "s]" : "";

                if (i == activeSlot) {
                    tooltipComponents.add(Component.literal("§a> Slot " + (i + 1) + " [Active]: §f" + assignedSpell + cooldownText));
                } else {
                    tooltipComponents.add(Component.literal("§7- Slot " + (i + 1) + ": §8" + assignedSpell + cooldownText));
                }
            }
        } else {
            tooltipComponents.add(Component.literal("§8Hold §7[Shift]§8 for slot details"));
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    public static void setSpellInSlot(ItemStack stack, int slot, String spellId) {
        CompoundTag tag = stack.getOrCreateTag();
        int maxSlots = tag.contains("SpellSlots") ? tag.getInt("SpellSlots") : 0;
        if (slot >= 0 && slot < maxSlots) {
            CompoundTag spellsTag = tag.getCompound("Spells");
            spellsTag.putString("Slot_" + slot, spellId);
            tag.put("Spells", spellsTag);
        }
    }

    public static String getSpellInSlot(ItemStack stack, int slot) {
        if (stack.hasTag() && stack.getTag().contains("Spells")) {
            CompoundTag spellsTag = stack.getTag().getCompound("Spells");
            String spell = spellsTag.getString("Slot_" + slot);
            if (!spell.isEmpty()) {
                return spell;
            }
        }
        return "Empty";
    }

    public static long getRemainingCooldownTicks(ItemStack stack, int slot, long currentWorldTicks) {
        if (stack.hasTag() && stack.getTag().contains("SlotCooldowns")) {
            CompoundTag cooldownsTag = stack.getTag().getCompound("SlotCooldowns");
            if (cooldownsTag.contains("Slot_" + slot)) {
                long readyAtTick = cooldownsTag.getLong("Slot_" + slot);
                return Math.max(0, readyAtTick - currentWorldTicks);
            }
        }
        return 0;
    }

    public static void setSlotCooldown(ItemStack stack, int slot, int cooldownTicks, long currentWorldTicks) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag cooldownsTag = tag.getCompound("SlotCooldowns");
        cooldownsTag.putLong("Slot_" + slot, currentWorldTicks + cooldownTicks);
        cooldownsTag.putLong("Max_" + slot, cooldownTicks); // Stores total length to evaluate bar scale fractions
        tag.put("SlotCooldowns", cooldownsTag);
    }

    public void castActiveSpell(ItemStack stack, Player player, Level level) {
        int activeSlot = getSelectedSlot(stack);
        String activeSpellId = getSpellInSlot(stack, activeSlot);

        if (activeSpellId.equals("Empty")) {
            return;
        }

        if (getRemainingCooldownTicks(stack, activeSlot, level.getGameTime()) > 0) {
            return;
        }

        net.minecraft.resources.ResourceLocation itemKey = new net.minecraft.resources.ResourceLocation("minecraftcalamity", activeSpellId);
        Item registeredItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(itemKey);

        if (!(registeredItem instanceof SpellItem spellItem)) {return;}String managerClassName = spellItem.getSpellId();try {String fullPath = "crab.mods.minecraftcalamity.items.spells.mage." + managerClassName;Class<?> managerClass = Class.forName(fullPath);Object managerInstance = managerClass.getDeclaredConstructor().newInstance();Method dataMethod = managerClass.getMethod("getspelldata");Object[][] data = (Object[][]) dataMethod.invoke(managerInstance);int manaCost = 0;double cooldownSeconds = 0.0;boolean spellFound = false;for (Object[] row : data) {if (row.length >= 3 && row[0].equals(activeSpellId)) {manaCost = ((Number) row[1]).intValue();cooldownSeconds = ((Number) row[2]).doubleValue();spellFound = true;break;}}if (!spellFound) return;PlayerMana mana = player.getCapability(ManaCapabilityProvider.PLAYER_MANA).orElse(null);if (mana != null) {if (mana.getCurrentMana() < manaCost) {player.sendSystemMessage(Component.literal("§cNot enough mana! Needs " + manaCost));return;}mana.consumeMana(player, manaCost);}int cooldownTicks = (int) (cooldownSeconds * 20);if (cooldownTicks > 0) {setSlotCooldown(stack, activeSlot, cooldownTicks, level.getGameTime());}Method spellMethod = managerClass.getMethod(activeSpellId, Player.class, Level.class);spellMethod.invoke(managerInstance, player, level);} catch (ClassNotFoundException e) {player.sendSystemMessage(Component.literal("§cSpell Manager class not found: " + managerClassName));} catch (NoSuchMethodException e) {player.sendSystemMessage(Component.literal("§cMethod '" + activeSpellId + "' not found inside manager."));} catch (Exception e) {e.printStackTrace();}}}