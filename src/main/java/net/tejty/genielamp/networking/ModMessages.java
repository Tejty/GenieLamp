package net.tejty.genielamp.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.tejty.genielamp.GenieLamp;
import net.tejty.genielamp.networking.packet.ItemWishingC2SPacket;
import net.tejty.genielamp.networking.packet.RenderTotemAnimationForItemS2CPacket;
import net.tejty.genielamp.networking.packet.WishingScreenOpenS2CPacket;

public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id(){
        return packetId++;
    }

    public static void register(){
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(GenieLamp.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(ItemWishingC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ItemWishingC2SPacket::new)
                .encoder(ItemWishingC2SPacket::toBytes)
                .consumerMainThread(ItemWishingC2SPacket::handle)
                .add();
        net.messageBuilder(WishingScreenOpenS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(WishingScreenOpenS2CPacket::new)
                .encoder(WishingScreenOpenS2CPacket::toBytes)
                .consumerMainThread(WishingScreenOpenS2CPacket::handle)
                .add();
        net.messageBuilder(RenderTotemAnimationForItemS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(RenderTotemAnimationForItemS2CPacket::new)
                .encoder(RenderTotemAnimationForItemS2CPacket::toBytes)
                .consumerMainThread(RenderTotemAnimationForItemS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message){
        INSTANCE.sendToServer(message);
    }
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
