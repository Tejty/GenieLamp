package net.tejty.genielamp.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tejty.genielamp.GenieLamp;
import net.tejty.genielamp.item.ModItems;

import java.util.List;

@Mod.EventBusSubscriber(modid = GenieLamp.MODID)
public class ModEvents {
    /*@Mod.EventBusSubscriber(modid = GenieLamp.MODID)
    public static class ForgeEvents{
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event){
            if (event.getEntity() instanceof Player player){
                player.displayClientMessage(Component.literal("giving you lamp"), false);
                player.addItem(new ItemStack(ModItems.CHARGED_MAGIC_LAMP.get(), 1));
            }

        }
    }*/

    @SubscribeEvent
    public static void addCustomWanderingTrades(WandererTradesEvent event){
        List<VillagerTrades.ItemListing> trades = event.getGenericTrades();

        trades.add((pTrader, pRandom) -> new MerchantOffer(
                new ItemStack(Items.EMERALD_BLOCK, 64),
                new ItemStack(ModItems.OLD_LAMP_SCRAP.get(), 1),
                30, 1, 1.5f
        ));
    }
}
