package crab.mods.minecraftcalamity.entity.custom;

import crab.mods.minecraftcalamity.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;

import net.minecraft.util.RandomSource;;

public class CaveWizardEntity extends Monster implements RangedAttackMob {

    public CaveWizardEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        // Create the staff and insert the fireball core into Slot 1
        ItemStack wizardStaff = new ItemStack(ModItems.WOODEN_STAFF.get());
        CompoundTag tag = wizardStaff.getOrCreateTag();

        tag.putInt("SpellSlots", 2);

        ListTag modifiers = new ListTag();
        CompoundTag fireballModifier = new CompoundTag();
        fireballModifier.putInt("Slot", 1);
        fireballModifier.putString("id", "minecraftcalamity:fireball_core");
        modifiers.add(fireballModifier);

        tag.put("SpellModifiers", modifiers);

        // Equip the configured staff in its main hand
        this.setItemSlot(EquipmentSlot.MAINHAND, wizardStaff);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F); // Don't drop the staff on death
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));

        // Keeps distance: Runs away if player gets closer than 4 blocks, attacks from up to 16 blocks away
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.25D, 40, 16.0F));

        // Custom wandering/movement behavior
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        // Targets
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    // Ranged attack behavior (Shoots a fireball toward the target)
    @Override
    public void performRangedAttack(@NotNull LivingEntity pTarget, float pVelocity) {
        double d0 = pTarget.getX() - this.getX();
        double d1 = pTarget.getY(0.5D) - this.getEyeY();
        double d2 = pTarget.getZ() - this.getZ();

        LargeFireball fireball = new LargeFireball(this.level(), this, d0, d1, d2, 2);
        fireball.setPos(this.getX(), this.getEyeY() - 0.1D, this.getZ());

        // Match your ModularStaffItem settings for consistency
        fireball.getPersistentData().putBoolean("IsBouncy", true);
        fireball.getPersistentData().putInt("BouncesLeft", 3);

        this.level().addFreshEntity(fireball);
        this.playSound(SoundEvents.GHAST_SHOOT, 1.0F, 1.0F);
    }

    @Override
    public boolean isPreventingPlayerRest(@NotNull Player pPlayer) {
        return true;
    }

    // Cave Spawning Condition (Spawns deep underground in dark areas)
    public static boolean checkCaveWizardSpawnRules(EntityType<CaveWizardEntity> entityType, ServerLevelAccessor level, net.minecraft.world.entity.MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        // Must spawn below sea level (Y < 50) in dark cave areas
        return pos.getY() < 50
                && level.getBrightness(LightLayer.BLOCK, pos) == 0
                && level.getBrightness(LightLayer.SKY, pos) == 0
                && checkMonsterSpawnRules(entityType, level, spawnType, pos, random);
    }
}