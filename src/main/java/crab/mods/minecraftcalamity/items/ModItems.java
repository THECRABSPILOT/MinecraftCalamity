package crab.mods.minecraftcalamity.items;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.blocks.ModBlocks;
import crab.mods.minecraftcalamity.items.accessory.AccessoryItem;
import crab.mods.minecraftcalamity.items.accessory.CloudInAJar;
import crab.mods.minecraftcalamity.items.accessory.SatchelItem;
import crab.mods.minecraftcalamity.items.accessory.TestAccessoryItem;
import crab.mods.minecraftcalamity.items.alchemy.LargeBottleItem;
import crab.mods.minecraftcalamity.items.magicitems.*;
import crab.mods.minecraftcalamity.items.magicitems.UniqueBooks.MonsterBookItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MinecraftCalamity.MODID);
    //BLOCKS


    public static final RegistryObject<Item> HELLFORGE = ITEMS.register("hellforge",
            () -> new BlockItem(ModBlocks.HELLFORGE.get(), new Item.Properties()));

    public static final RegistryObject<Item> PEDESTAL = ITEMS.register("pedestal",
            () -> new BlockItem(ModBlocks.PEDESTAL_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> ARCANE_WORKBENCH = ITEMS.register("arcane_workbench",
            () -> new BlockItem(ModBlocks.ARCANE_WORKBENCH.get(), new Item.Properties()));

    public static final RegistryObject<Item> SWORD_IN_STONE = ITEMS.register("sword_in_stone",
            () -> new BlockItem(ModBlocks.SWORD_IN_STONE.get(), new Item.Properties()));

    //MATERIALS

    public static final RegistryObject<Item> CALAMITITE_ORE = ITEMS.register("calamitite_ore",
            () -> new BlockItem(ModBlocks.CALAMITITE_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> CALAMITITE_SCRAP = ITEMS.register("calamitite_scrap",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BLACKSTEEL_INGOT = ITEMS.register("blacksteel_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CALAMITITE_INGOT = ITEMS.register("calamitite_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> AMETHYST_DUST = ITEMS.register("amethyst_dust",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SCULK_CARTILAGE = ITEMS.register("sculk_cartilage",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> SCULK_TENDRIL = ITEMS.register("sculk_tendril",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> LAVA_TUBE = ITEMS.register("lava_tube",
            () -> new Item(new Item.Properties().stacksTo(64)));


    //STAFFS
    public static final RegistryObject<Item> WOODEN_STAFF = ITEMS.register("wooden_staff",
            () -> new ModularStaffItem(new Item.Properties().stacksTo(1),2,10,0.2,1.0));

    public static final RegistryObject<Item> ROSE_STAFF = ITEMS.register("rose_staff",
            () -> new ModularStaffItem(new Item.Properties().stacksTo(1),4,10,0.2,1.2));

    public static final RegistryObject<Item> SCULK_SCEPTER = ITEMS.register("sculk_scepter",
            () -> new ModularStaffItem(new Item.Properties().stacksTo(1),6,15,0.2,1.5));


    //STAFF SPELLS

    public static final RegistryObject<Item> FIREBALL_CORE = ITEMS.register("fireball_core",
            () -> new StaffSpellItem(new Item.Properties().stacksTo(64), "projectile"));

    public static final RegistryObject<Item> LIGHTNING_CORE = ITEMS.register("lightning_core",
            () -> new StaffSpellItem(new Item.Properties().stacksTo(64), "projectile"));

    public static final RegistryObject<Item> SOUL_BOLT = ITEMS.register("soul_bolt",
            () -> new StaffSpellItem(new Item.Properties().stacksTo(64), "projectile"));

    //MODIFIERS
    public static final RegistryObject<Item> BOUNCE_MODIFIER = ITEMS.register("bounce_modifier",
            () -> new StaffSpellItem(new Item.Properties().stacksTo(64), "modifier"));

    public static final RegistryObject<Item> SPLIT_MODIFER = ITEMS.register("split_modifier",
            () -> new StaffSpellItem(new Item.Properties().stacksTo(64), "modifier"));

    //ARMOR

    public static final RegistryObject<Item> CALAMITITE_HELMET = ITEMS.register("calamitite_helmet",
            () -> new CalamitieArmorItem(ModArmorMaterials.CALAMITITE_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> CALAMITITE_CHESTPLATE = ITEMS.register("calamitite_chestplate",
            () -> new CalamitieArmorItem(ModArmorMaterials.CALAMITITE_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> CALAMITITE_LEGGINGS = ITEMS.register("calamitite_leggings",
            () -> new CalamitieArmorItem(ModArmorMaterials.CALAMITITE_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> CALAMITITE_BOOTS = ITEMS.register("calamitite_boots",
            () -> new CalamitieArmorItem(ModArmorMaterials.CALAMITITE_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));

    //SPELLBOOKS

    public static final RegistryObject<Item> LIBRARIAN_BOOK = ITEMS.register("librarian_book",
            () -> new SpellBookItem(new Item.Properties().stacksTo(1), 6));

    public static final RegistryObject<Item> CARNIVOUROUS_GRIMOIRE = ITEMS.register("carniverous_grimoire",
            () -> new MonsterBookItem(new Item.Properties().stacksTo(1), 6));

    public static final RegistryObject<Item> SCULK_GRIMOIRE = ITEMS.register("sculk_grimoire",
            () -> new SpellBookItem(new Item.Properties().stacksTo(1), 8));

    //public static final RegistryObject<Item> WEAVER_BOOK = ITEMS.register("weaver_book",
    //        () -> new WeaverBookItem(new Item.Properties()));  //probably wont work on dis for a while


    //BOOK SPELLS
    public static final RegistryObject<Item> FIRE_FLIGHT = ITEMS.register("fire_flight",
            () -> new SpellItem(new Item.Properties().stacksTo(64), "FireSpells","Allows ten seconds of flight"));

    public static final RegistryObject<Item> FIREBALL = ITEMS.register("fireball",
            () -> new SpellItem(new Item.Properties().stacksTo(64), "FireSpells","Shoots a Fireball (Note: this is explosive and damages blocks)"));

    public static final RegistryObject<Item> INFERNAL_COLUMN = ITEMS.register("infernal_column",
            () -> new SpellItem(new Item.Properties().stacksTo(64), "FireSpells","Summon a column of fire"));

    public static final RegistryObject<Item> SUMMON_GOLEM = ITEMS.register("summon_golem",
            () -> new SpellItem(new Item.Properties().stacksTo(64), "VillagerSpells", "Summon a temporary Iron Golem"));

    public static final RegistryObject<Item> HEAL = ITEMS.register("heal",
            () -> new SpellItem(new Item.Properties().stacksTo(64), "VillagerSpells","Instantly heal yourself"));



    //ACCESSORIES
    public static final RegistryObject<Item> MANA_STAR = ITEMS.register("mana_star",
            () -> new AccessoryItem(new Item.Properties().stacksTo(1),"§7Increases Max mana by 50"));

    public static final RegistryObject<Item> CROSS_RING = ITEMS.register("cross_ring",
            () -> new AccessoryItem(new Item.Properties().stacksTo(1),"§7Grants Immunity to wither"));

    public static final RegistryObject<Item> GLOVE = ITEMS.register("glove",
            () -> new AccessoryItem(new Item.Properties().stacksTo(1),"§7Doesnt do much... yet"));

    public static final RegistryObject<Item> HEATER_GLOVE = ITEMS.register("heater_glove",
            () -> new AccessoryItem(new Item.Properties().stacksTo(1),"§7Instantly smelts mined things and melts connected snow"));

    public static final RegistryObject<Item> TEST_ACCESSORY = ITEMS.register("test_accessory",
            () -> new TestAccessoryItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CLOUD_IN_A_JAR = ITEMS.register("cloud_in_a_jar",
            () -> new CloudInAJar(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SATCHEL = ITEMS.register("satchel",
            () -> new SatchelItem(new Item.Properties().stacksTo(1)));

    //WEAPONS N STUFF
    public static final RegistryObject<Item> ENCHANTED_SWORD = ITEMS.register("enchanted_sword",
            () -> new EnchantedSwordItem(Tiers.DIAMOND, 13, -2.4f, new Item.Properties(),"§7Does this make ME the king?"));

    public static final RegistryObject<Item> BOTCHED_ENCHANTED_SWORD = ITEMS.register("botched_enchanted_sword",
            () -> new EnchantedSwordItem(Tiers.DIAMOND, 10, -2.4f, new Item.Properties(),"§7Feels off."));

    public static final RegistryObject<Item> SWORD_OF_THE_DEEP = ITEMS.register("sword_of_the_deep",
            () -> new SwordItem(Tiers.IRON, 3, -2.4f, new Item.Properties()));

    public static final RegistryObject<Item> IRON_DRILL = ITEMS.register("iron_drill",
            () -> new DrillItem(Tiers.IRON, 1, -2.8F, 3, new Item.Properties()));

    public static final RegistryObject<Item> SCULK_AXE = ITEMS.register("sculk_axe",
            () -> new AxeItem(Tiers.IRON, 3, -2.4f, new Item.Properties()));




    //ALCHEMY
    public static final RegistryObject<Item> LARGE_BOTTLE = ITEMS.register("large_bottle",
            () -> new LargeBottleItem(new Item.Properties()));

    public static final RegistryObject<Item> SCOOPER = ITEMS.register("scooper",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SHULKER_MEAT = ITEMS.register("shulker_meat",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(0)
                            .saturationMod(0f)
                            .effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 600, 1), 1.0f)
                            .alwaysEat()
                            .build()
            )));

    //GLYPHS
    public static final RegistryObject<Item> COMMON_GLYPH = ITEMS.register("common_glyph",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> UNCOMMON_GLYPH = ITEMS.register("uncommon_glyph",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RARE_GLYPH = ITEMS.register("rare_glyph",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> EPIC_GLYPH = ITEMS.register("epic_glyph",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> LEGENDARY_GLYPH = ITEMS.register("legendary_glyph",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SPECIAL_GLYPH = ITEMS.register("special_glyph",
            () -> new Item(new Item.Properties()));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}