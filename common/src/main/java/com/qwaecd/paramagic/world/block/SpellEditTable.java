package com.qwaecd.paramagic.world.block;

import com.qwaecd.paramagic.ui.menu.SpellEditTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class SpellEditTable extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final Component CONTAINER_TITLE = Component.literal("Spell Edit Table");
    private static final VoxelShape SHAPE_NORTH = northShape();
    private static final VoxelShape SHAPE_WEST = westShape();
    private static final VoxelShape SHAPE_EAST = eastShape();
    private static final VoxelShape SHAPE_SOUTH = southShape();
    public SpellEditTable() {
        super(Properties.of().sound(SoundType.STONE).strength(2.0F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(this.getMenuProvider(state, level, pos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((i, inventory, player) -> new SpellEditTableMenu(i, inventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public static VoxelShape southShape() {
        return Stream.of(
                Stream.of(
                        Block.box(12, 7, 5, 13, 8, 11),
                        Block.box(3, 7, 5, 4, 8, 11),
                        Block.box(5, 7, 12, 11, 8, 13),
                        Block.box(5, 7, 3, 11, 8, 4)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
                Stream.of(
                        Block.box(3, 0, 11, 5, 11, 13),
                        Block.box(3, 0, 3, 5, 11, 5),
                        Block.box(11, 0, 3, 13, 11, 5),
                        Block.box(11, 0, 11, 13, 11, 13)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
                Shapes.join(Stream.of(
                        Block.box(8, 7, 1, 9, 8, 1),
                        Block.box(7, 8, 1, 9, 9, 1),
                        Block.box(6, 9, 1, 9, 10, 1),
                        Block.box(6, 10, 1, 10, 11, 1)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), Block.box(1, 11, 1, 15, 13, 15), BooleanOp.OR)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }

    public static VoxelShape northShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.6875, 0, 0.1875, 0.8125, 0.6875, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0, 0.6875, 0.8125, 0.6875, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.6875, 0.3125, 0.6875, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.1875, 0.3125, 0.6875, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.4375, 0.3125, 0.25, 0.5, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0.4375, 0.3125, 0.8125, 0.5, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.4375, 0.1875, 0.6875, 0.5, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.4375, 0.75, 0.6875, 0.5, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.6875, 0.0625, 0.9375, 0.8125, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.5, 0.9375, 0.5625, 0.5625, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.625, 0.9375, 0.625, 0.6875, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.5625, 0.9375, 0.625, 0.625, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.4375, 0.9375, 0.5, 0.5, 0.9375), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape eastShape() {
        return Stream.of(
                Stream.of(
                        Block.box(5, 7, 3, 11, 8, 4),
                        Block.box(5, 7, 12, 11, 8, 13),
                        Block.box(12, 7, 5, 13, 8, 11),
                        Block.box(3, 7, 5, 4, 8, 11)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
        Stream.of(
                Block.box(11, 0, 11, 13, 11, 13),
                Block.box(3, 0, 11, 5, 11, 13),
                Block.box(3, 0, 3, 5, 11, 5),
                Block.box(11, 0, 3, 13, 11, 5)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
        Shapes.join(Stream.of(
                Block.box(1, 7, 7, 1, 8, 8),
                Block.box(1, 8, 7, 1, 9, 9),
                Block.box(1, 9, 7, 1, 10, 10),
                Block.box(1, 10, 6, 1, 11, 10)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), Block.box(1, 11, 1, 15, 13, 15), BooleanOp.OR)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }

    public static VoxelShape westShape() {
        return Stream.of(
                Stream.of(
                        Block.box(5, 7, 12, 11, 8, 13),
                        Block.box(5, 7, 3, 11, 8, 4),
                        Block.box(3, 7, 5, 4, 8, 11),
                        Block.box(12, 7, 5, 13, 8, 11)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
        Stream.of(
                Block.box(3, 0, 3, 5, 11, 5),
                Block.box(11, 0, 3, 13, 11, 5),
                Block.box(11, 0, 11, 13, 11, 13),
                Block.box(3, 0, 11, 5, 11, 13)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
        Shapes.join(Stream.of(
                Block.box(15, 7, 8, 15, 8, 9),
                Block.box(15, 8, 7, 15, 9, 9),
                Block.box(15, 9, 6, 15, 10, 9),
                Block.box(15, 10, 6, 15, 11, 10)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), Block.box(1, 11, 1, 15, 13, 15), BooleanOp.OR)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }


}
