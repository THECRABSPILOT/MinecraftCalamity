package crab.mods.minecraftcalamity.items.magicitems;

import crab.mods.minecraftcalamity.capability.ManaCapabilityProvider;
import crab.mods.minecraftcalamity.capability.PlayerMana;
import crab.mods.minecraftcalamity.items.spells.StaffSpells;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

public class ModularStaffItem extends Item {
    private final int SpellSlots;
    private final int BaseManaCost;
    private final double CastSpeed;

    public ModularStaffItem(Properties properties, int SpellSlots, int BaseManaCost, double CastSpeed) {
        super(properties);
        this.SpellSlots = SpellSlots;
        this.BaseManaCost = BaseManaCost;
        this.CastSpeed = CastSpeed;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Run your code only on the server side
        if (!level.isClientSide()) {
            CastSpell(itemstack, player, level);
        }

        // Return SUCCESS to swing the hand and consume the action
        return InteractionResultHolder.success(itemstack);
    }

    public void CastSpell(ItemStack stack, Player player, Level level) {
        try {
            String fullPath = "crab.mods.minecraftcalamity.items.spells.StaffSpells";
            Class<?> staffClass = Class.forName(fullPath);
            Object staffInstance = staffClass.getDeclaredConstructor().newInstance();

            Method getSpelldata = staffClass.getMethod("getSpelldata");
            Method getModifiers = staffClass.getMethod("getModifiers");

            Object[][] projectiles = (Object[][]) getSpelldata.invoke(staffInstance);
            Object[][] modifiers = (Object[][]) getModifiers.invoke(staffInstance);

            String foundProjectileId = null;
            java.util.List<String> activeModifiers = new java.util.ArrayList<>();
            int totalManaCost = this.BaseManaCost;
            boolean projectileFoundInSlots = false;

            // 1. Scan all slots to find modifiers and strictly ONE projectile
            for (int i = 0; i < SpellSlots; i++) {
                String spellId = getSpellInSlot(stack, i);

                if (spellId == null || spellId.equals("Empty")) {
                    continue;
                }

                net.minecraft.resources.ResourceLocation itemKey = new net.minecraft.resources.ResourceLocation("minecraftcalamity", spellId);
                Item registeredItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(itemKey);

                if (!(registeredItem instanceof StaffSpellItem spellItem)) {
                    continue;
                }

                // Check if it's a projectile
                boolean isProj = false;
                int spellCost = 0;

                for (Object[] row : projectiles) {
                    if (row.length >= 2 && row[0].equals(spellId)) {
                        spellCost = ((Number) row[1]).intValue();
                        isProj = true;
                        break;
                    }
                }

                if (isProj) {
                    if (projectileFoundInSlots) {
                        player.sendSystemMessage(Component.literal("§cStaff can only hold 1 projectile core!"));
                        return; // Stop if a projectile was already selected
                    }
                    foundProjectileId = spellId;
                    totalManaCost += spellCost;
                    projectileFoundInSlots = true;
                    continue;
                }

                // Otherwise, check if it's a modifier
                boolean isMod = false;
                for (Object[] row : modifiers) {
                    if (row.length >= 2 && row[0].equals(spellId)) {
                        spellCost = ((Number) row[1]).intValue();
                        isMod = true;
                        break;
                    }
                }

                if (isMod) {
                    activeModifiers.add(spellId);
                    totalManaCost += spellCost;
                }
            }

            // Validate that a projectile actually exists in the staff
            if (!projectileFoundInSlots || foundProjectileId == null) {
                player.sendSystemMessage(Component.literal("§cNo projectile spell slotted in staff!"));
                return;
            }

            // 2. Handle total combined Mana check and consumption
            crab.mods.minecraftcalamity.capability.PlayerMana mana = player.getCapability(crab.mods.minecraftcalamity.capability.ManaCapabilityProvider.PLAYER_MANA).orElse(null);
            if (mana != null) {
                if (mana.getCurrentMana() < totalManaCost) {
                    player.sendSystemMessage(Component.literal("§cNot enough mana! Needs " + totalManaCost));
                    return;
                }
                mana.consumeMana(player, totalManaCost);
            }

            // 3. Apply Modifiers first (pass context or apply effects)
            for (String modId : activeModifiers) {
                try {
                    Method modMethod = staffClass.getMethod(modId, Player.class, Level.class);
                    modMethod.invoke(staffInstance, player, level);
                } catch (NoSuchMethodException e) {
                    // Modifier method optional or missing
                }
            }

            // 4. Cast the single main projectile spell
            try {
                Method projectileMethod = staffClass.getMethod(foundProjectileId, Player.class, Level.class);
                projectileMethod.invoke(staffInstance, player, level);
            } catch (NoSuchMethodException e) {
                player.sendSystemMessage(Component.literal("§cProjectile method '" + foundProjectileId + "' not implemented!"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("§bSpell Slots: " + this.SpellSlots));
        tooltipComponents.add(Component.literal("§3Base Mana Cost: §f" + this.BaseManaCost));

        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            tooltipComponents.add(Component.literal("§7--- Spell Slots ---"));
            for (int i = 0; i < SpellSlots; i++) {
                String assignedSpell = getSpellInSlot(stack, i);
                tooltipComponents.add(Component.literal("§7- Slot " + (i + 1) + ": §8" + assignedSpell));
            }
        } else {
            tooltipComponents.add(Component.literal("§8Hold §7[Shift]§8 for slot details"));
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}