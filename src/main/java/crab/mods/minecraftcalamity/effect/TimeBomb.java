package crab.mods.minecraftcalamity.effect;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class TimeBomb extends MobEffect {
    public TimeBomb(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide()) {
            var effectInstance = pLivingEntity.getEffect(this);

            if (effectInstance != null && effectInstance.getDuration() == 1 && !pLivingEntity.isDeadOrDying()) {
                Level level = pLivingEntity.level();

                float explosionRadius = 3.0f + (pAmplifier * 1.5f);

                level.explode(
                        pLivingEntity,
                        pLivingEntity.getX(),
                        pLivingEntity.getY(),
                        pLivingEntity.getZ(),
                        explosionRadius,
                        Level.ExplosionInteraction.MOB
                );


                DamageSource explosionDamage = level.damageSources().explosion(null, pLivingEntity);
                pLivingEntity.hurt(explosionDamage, Float.MAX_VALUE);
            }
        }

        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}