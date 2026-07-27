package crab.mods.minecraftcalamity.items.spells;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.blocks.ModBlocks;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static crab.mods.minecraftcalamity.MinecraftCalamity.ITEMS;

public class ModSpells {


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
