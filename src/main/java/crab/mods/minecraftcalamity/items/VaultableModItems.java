package crab.mods.minecraftcalamity.items;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VaultableModItems {

    // 1. Create the registry for this specific class
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MinecraftCalamity.MODID);

    // 2. Define your item
    public static final RegistryObject<Item> CALAMITY_GEM = ITEMS.register("calamity_gem",
            () -> new Item(new Item.Properties()));

    // 3. Helper method to attach this register to the mod event bus
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}