package net.tejty.genielamp.networking.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.tejty.genielamp.block.ModBlocks;
import net.tejty.genielamp.config.GenieLampCommonConfigs;

import java.util.List;
import java.util.function.Supplier;

public class ItemWishingC2SPacket {
    private final ItemStack stack;
    private final BlockPos pos;
    public ItemWishingC2SPacket(ItemStack stack, BlockPos pos){
        this.stack = stack;
        this.pos = pos;
    }

    public ItemWishingC2SPacket(FriendlyByteBuf buf){
        this.stack = buf.readItem();
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeItem(this.stack);
        buf.writeBlockPos(this.pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            // SERVER SIDE
            ServerPlayer player = context.getSender();
            ServerLevel level = player.serverLevel();

            //player.addItem(stack);
            if (level.getBlockState(pos).is(ModBlocks.CHARGED_MAGIC_LAMP.get())) {
                List<Item> bannedItems = GenieLampCommonConfigs.BANNED_ITEMS.get().stream()
                        .map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName))).toList();
                if (bannedItems.contains(stack.getItem())){
                    return;
                }
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, stack);
                itemEntity.setPickUpDelay(60);
                itemEntity.lerpMotion(0, 0.05, 0);
                level.addFreshEntity(itemEntity);

                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

                level.sendParticles(ParticleTypes.GLOW, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 50, 0, 0, 0, 0.3);
                level.sendParticles(ParticleTypes.DOLPHIN, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 100, 0.3, 0.3, 0.3, 1);
                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 10, 0, 0, 0, 0.1);
                level.sendParticles(ParticleTypes.SONIC_BOOM, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
                level.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 10, 0, 0.2, 0, 0);

                level.playSound(null, pos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 2f, 0.5f);
            }
        });

        return true;
    }
}
