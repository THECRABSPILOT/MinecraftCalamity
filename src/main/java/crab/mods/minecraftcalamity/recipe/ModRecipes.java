package crab.mods.minecraftcalamity.recipe;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MinecraftCalamity.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MinecraftCalamity.MODID);

    public static final RegistryObject<RecipeSerializer<HellforgeRecipe>> HELLFORGE_SERIALIZER =
            SERIALIZERS.register("hellforge", () -> HellforgeRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeType<HellforgeRecipe>> HELLFORGE_TYPE =
            TYPES.register("hellforge", () -> HellforgeRecipe.Type.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}