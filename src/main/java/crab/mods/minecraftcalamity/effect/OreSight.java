package crab.mods.minecraftcalamity.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class OreSight extends MobEffect {
    public OreSight(MobEffectCategory category, int color) {
        super(category, color); // BENEFICIAL + 0xFFD700 recommended
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // empty on purpose – all logic is client-side
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false; // no need to tick every frame
    }
}