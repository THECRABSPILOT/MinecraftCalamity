package crab.mods.minecraftcalamity.effect;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MinecraftCalamity.MODID);

    public static final RegistryObject<MobEffect> MANA_REGEN = MOB_EFFECTS.register("mana_regen",
            () -> new ManaRegen(MobEffectCategory.NEUTRAL, 0x001aff));

    public static final RegistryObject<MobEffect> TIME_BOMB = MOB_EFFECTS.register("time_bomb",
            () -> new TimeBomb(MobEffectCategory.NEUTRAL, 0x53ed7c));

    public static final RegistryObject<MobEffect> ORE_SIGHT = MOB_EFFECTS.register("ore_sight",
            () -> new OreSight(MobEffectCategory.BENEFICIAL, 0xfccf03)); // ← fixed

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}