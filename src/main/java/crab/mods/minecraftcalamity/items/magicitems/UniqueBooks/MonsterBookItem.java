package crab.mods.minecraftcalamity.items.magicitems.UniqueBooks;

import crab.mods.minecraftcalamity.capability.ManaCapabilityProvider;
import crab.mods.minecraftcalamity.capability.PlayerMana;
import crab.mods.minecraftcalamity.items.magicitems.SpellBookItem;
import crab.mods.minecraftcalamity.items.magicitems.SpellItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

public class MonsterBookItem extends SpellBookItem {

    public MonsterBookItem(Properties properties, int spellSlots) {
        super(properties, spellSlots);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("§4The book will damage you to cast spells if you are out of mana"));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    @Override
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
                if (mana.getCurrentMana() >= manaCost) {
                    mana.consumeMana(player, manaCost);
                } else {
                    float damageAmount = Math.max(1.0f, (float) manaCost / 20.0f);
                    player.hurt(player.damageSources().magic(), damageAmount);
                }
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