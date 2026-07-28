package net.sophka.polaroid.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.client.gui.screens.PhotoScreen;
import net.sophka.polaroid.world.block.entity.PhotoBlockEntity;
import net.sophka.polaroid.world.item.PhotoItem;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class PhotoBlock extends Block implements EntityBlock, SelectableSlotContainer {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(
            Block.box(0.0, 0.0, 15.75, 16.0, 16.0, 16.0)
    );

    public PhotoBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new PhotoBlockEntity(worldPosition, blockState);
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }


    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
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

    @Override
    public int getRows() {
        return PhotoBlockEntity.ROWS;
    }

    @Override
    public int getColumns() {
        return PhotoBlockEntity.COLUMNS;
    }

    private void playSound(LevelAccessor level, BlockPos pos, SoundEvent sound) {
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
    ) {
        if (level.getBlockEntity(pos) instanceof PhotoBlockEntity photoBlockEntity && !hand.equals(InteractionHand.OFF_HAND)) {
            OptionalInt hitSlot = this.getHitSlot(hitResult, state.getValue(FACING));
            if (hitSlot.isEmpty()) {
                return InteractionResult.PASS;
            } else {
                if(player.isCrouching()){
                    return InteractionResult.PASS;
                }
                Inventory inventory = player.getInventory();
                if (level.isClientSide()) {
                    return inventory.getSelectedItem().isEmpty() ? InteractionResult.PASS : InteractionResult.SUCCESS;
                } else {
                    boolean itemRemoved = swapSingleItem(itemStack, player, photoBlockEntity, hitSlot.getAsInt(), inventory);
                    if (itemRemoved) {
                        this.playSound(level, pos, itemStack.isEmpty() ? SoundEvents.SHELF_TAKE_ITEM : SoundEvents.SHELF_SINGLE_SWAP);
                        if(photoBlockEntity.isEmpty()){
                            level.removeBlock(pos,false);
                        }
                    } else {
                        if (itemStack.isEmpty()) {
                            return InteractionResult.PASS;
                        }

                        this.playSound(level, pos, SoundEvents.SHELF_PLACE_ITEM);
                    }

                    return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack);
                }
            }
        } else {
            return InteractionResult.PASS;
        }
    }

    private static boolean swapSingleItem(ItemStack itemStack, Player player, PhotoBlockEntity photoBlockEntity, int hitSlot, Inventory inventory) {
        ItemStack removedItem = photoBlockEntity.swapItemNoUpdate(hitSlot, itemStack);
        ItemStack newInventoryItem = player.hasInfiniteMaterials() && removedItem.isEmpty() ? itemStack.copy() : removedItem;
        inventory.setItem(inventory.getSelectedSlot(), newInventoryItem);
        inventory.setChanged();
        photoBlockEntity.setChanged(
                newInventoryItem.has(DataComponents.USE_EFFECTS) && !newInventoryItem.get(DataComponents.USE_EFFECTS).interactVibrations()
                        ? null
                        : GameEvent.ITEM_INTERACT_FINISH
        );

        Polaroid600.LOGGER.debug(photoBlockEntity.getItems().stream().map(ItemStack::toString).collect(Collectors.joining()));
        return !removedItem.isEmpty();
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos relative = pos.relative(direction.getOpposite());
        BlockState blockState = level.getBlockState(relative);
        return direction.getAxis().isHorizontal() && blockState.isFaceSturdy(level, relative, direction);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess ticks,
            BlockPos pos,
            Direction directionToNeighbour,
            BlockPos neighbourPos,
            BlockState neighbourState,
            RandomSource random
    ) {
        return directionToNeighbour.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }
}
