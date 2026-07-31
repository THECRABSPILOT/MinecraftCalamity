package crab.mods.minecraftcalamity.entity;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import crab.mods.minecraftcalamity.entity.custom.CaveWizardEntity;
import crab.mods.minecraftcalamity.entity.custom.DynamicProjectileEntity;
import crab.mods.minecraftcalamity.entity.custom.SwordProjectileEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MinecraftCalamity.MODID);

    public static final RegistryObject<EntityType<CaveWizardEntity>> CAVE_WIZARD =
            ENTITY_TYPES.register("cave_wizard", () -> EntityType.Builder.of(CaveWizardEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f) // Similar hitbox size to a skeleton or evoker
                    .clientTrackingRange(8)
                    .build(new ResourceLocation(MinecraftCalamity.MODID, "cave_wizard").toString()));

    public static final RegistryObject<EntityType<DynamicProjectileEntity>> DYNAMIC_PROJECTILE =
            ENTITY_TYPES.register("dynamic_projectile", () ->
                    EntityType.Builder.<DynamicProjectileEntity>of(DynamicProjectileEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F) // Adjust hitbox size as needed
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("dynamic_projectile"));

    public static final RegistryObject<EntityType<SwordProjectileEntity>> SWORD_PROJECTILE =
            ENTITY_TYPES.register("sword_projectile", () ->
                    EntityType.Builder.<SwordProjectileEntity>of(SwordProjectileEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F) // Adjust hitbox size as needed
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("sword_projectile"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}