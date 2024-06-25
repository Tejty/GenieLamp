package net.tejty.genielamp.networking.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WishingScreenOpenS2CPacket {
    private final BlockPos pos;
    public WishingScreenOpenS2CPacket(BlockPos pos){
        this.pos = pos;
    }

    public WishingScreenOpenS2CPacket(FriendlyByteBuf buf){
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeBlockPos(this.pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            // CLIENT SIDE

            Player player = context.getSender();
            /*if (player instanceof AbstractClientPlayer client){
               */
            //Minecraft.getInstance().setScreen(new WishingScreen(context.getSender().level(), context.getSender().connection.getPlayer(), pos));
        });

        return true;
    }
}
