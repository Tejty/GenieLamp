package net.tejty.genielamp.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tejty.genielamp.GenieLamp;
import net.tejty.genielamp.item.ModItems;

public class ModEvents {
    @Mod.EventBusSubscriber(modid = GenieLamp.MODID)
    public static class ForgeEvents{
        /*@SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event){
            if (event.getEntity() instanceof Player player){
                player.displayClientMessage(Component.literal("giving you lamp"), false);
                player.addItem(new ItemStack(ModItems.CHARGED_MAGIC_LAMP.get(), 1));
            }

        }*/
    }
}
