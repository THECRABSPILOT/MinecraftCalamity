package crab.mods.minecraftcalamity.items.magicitems;

import net.minecraft.world.item.Item;

public class SpellItem extends Item {

    private final String SpellManager;

    public SpellItem(Properties properties, String SpellManager) {
        super(properties);
        this.SpellManager = SpellManager;
    }

    public String getSpellId() {
        return this.SpellManager;
    }
}