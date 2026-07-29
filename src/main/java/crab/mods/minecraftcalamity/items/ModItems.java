package crab.mods.minecraftcalamity.items;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.blocks.ModBlocks;
import crab.mods.minecraftcalamity.items.magicitems.ModularStaffItem;
import crab.mods.minecraftcalamity.items.magicitems.SpellBookItem;
import crab.mods.minecraftcalamity.items.magicitems.SpellItem;
import crab.mods.minecraftcalamity.items.magicitems.StaffSpellItem;
import crab.mods.minecraftcalamity.items.magicitems.UniqueBooks.MonsterBookItem;
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

    public static final RegistryObject<Item> CALAMITITE_INGOT = ITEMS.register("calamitite_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> HELLFORGE = ITEMS.register("hellforge",
            () -> new BlockItem(ModBlocks.HELLFORGE.get(), new Item.Properties()));

    public static final RegistryObject<Item> PEDESTAL = ITEMS.register("pedestal",
            () -> new BlockItem(ModBlocks.PEDESTAL_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> ARCANE_WORKBENCH = ITEMS.register("arcane_workbench",
            () -> new BlockItem(ModBlocks.ARCANE_WORKBENCH.get(), new Item.Properties()));

    public static final RegistryObject<Item> WOODEN_STAFF = ITEMS.register("wooden_staff",
            () -> new ModularStaffItem(new Item.Properties().stacksTo(1),2,10,0.2));
//STAFF SPELLS

    public static final RegistryObject<Item> FIREBALL_CORE = ITEMS.register("fireball_core",
            () -> new StaffSpellItem(new Item.Properties().stacksTo(64), "projectile"));


    public static final RegistryObject<Item> BOUNCE_MODIFIER = ITEMS.register("bounce_modifier",
            () -> new StaffSpellItem(new Item.Properties().stacksTo(64), "modifier"));

    public static final RegistryObject<Item> LIGHTNING_CORE = ITEMS.register("lightning_core",
            () -> new StaffSpellItem(new Item.Properties().stacksTo(64), "projectile"));



    public static final RegistryObject<Item> SPLIT_MODIFER = ITEMS.register("split_modifier",
            () -> new StaffSpellItem(new Item.Properties().stacksTo(64), "modifier"));


    // ARMOR
    public static final RegistryObject<Item> CALAMITITE_HELMET = ITEMS.register("calamitite_helmet",
            () -> new CalamitieArmorItem(ModArmorMaterials.CALAMITITE_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> CALAMITITE_CHESTPLATE = ITEMS.register("calamitite_chestplate",
            () -> new CalamitieArmorItem(ModArmorMaterials.CALAMITITE_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> CALAMITITE_LEGGINGS = ITEMS.register("calamitite_leggings",
            () -> new CalamitieArmorItem(ModArmorMaterials.CALAMITITE_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> CALAMITITE_BOOTS = ITEMS.register("calamitite_boots",
            () -> new CalamitieArmorItem(ModArmorMaterials.CALAMITITE_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));




    //SpellBooks

    public static final RegistryObject<Item> LIBRARIAN_BOOK = ITEMS.register("librarian_book",
            () -> new SpellBookItem(new Item.Properties().stacksTo(1), 6));

    public static final RegistryObject<Item> CARNIVOUROUS_GRIMOIRE = ITEMS.register("carniverous_grimoire",
            () -> new MonsterBookItem(new Item.Properties().stacksTo(1), 6));


    //BOOK SPELLS

    //FIRE
    public static final RegistryObject<Item> FIRE_FLIGHT = ITEMS.register("fire_flight",
            () -> new SpellItem(new Item.Properties().stacksTo(64), "FireSpells"));

    public static final RegistryObject<Item> FIREBALL = ITEMS.register("fireball",
            () -> new SpellItem(new Item.Properties().stacksTo(64), "FireSpells"));





    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}