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

    // MATERIALS
    public static final RegistryObject<Item> CALAMITITE_SCRAP = ITEMS.register("calamitite_scrap",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BLACKSTEEL_INGOT = ITEMS.register("blacksteel_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> HELLFORGE = ITEMS.register("hellforge",
            () -> new BlockItem(ModBlocks.HELLFORGE.get(), new Item.Properties()));


    // ARMOR
    public static final RegistryObject<Item> CALAMITITE_HELMET = ITEMS.register("calamitite_helmet",
            () -> new CalamitieArmorItem(ModArmorMaterials.CALAMITITE_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> CALAMITITE_CHESTPLATE = ITEMS.register("calamitite_chestplate",
            () -> new CalamitieArmorItem(ModArmorMaterials.CALAMITITE_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> CALAMITITE_LEGGINGS = ITEMS.register("calamitite_leggings",
            () -> new CalamitieArmorItem(ModArmorMaterials.CALAMITITE_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> CALAMITITE_BOOTS = ITEMS.register("calamitite_boots",
            () -> new CalamitieArmorItem(ModArmorMaterials.CALAMITITE_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}