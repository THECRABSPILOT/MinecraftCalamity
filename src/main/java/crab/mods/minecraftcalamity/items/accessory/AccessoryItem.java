package crab.mods.minecraftcalamity.items.accessory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AccessoryItem extends Item {
    private  final String HoverText;

    public AccessoryItem(Properties properties, String HoverText) {
        super(properties);
        this.HoverText = HoverText;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal(HoverText));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
    public String getSpellId() {
        return this.HoverText;
    }
}
