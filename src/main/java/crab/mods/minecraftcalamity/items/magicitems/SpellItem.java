package crab.mods.minecraftcalamity.items.magicitems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

public class SpellItem extends Item {

    private final String SpellManager;
    private final String Description;

    public SpellItem(Properties properties, String SpellManager, String Description) {
        super(properties);
        this.SpellManager = SpellManager;
        this.Description = Description;
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("minecraftcalamity.level", 1);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {

        tooltipComponents.add(Component.literal(Description));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        tooltipComponents.add(Component.literal(" "));


        int currentLevel = 1;
        if (stack.hasTag() && stack.getTag().contains("minecraftcalamity.level")) {
            currentLevel = stack.getTag().getInt("minecraftcalamity.level");
        }
        tooltipComponents.add(Component.literal("§eLevel: " + currentLevel));

        try {
            String fullPath = "crab.mods.minecraftcalamity.items.spells.mage." + SpellManager;
            Class<?> managerClass = Class.forName(fullPath);
            Object managerInstance = managerClass.getDeclaredConstructor().newInstance();

            Method dataMethod = managerClass.getMethod("getspelldata");
            Object[][] data = (Object[][]) dataMethod.invoke(managerInstance);

            int manaCost = 0;
            double cooldownSeconds = 0.0;
            boolean spellFound = false;


            String fullRegistryId = BuiltInRegistries.ITEM.getKey(this).toString();
            String pathOnlyId = BuiltInRegistries.ITEM.getKey(this).getPath();

            for (Object[] row : data) {
                if (row.length >= 3 && row[0] != null) {
                    String rowId = row[0].toString();
                    if (rowId.equals(fullRegistryId) || rowId.equals(pathOnlyId)) {
                        manaCost = ((Number) row[1]).intValue();
                        cooldownSeconds = ((Number) row[2]).doubleValue();
                        spellFound = true;
                        break;
                    }
                }
            }

            if (spellFound) {
                tooltipComponents.add(Component.literal("§bMana Cost: " + manaCost));
                tooltipComponents.add(Component.literal("§bCooldown: " + cooldownSeconds + "s"));
            } else {
                tooltipComponents.add(Component.literal("§c[Spell data ID mismatch: " + pathOnlyId + "]"));
            }

        } catch (Exception e) {
            tooltipComponents.add(Component.literal("§c[Error loading spell manager: " + SpellManager + "]"));
        }
    }

    public String getSpellId() {
        return this.SpellManager;
    }
}
