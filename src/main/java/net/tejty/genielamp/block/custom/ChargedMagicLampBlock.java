package net.tejty.genielamp.block.custom;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.tejty.genielamp.world.inventory.GenieLampMenu;

public class ChargedMagicLampBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING;
    public static final VoxelShape SHAPE_NORTH;
    public static final VoxelShape SHAPE_EAST;
    public static final VoxelShape SHAPE_SOUTH;
    public static final VoxelShape SHAPE_WEST;
    private static final RandomSource random = RandomSource.create();
    public ChargedMagicLampBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        switch (pState.getValue(FACING)) {
            case NORTH:
                return SHAPE_NORTH;
            case EAST:
                return SHAPE_EAST;
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
        }
        return SHAPE_NORTH;
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        SHAPE_NORTH = Shapes.or(
                box(6, 0, 6, 10, 1, 10),
                box(7, 1, 7, 9, 2, 9),
                box(6, 2, 5, 10, 6, 11),
                box(7.5, 6, 7.5, 8.5, 7, 8.5),
                box(7, 7, 7, 9, 8, 9),
                box(7.9, 2, 11, 8.1, 8, 15),
                box(7, 3, 3, 9, 4, 5),
                box(7, 4, 0, 9, 6, 5)
        );
        SHAPE_EAST = Shapes.or(
                box(6, 0, 6, 10, 1, 10),
                box(7, 1, 7, 9, 2, 9),
                box(5, 2, 6, 11, 6, 10),
                box(7.5, 6, 7.5, 8.5, 7, 8.5),
                box(7, 7, 7, 9, 8, 9),
                box(1, 2, 7.9, 5, 8, 8.1),
                box(11, 3, 7, 13, 4, 9),
                box(11, 4, 7, 16, 6, 9)
        );
        SHAPE_SOUTH = Shapes.or(
                box(6, 0, 6, 10, 1, 10),
                box(7, 1, 7, 9, 2, 9),
                box(6, 2, 5, 10, 6, 11),
                box(7.5, 6, 7.5, 8.5, 7, 8.5),
                box(7, 7, 7, 9, 8, 9),
                box(7.9, 2, 1, 8.1, 8, 5),
                box(7, 3, 11, 9, 4, 13),
                box(7, 4, 11, 9, 6, 16)
        );
        SHAPE_WEST = Shapes.or(
                box(6, 0, 6, 10, 1, 10),
                box(7, 1, 7, 9, 2, 9),
                box(5, 2, 6, 11, 6, 10),
                box(7.5, 6, 7.5, 8.5, 7, 8.5),
                box(7, 7, 7, 9, 8, 9),
                box(11, 2, 7.9, 15, 8, 8.1),
                box(3, 3, 7, 5, 4, 9),
                box(0, 4, 7, 5, 6, 9)
        );
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer == null)
            return InteractionResult.FAIL;
        if (pPlayer instanceof ServerPlayer _ent) {
            NetworkHooks.openScreen((ServerPlayer) _ent, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("Wishing Screen");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                    return new GenieLampMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pPos));
                }
            }, pPos);
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        for (int i = 0; i < 5; i++) {
            pLevel.addParticle(
                    ParticleTypes.DOLPHIN,
                    pPos.getCenter().x + pState.getValue(FACING).step().x * 0.45,
                    pPos.getCenter().y - 0.1,
                    pPos.getCenter().z + pState.getValue(FACING).step().z * 0.45,
                    0,
                    0.3,
                    0
            );
        }
        if (random.nextInt(0, 5) == 0) {
            pLevel.addParticle(
                    ParticleTypes.GLOW,
                    pPos.getX() + 0.2 + random.nextDouble() * 0.6,
                    pPos.getY() + 0.0 + random.nextDouble() * 0.6,
                    pPos.getZ() + 0.2 + random.nextDouble() * 0.6,
                    0,
                    0.2,
                    0
            );
        }
        if (random.nextInt(0, 5) == 0) {
            pLevel.playLocalSound(pPos, SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, SoundSource.BLOCKS, 0.5F, 0.5F, true);
        }
    }
}
