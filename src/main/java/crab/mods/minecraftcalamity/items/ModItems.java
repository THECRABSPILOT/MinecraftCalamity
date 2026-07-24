package crab.mods.minecraftcalamity.items;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.blocks.ModBlocks;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MinecraftCalamity.MODID);

    // ORES
    public static final RegistryObject<Item> CALAMITITE_ORE = ITEMS.register("calamitite_ore",
            () -> new BlockItem(ModBlocks.CALAMITITE_ORE.get(), new Item.Properties()));

    // Template Test Accessory Registration
    public static final RegistryObject<Item> TEST_ACCESSORY = ITEMS.register("test_accessory",
            () -> new TestAccessoryItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CLOUD_IN_A_JAR = ITEMS.register("cloud_in_a_jar",
            () -> new CloudInAJar(new Item.Properties().stacksTo(1)));

    // ARMOR
    public static final RegistryObject<Item> CUSTOM_HELMET = ITEMS.register("custom_helmet",
            () -> new CustomArmorItem(ModArmorMaterials.CUSTOM_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> CUSTOM_CHESTPLATE = ITEMS.register("custom_chestplate",
            () -> new CustomArmorItem(ModArmorMaterials.CUSTOM_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> CUSTOM_LEGGINGS = ITEMS.register("custom_leggings",
            () -> new CustomArmorItem(ModArmorMaterials.CUSTOM_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> CUSTOM_BOOTS = ITEMS.register("custom_boots",
            () -> new CustomArmorItem(ModArmorMaterials.CUSTOM_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}