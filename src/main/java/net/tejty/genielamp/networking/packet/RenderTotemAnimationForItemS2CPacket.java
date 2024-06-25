package net.tejty.genielamp.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RenderTotemAnimationForItemS2CPacket {
    private final ItemStack itemStack;
    public RenderTotemAnimationForItemS2CPacket(ItemStack itemStack){
        this.itemStack = itemStack;
    }

    public RenderTotemAnimationForItemS2CPacket(FriendlyByteBuf buf){
        this.itemStack = buf.readItem();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeItem(this.itemStack);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            // CLIENT SIDE

            Minecraft.getInstance().gameRenderer.displayItemActivation(itemStack);
        });

        return true;
    }
}
