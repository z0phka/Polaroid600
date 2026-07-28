package net.sophka.polaroid.world.item;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.sophka.polaroid.config.ServerConfig;
import net.sophka.polaroid.init.ModBlocks;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.utils.Utils;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class PhotoItem extends BlockItem {

    public PhotoItem(Properties properties) {
        super(null,properties);
    }

    @Override
    public Block getBlock() {
        return ModBlocks.PHOTO.get();
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult place(BlockPlaceContext placeContext) {
        if (!this.getBlock().isEnabled(placeContext.getLevel().enabledFeatures())) {
            return InteractionResult.FAIL;
        } else if (!placeContext.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext updatedPlaceContext = this.updatePlacementContext(placeContext);
            if (updatedPlaceContext == null) {
                return InteractionResult.FAIL;
            } else {
                BlockState placementState = this.getPlacementState(updatedPlaceContext);
                if (placementState == null) {
                    return InteractionResult.FAIL;
                } else if (!this.placeBlock(updatedPlaceContext, placementState)) {
                    return InteractionResult.FAIL;
                } else {
                    BlockPos pos = updatedPlaceContext.getClickedPos();
                    Level level = updatedPlaceContext.getLevel();
                    Player player = updatedPlaceContext.getPlayer();
                    ItemStack itemStack = updatedPlaceContext.getItemInHand();
                    BlockState placedState = level.getBlockState(pos);
                    if (placedState.is(placementState.getBlock())) {
                        this.updateCustomBlockEntityTag(pos, level, player, itemStack, placedState);
                        placedState.getBlock().setPlacedBy(level, pos, placedState, player, itemStack);
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, pos, itemStack);
                            BlockHitResult oldResult = placeContext.getHitResult();
                            BlockHitResult blockHitResult = new BlockHitResult(
                                    oldResult.getLocation()
                                            .subtract(oldResult.getBlockPos().getX(),oldResult.getBlockPos().getY(),oldResult.getBlockPos().getZ())
                                            .add(pos.getX(), pos.getY(), pos.getZ()),
                                    oldResult.getDirection(),
                                    pos,
                                    oldResult.isInside());

                            placedState.useItemOn(itemStack,level, player, placeContext.getHand(), blockHitResult);
                        }
                    }

                    SoundType soundType = placedState.getSoundType(level, pos, placeContext.getPlayer());
                    level.playSound(
                            player, pos, this.getPlaceSound(placedState, level, pos, placeContext.getPlayer()), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F
                    );
                    level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, placedState));

                    itemStack.consume(1, player);
                    return InteractionResult.SUCCESS;
                }
            }
        }
    }

    public static long timeSinceCreation(Level level, ItemStack stack){
        return level.getGameTime() - stack.getOrDefault(ModDataComponents.CREATED_TIME,0L);
    }

    public static double developmentProgress(Level level, ItemStack stack){
        return Utils.clampUnit((double) timeSinceCreation(level, stack) / ServerConfig.DEVELOPMENT_TIME.get());
    }

    public static boolean developed(Level level, ItemStack stack){
        return developmentProgress(level, stack) >= 1;
    }

    public static boolean sunDamageSusceptible(Level level, ItemStack stack){
        return ServerConfig.SOLARIZATION_ENABLED.get() && timeSinceCreation(level, stack) < ServerConfig.SOLARIZATION_TIME.get();
    }

    public static double sunDamage(Level level, ItemStack stack){
        return ServerConfig.SOLARIZATION_ENABLED.get() ?
                Utils.clampUnit(
                        Math.max(stack.getOrDefault(ModDataComponents.SUN_DAMAGE,0) - ServerConfig.SOLARIZATION_GRACE_AMOUNT.get(),0) /
                                (float)Math.max(ServerConfig.SOLARIZATION_TIME.get() - ServerConfig.SOLARIZATION_GRACE_AMOUNT.get(), 1)) :
                0;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if((slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND) || !sunDamageSusceptible(level, itemStack)){
            return;
        }
        BlockPos roundedPos = BlockPos.containing(owner.getX(), owner.getEyeY(), owner.getZ());
        if(level.isRaining() || !level.canSeeSky(roundedPos)){
            return;
        }
        float light = owner.getLightLevelDependentMagicValue();
        if(light < 0.5){
            return;
        }
        itemStack.set(ModDataComponents.SUN_DAMAGE, itemStack.getOrDefault(ModDataComponents.SUN_DAMAGE,0) + 1);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack != newStack &&
                ((oldStack.getItem() != newStack.getItem()) ||
                        !oldStack.has(ModDataComponents.PHOTO) ||
                        !newStack.has(ModDataComponents.PHOTO) ||
                        !Objects.equals(oldStack.get(ModDataComponents.PHOTO), newStack.get(ModDataComponents.PHOTO)));
    }
}
