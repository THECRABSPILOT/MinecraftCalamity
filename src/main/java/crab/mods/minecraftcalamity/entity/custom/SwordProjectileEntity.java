package crab.mods.minecraftcalamity.entity.custom;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class SwordProjectileEntity extends Projectile implements IEntityAdditionalSpawnData {

    private static final EntityDataAccessor<Integer> DATA_COLOR =
            SynchedEntityData.defineId(SwordProjectileEntity.class, EntityDataSerializers.INT);

    // Projectile Movement & Combat Properties
    private double baseSpeed = 0.5D;
    private double acceleration = 0.05D;
    private double gravity = 0.03D;
    private boolean hasGravity = true;
    private float damage = 5.0F;
    private MobEffect effectOnHit = null;
    int effectDuration = 100;
    int effectAmplifier = 0;

    // Bounce & Split Properties
    private boolean canBounce = false;
    private int maxBounces = 3;
    private int bounceCount = 0;
    private boolean shouldSplitOnImpact = false;
    private int splitCount = 3;

    // Particle & Visibility Properties
    private String trailParticle = null;
    private double particleSpread = 0.1D;
    private boolean singleParticle = false;
    private boolean isDeadByImpact = false;

    public SwordProjectileEntity(EntityType<? extends SwordProjectileEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_COLOR, 0xFFFFFF);
    }

    public void setProjectileColor(int color) {
        this.entityData.set(DATA_COLOR, color);
    }

    public int getProjectileColor() {
        return this.entityData.get(DATA_COLOR);
    }

    public void setStats(double baseSpeed, double acceleration, double gravity, boolean hasGravity, float damage, MobEffect effect, int duration, int amplifier) {
        this.baseSpeed = baseSpeed;
        this.acceleration = acceleration;
        this.gravity = gravity;
        this.hasGravity = hasGravity;
        this.damage = damage;
        this.effectOnHit = effect;
        this.effectDuration = duration;
        this.effectAmplifier = amplifier;
    }

    public void setParticleConfig(String particleRegistryName, double spread, boolean single, boolean invisible) {
        this.trailParticle = particleRegistryName;
        this.particleSpread = spread;
        this.singleParticle = single;
        this.setInvisible(invisible);
    }

    @Override
    public void tick() {
        super.tick();

        // If the projectile hit something, wait 15 ticks (enough time for impact particles to finish playing) before discarding
        if (this.isDeadByImpact) {
            if (this.tickCount >= 15) {
                this.discard();
            }
            return;
        }

        // Server-side movement, acceleration, gravity, and collision logic
        if (!this.level().isClientSide()) {
            Vec3 motion = this.getDeltaMovement();

            // Initialization if spawned without velocity vectors
            if (motion.lengthSqr() == 0) {
                float yaw = this.getYRot();
                float pitch = this.getXRot();
                double x = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
                double y = -Math.sin(Math.toRadians(pitch));
                double z = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
                motion = new Vec3(x, y, z).normalize().scale(baseSpeed);
            } else {
                double currentSpeed = motion.length() + acceleration;
                motion = motion.normalize().scale(currentSpeed);
            }

            if (this.hasGravity) {
                motion = motion.add(0.0D, -this.gravity, 0.0D);
            }

            this.setDeltaMovement(motion);

            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS) {
                this.onHit(hitresult);
            }

            this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);

            if (this.tickCount > 200) {
                this.discard();
            }
        } else {
            this.setPos(this.getX() + this.getDeltaMovement().x, this.getY() + this.getDeltaMovement().y, this.getZ() + this.getDeltaMovement().z);

            if (this.trailParticle != null && !this.trailParticle.isEmpty()) {
                ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.get(new ResourceLocation(this.trailParticle));
                if (type instanceof SimpleParticleType simpleType) {
                    int count = this.singleParticle ? 1 : 3;
                    for (int i = 0; i < count; i++) {
                        double offsetX = this.singleParticle ? 0.0D : (this.random.nextDouble() - 0.5D) * this.particleSpread * 2.0D;
                        double offsetY = this.singleParticle ? 0.0D : (this.random.nextDouble() - 0.5D) * this.particleSpread * 2.0D;
                        double offsetZ = this.singleParticle ? 0.0D : (this.random.nextDouble() - 0.5D) * this.particleSpread * 2.0D;

                        this.level().addParticle(simpleType,
                                this.getX() + offsetX,
                                this.getY() + (this.getBbHeight() / 2.0F) + offsetY,
                                this.getZ() + offsetZ,
                                0.0D, 0.0D, 0.0D);

                        if (this.singleParticle) break;
                    }
                }
            }
        }
    }

    private void spawnImpactParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            int color = this.getProjectileColor();
            float red = (float)(color >> 16 & 255) / 255.0F;
            float green = (float)(color >> 8 & 255) / 255.0F;
            float blue = (float)(color & 255) / 255.0F;

            ParticleOptions particleOptions = new DustParticleOptions(new Vector3f(red, green, blue), 1.5F);

            serverLevel.sendParticles(particleOptions,
                    this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ(),
                    20, // count
                    0.4D, 0.4D, 0.4D, // spread
                    0.1D // speed
            );
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level().isClientSide() && !this.isDeadByImpact) {
            spawnImpactParticles();
            this.setInvisible(true); // Hide model immediately so only particles are visible
            this.isDeadByImpact = true;
            this.tickCount = 0; // Reset tick counter to track delay for particle lifespan

            if (pResult.getEntity() instanceof LivingEntity target && target.isAlive()) {
                target.hurt(this.damageSources().magic(), this.damage);
                if (this.effectOnHit != null) {
                    target.addEffect(new MobEffectInstance(this.effectOnHit, this.effectDuration, this.effectAmplifier));
                }

                if (this.shouldSplitOnImpact) {
                    this.performSplit();
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (!this.level().isClientSide() && !this.isDeadByImpact) {
            if (this.canBounce && this.bounceCount < this.maxBounces) {
                this.bounceCount++;
                Vec3 motion = this.getDeltaMovement();

                switch (pResult.getDirection()) {
                    case UP, DOWN -> this.setDeltaMovement(motion.x, -motion.y, motion.z);
                    case NORTH, SOUTH -> this.setDeltaMovement(motion.x, motion.y, -motion.z);
                    case EAST, WEST -> this.setDeltaMovement(-motion.x, motion.y, motion.z);
                }
                return;
            }

            spawnImpactParticles();
            this.setInvisible(true); // Hide model immediately so only particles are visible
            this.isDeadByImpact = true;
            this.tickCount = 0; // Reset tick counter to track delay for particle lifespan

            if (this.shouldSplitOnImpact) {
                this.performSplit();
            }
        }
    }

    private void performSplit() {
        if (this.level().isClientSide()) return;

        double currentSpeed = this.getDeltaMovement().length();
        for (int i = 0; i < this.splitCount; i++) {
            @SuppressWarnings("unchecked")
            EntityType<SwordProjectileEntity> entityType = (EntityType<SwordProjectileEntity>) this.getType();

            SwordProjectileEntity splitProjectile = new SwordProjectileEntity(entityType, this.level());
            splitProjectile.setPos(this.getX(), this.getY(), this.getZ());
            splitProjectile.setProjectileColor(this.getProjectileColor());
            splitProjectile.setStats(currentSpeed, this.acceleration, this.gravity, this.hasGravity, this.damage * 0.5f, this.effectOnHit, this.effectDuration, this.effectAmplifier);
            splitProjectile.setParticleConfig(this.trailParticle, this.particleSpread, this.singleParticle, this.isInvisible());

            double angle = (Math.PI * 2 / this.splitCount) * i;
            Vec3 spreadMotion = new Vec3(Math.cos(angle) * currentSpeed, 0.2D, Math.sin(angle) * currentSpeed);
            splitProjectile.setDeltaMovement(spreadMotion);

            this.level().addFreshEntity(splitProjectile);
        }
    }

    @Override
    protected boolean canHitEntity(@NotNull net.minecraft.world.entity.Entity pTarget) {
        return super.canHitEntity(pTarget) && pTarget instanceof LivingEntity;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ProjectileColor")) this.setProjectileColor(pCompound.getInt("ProjectileColor"));
        if (pCompound.contains("BaseSpeed")) this.baseSpeed = pCompound.getDouble("BaseSpeed");
        if (pCompound.contains("Acceleration")) this.acceleration = pCompound.getDouble("Acceleration");
        if (pCompound.contains("Gravity")) this.gravity = pCompound.getDouble("Gravity");
        if (pCompound.contains("HasGravity")) this.hasGravity = pCompound.getBoolean("HasGravity");
        if (pCompound.contains("Damage")) this.damage = pCompound.getFloat("Damage");
        if (pCompound.contains("CanBounce")) this.canBounce = pCompound.getBoolean("CanBounce");
        if (pCompound.contains("MaxBounces")) this.maxBounces = pCompound.getInt("MaxBounces");
        if (pCompound.contains("ShouldSplitOnImpact")) this.shouldSplitOnImpact = pCompound.getBoolean("ShouldSplitOnImpact");
        if (pCompound.contains("SplitCount")) this.splitCount = pCompound.getInt("SplitCount");
        if (pCompound.contains("EffectOnHit")) {
            this.effectOnHit = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(pCompound.getString("EffectOnHit")));
            this.effectDuration = pCompound.getInt("EffectDuration");
            this.effectAmplifier = pCompound.getInt("EffectAmplifier");
        }
        if (pCompound.contains("TrailParticle")) this.trailParticle = pCompound.getString("TrailParticle");
        if (pCompound.contains("ParticleSpread")) this.particleSpread = pCompound.getDouble("ParticleSpread");
        if (pCompound.contains("SingleParticle")) this.singleParticle = pCompound.getBoolean("SingleParticle");
        if (pCompound.contains("Invisible")) this.setInvisible(pCompound.getBoolean("Invisible"));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("ProjectileColor", this.getProjectileColor());
        pCompound.putDouble("BaseSpeed", this.baseSpeed);
        pCompound.putDouble("Acceleration", this.acceleration);
        pCompound.putDouble("Gravity", this.gravity);
        pCompound.putBoolean("HasGravity", this.hasGravity);
        pCompound.putFloat("Damage", this.damage);
        pCompound.putBoolean("CanBounce", this.canBounce);
        pCompound.putInt("MaxBounces", this.maxBounces);
        pCompound.putBoolean("ShouldSplitOnImpact", this.shouldSplitOnImpact);
        pCompound.putInt("SplitCount", this.splitCount);
        if (this.effectOnHit != null) {
            pCompound.putString("EffectOnHit", BuiltInRegistries.MOB_EFFECT.getKey(this.effectOnHit).toString());
            pCompound.putInt("EffectDuration", this.effectDuration);
            pCompound.putInt("EffectAmplifier", this.effectAmplifier);
        }
        if (this.trailParticle != null) pCompound.putString("TrailParticle", this.trailParticle);
        pCompound.putDouble("ParticleSpread", this.particleSpread);
        pCompound.putBoolean("SingleParticle", this.singleParticle);
        pCompound.putBoolean("Invisible", this.isInvisible());
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeDouble(this.baseSpeed);
        buffer.writeDouble(this.acceleration);
        buffer.writeDouble(this.gravity);
        buffer.writeBoolean(this.hasGravity);
        buffer.writeFloat(this.damage);
        buffer.writeBoolean(this.canBounce);
        buffer.writeInt(this.maxBounces);
        buffer.writeBoolean(this.shouldSplitOnImpact);
        buffer.writeInt(this.splitCount);
        buffer.writeBoolean(this.trailParticle != null);
        if (this.trailParticle != null) {
            buffer.writeUtf(this.trailParticle);
        }
        buffer.writeDouble(this.particleSpread);
        buffer.writeBoolean(this.singleParticle);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.baseSpeed = additionalData.readDouble();
        this.acceleration = additionalData.readDouble();
        this.gravity = additionalData.readDouble();
        this.hasGravity = additionalData.readBoolean();
        this.damage = additionalData.readFloat();
        this.canBounce = additionalData.readBoolean();
        this.maxBounces = additionalData.readInt();
        this.shouldSplitOnImpact = additionalData.readBoolean();
        this.splitCount = additionalData.readInt();
        if (additionalData.readBoolean()) {
            this.trailParticle = additionalData.readUtf();
        }
        this.particleSpread = additionalData.readDouble();
        this.singleParticle = additionalData.readBoolean();
    }
}