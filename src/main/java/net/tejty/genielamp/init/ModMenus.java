package net.tejty.genielamp.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.tejty.genielamp.GenieLamp;
import net.tejty.genielamp.world.inventory.GenieLampMenu;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, GenieLamp.MODID);
    public static final RegistryObject<MenuType<GenieLampMenu>> GENIE_LAMP = REGISTRY.register("genie_lamp", () -> IForgeMenuType.create(GenieLampMenu::new));

}
