package crab.mods.minecraftcalamity.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CalamityConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // Config option fields
    
    public static final ForgeConfigSpec.BooleanValue OLDWORLDITEMS;
    public static final ForgeConfigSpec.IntValue SEVERITY;


    static {
        BUILDER.push("Minecraft Calamity General Settings");


        OLDWORLDITEMS = BUILDER
                .comment("Enables item called calamity altar to load calamity structures and ores into old worlds")
                .define("oldworlditems", false);


        SEVERITY = BUILDER
                .comment("Increases boss hp and power")
                .defineInRange("Severity", 64, 1, 512);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}