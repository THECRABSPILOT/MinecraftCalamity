package crab.mods.minecraftcalamity.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CalamityConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // Config option fields
    public static final ForgeConfigSpec.BooleanValue ENABLE_CALAMITY_EVENTS;
    public static final ForgeConfigSpec.BooleanValue LOG_DEBUG_MESSAGES;
    public static final ForgeConfigSpec.IntValue MAX_EVENT_RADIUS;


    static {
        BUILDER.push("Minecraft Calamity General Settings");

        // 1. Boolean Config
        ENABLE_CALAMITY_EVENTS = BUILDER
                .comment("Disable items similair to other installed mods")
                .define("vaultitems", true);

        // 2. Boolean Config
        LOG_DEBUG_MESSAGES = BUILDER
                .comment("Enables item called calamity altar to load calamity structures and ores into old worlds")
                .define("oldworlditems", false);

        // 3. Integer Config (with min/max bounds)
        MAX_EVENT_RADIUS = BUILDER
                .comment("Increases boss hp and power")
                .defineInRange("Severity", 64, 1, 512);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}