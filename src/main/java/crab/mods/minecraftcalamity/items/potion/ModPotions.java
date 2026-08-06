package crab.mods.minecraftcalamity.items.potion;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.effect.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static  final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, MinecraftCalamity.MODID); //nvm found it

    public static final RegistryObject<Potion> MANA_BREW = POTIONS.register("mana_brew",
            () -> new Potion(new MobEffectInstance(ModEffects.MANA_REGEN.get(), 1200, 0)));

    public static final RegistryObject<Potion> CREEPER_MILK = POTIONS.register("creeper_milk",
            () -> new Potion(new MobEffectInstance(ModEffects.TIME_BOMB.get(), 200, 0)));


    public static final RegistryObject<Potion> SPELUNKER_POTION = POTIONS.register("spelunker_potion",
            () -> new Potion(new MobEffectInstance(ModEffects.ORE_SIGHT.get(), 600, 0)));

    public static final RegistryObject<Potion> LEVITATION_POTION = POTIONS.register("levitation_potion",
            () -> new Potion(new MobEffectInstance(MobEffects.LEVITATION, 600, 0)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
