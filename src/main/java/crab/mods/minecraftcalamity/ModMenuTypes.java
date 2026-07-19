package crab.mods.minecraftcalamity;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, "minecraftcalamity");

    // Registers the menu layout using a standard two-argument network supplier
    public static final RegistryObject<MenuType<CalamityCraftingMenu>> CALAMITY_CRAFTING_MENU =
            MENUS.register("calamity_crafting_menu", () ->
                    IForgeMenuType.create((windowId, inv, data) -> new CalamityCraftingMenu(windowId, inv))
            );

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
