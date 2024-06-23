package net.tejty.genielamp.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChargedGenieLampItem extends BlockItem {
    public ChargedGenieLampItem(Block block, Item.Properties properties){
        super(block, properties);
    }

    public boolean isFoil(@NotNull ItemStack pStack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable("item.genie_lamp.charged_magic_lamp").withStyle(ChatFormatting.GOLD));

        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }
}
