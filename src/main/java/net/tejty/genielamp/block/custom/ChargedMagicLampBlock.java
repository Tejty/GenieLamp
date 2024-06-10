package net.tejty.genielamp.block.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
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
import net.tejty.genielamp.client.gui.screens.WishingScreen;
import org.jetbrains.annotations.NotNull;

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
        if (pLevel.isClientSide) {
            Minecraft.getInstance().setScreen(new WishingScreen(pPlayer));
        }

        return InteractionResult.SUCCESS;
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
        //ParticleUtils.spawnParticlesAlongAxis(((Direction)pState.getValue(FACING)).getAxis(), pLevel, pPos, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(1, 2));
    }
}
