package crab.mods.minecraftcalamity.items.magicitems;

import net.minecraft.world.item.Item;

public class StaffSpellItem extends Item {

    private final String Type;

    public StaffSpellItem(Properties properties, String Type) {
        super(properties);
        this.Type = Type;
    }

    public String getSpellId() {
        return this.Type;
    }
}