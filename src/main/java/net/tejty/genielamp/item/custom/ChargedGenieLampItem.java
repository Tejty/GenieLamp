package net.tejty.genielamp.item.custom;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ChargedGenieLampItem extends BlockItem {
    public ChargedGenieLampItem(Block block, Item.Properties properties){
        super(block, properties);
    }

    public boolean isFoil(@NotNull ItemStack pStack) {
        return true;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.BRUSH;
        //return super.getUseAnimation(pStack);
    }
}
