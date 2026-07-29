package crab.mods.minecraftcalamity.items.magicitems.UniqueBooks;

import crab.mods.minecraftcalamity.items.magicitems.SpellBookItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

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
}