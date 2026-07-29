package crab.mods.minecraftcalamity.items.magicitems;

import crab.mods.minecraftcalamity.capability.ManaCapabilityProvider;
import crab.mods.minecraftcalamity.capability.PlayerMana;
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        String activeSpell = getSpellInSlot(stack, getSelectedSlot(stack));

        tooltipComponents.add(Component.literal("§bSpell Slots: " + this.SpellSlots));
        tooltipComponents.add(Component.literal("§eSelected Spell: §f" + activeSpell));

        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            tooltipComponents.add(Component.literal("§7--- Spell Slots ---"));
            for (int i = 0; i < SpellSlots; i++) {
                String assignedSpell = getSpellInSlot(stack, i);

                if (i == getSelectedSlot(stack)) {
                    tooltipComponents.add(Component.literal("§a> Slot " + (i + 1) + " [Active]: §f" + assignedSpell));
                } else {
                    tooltipComponents.add(Component.literal("§7- Slot " + (i + 1) + ": §8" + assignedSpell));
                }
            }
        } else {
            tooltipComponents.add(Component.literal("§8Hold §7[Shift]§8 for slot details"));
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    public static void setSpellInSlot(ItemStack stack, int slot, String spellId) {
        if (stack.hasTag() && slot >= 0 && slot < stack.getOrCreateTag().getInt("SpellSlots")) {
            net.minecraft.nbt.CompoundTag spellsTag = stack.getOrCreateTag().getCompound("Spells");
            spellsTag.putString("Slot_" + slot, spellId);
            stack.getOrCreateTag().put("Spells", spellsTag);
        }
    }

    public static String getSpellInSlot(ItemStack stack, int slot) {
        if (stack.hasTag() && stack.getTag().contains("Spells")) {
            net.minecraft.nbt.CompoundTag spellsTag = stack.getTag().getCompound("Spells");
            String spell = spellsTag.getString("Slot_" + slot);
            if (!spell.isEmpty()) {
                return spell;
            }
        }
        return "Empty";
    }

    public void castActiveSpell(ItemStack stack, Player player, Level level) {
        int activeSlot = getSelectedSlot(stack);
        String activeSpellId = getSpellInSlot(stack, activeSlot);

        if (activeSpellId.equals("Empty")) {
            return;
        }

        net.minecraft.resources.ResourceLocation itemKey = new net.minecraft.resources.ResourceLocation("minecraftcalamity", activeSpellId);
        Item registeredItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(itemKey);

        if (!(registeredItem instanceof SpellItem spellItem)) {
            return;
        }

        String managerClassName = spellItem.getSpellId();

        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return;
        }

        try {
            String fullPath = "crab.mods.minecraftcalamity.items.spells.mage." + managerClassName;
            Class<?> managerClass = Class.forName(fullPath);
            Object managerInstance = managerClass.getDeclaredConstructor().newInstance();

            Method dataMethod = managerClass.getMethod("getspelldata");
            Object[][] data = (Object[][]) dataMethod.invoke(managerInstance);

            int manaCost = 0;
            double cooldownSeconds = 0.0;
            boolean spellFound = false;

            for (Object[] row : data) {
                if (row.length >= 3 && row[0].equals(activeSpellId)) {
                    manaCost = ((Number) row[1]).intValue();
                    cooldownSeconds = ((Number) row[2]).doubleValue();
                    spellFound = true;
                    break;
                }
            }

            if (!spellFound) return;

            PlayerMana mana = player.getCapability(ManaCapabilityProvider.PLAYER_MANA).orElse(null);
            if (mana != null) {
                if (mana.getCurrentMana() < manaCost) {
                    player.sendSystemMessage(Component.literal("§cNot enough mana! Needs " + manaCost));
                    return;
                }
                mana.consumeMana(player, manaCost);
            }

            int cooldownTicks = (int) (cooldownSeconds * 20);
            if (cooldownTicks > 0) {
                player.getCooldowns().addCooldown(stack.getItem(), cooldownTicks);
            }

            Method spellMethod = managerClass.getMethod(activeSpellId, Player.class, Level.class);
            spellMethod.invoke(managerInstance, player, level);

        } catch (ClassNotFoundException e) {
            player.sendSystemMessage(Component.literal("§cSpell Manager class not found: " + managerClassName));
        } catch (NoSuchMethodException e) {
            player.sendSystemMessage(Component.literal("§cMethod '" + activeSpellId + "' not found in " + managerClassName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
