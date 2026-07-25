package crab.mods.minecraftcalamity.menu;

import crab.mods.minecraftcalamity.MinecraftCalamity;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, MinecraftCalamity.MODID);

    public static final RegistryObject<MenuType<AccessoryMenu>> ACCESSORY_MENU =
            MENUS.register("accessory_menu", () -> IForgeMenuType.create(AccessoryMenu::new));

    // Register the Hellforge Menu
    public static final RegistryObject<MenuType<HellforgeMenu>> HELLFORGE_MENU =
            MENUS.register("hellforge_menu", () -> IForgeMenuType.create(HellforgeMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}