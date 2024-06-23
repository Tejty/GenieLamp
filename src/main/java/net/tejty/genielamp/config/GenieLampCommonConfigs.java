package net.tejty.genielamp.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import net.tejty.genielamp.Config;

import java.util.Arrays;
import java.util.List;

public class GenieLampCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BANNED_ITEMS;
    public static final ForgeConfigSpec.ConfigValue<Integer> XP_AMOUNT;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName.replaceAll("\\s", "")));
    }

    static {
        BUILDER.push("Config for Genie Lamp");

        List<String> defaultBannedItems = List.of(
                "minecraft:reinforced_deepslate",
                "minecraft:dragon_egg",
                "minecraft:end_portal_frame",
                "minecraft:bedrock",
                "minecraft:command_block",
                "minecraft:chain_command_block",
                "minecraft:repeating_command_block",
                "minecraft:command_block_minecart",
                "minecraft:jigsaw",
                "minecraft:structure_block",
                "minecraft:structure_void",
                "minecraft:debug_stick",
                "minecraft:light",
                "minecraft:barrier"
        );
        BANNED_ITEMS = BUILDER.comment("List of banned items that will not be able to wish")
                .defineListAllowEmpty("Blocked items", defaultBannedItems, GenieLampCommonConfigs::validateItemName);

        XP_AMOUNT = BUILDER.comment("Amount of XP levels required to charge lamp")
                .define("XP amount", 100);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
