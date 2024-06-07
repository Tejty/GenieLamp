package net.tejty.genielamp.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.tejty.genielamp.GenieLamp;
import net.tejty.genielamp.block.custom.ChargedMagicLampBlock;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GenieLamp.MODID);

    public static final RegistryObject<Block> CHARGED_MAGIC_LAMP = BLOCKS.register("charged_magic_lamp",
            () -> new ChargedMagicLampBlock(
                    BlockBehaviour.Properties.of()
                            .instabreak()
                            .lightLevel((state) -> 8)
                            .noOcclusion()
                            .noParticlesOnBreak()
                            .sound(SoundType.AMETHYST)
                            .pushReaction(PushReaction.DESTROY)
            )
    );

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
