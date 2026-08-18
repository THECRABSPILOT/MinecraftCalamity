package crab.mods.minecraftcalamity;

import net.minecraftforge.common.ForgeConfigSpec;

public class CalamityConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // Config option fields
    
    public static final ForgeConfigSpec.BooleanValue OLDWORLDITEMS;
    public static final ForgeConfigSpec.IntValue SEVERITY;
    public static final ForgeConfigSpec.BooleanValue DONIGHTRAIDS;
    public static final ForgeConfigSpec.IntValue NIGHTRAIDCHANCE;
    public static final ForgeConfigSpec.BooleanValue SCARYZOMBIES;

    static {
        BUILDER.push("Minecraft Calamity Settings");


        OLDWORLDITEMS = BUILDER
                .comment("Enables item called calamity altar to load calamity structures and ores into old worlds")
                .define("oldworlditems", false);


        SEVERITY = BUILDER
                .comment("Increases boss hp and power")
                .defineInRange("Severity", 64, 1, 512);

        DONIGHTRAIDS = BUILDER
                .comment("Each Night theres a 1 in 4(configurable) chance that a nearby village is attacked by a horde of illagers or zombies")
                .define("donightraids", true);

        NIGHTRAIDCHANCE = BUILDER
                .comment("1 in # chance each night a nearby village is attacked")
                .defineInRange("raidchance", 4, 1, 512);

        SCARYZOMBIES = BUILDER
                .comment("\"gift\" zombies the ability to use pickaxes.")
                .define("evilzombies", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}