package net.tejty.genielamp.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.tejty.genielamp.GenieLamp;
import net.tejty.genielamp.block.ModBlocks;
import net.tejty.genielamp.item.custom.ChargedGenieLampItem;
import net.tejty.genielamp.item.custom.GenieLampItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GenieLamp.MODID);

    public static final RegistryObject<Item> MAGIC_LAMP = ITEMS.register("magic_lamp",
            () -> new GenieLampItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> CHARGED_MAGIC_LAMP = ITEMS.register("charged_magic_lamp",
            () -> new ChargedGenieLampItem(ModBlocks.CHARGED_MAGIC_LAMP.get(), new Item.Properties().stacksTo(64).rarity(Rarity.EPIC).fireResistant()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
