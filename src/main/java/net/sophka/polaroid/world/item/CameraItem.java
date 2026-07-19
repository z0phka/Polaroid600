package net.sophka.polaroid.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.server.photo.ServerPhotoTaker;
import net.sophka.polaroid.world.item.component.CameraFilm;

public class CameraItem extends Item {

    //TODO: Idk, maybe add a builder to this or something to keep it final
    public static class CameraProperties{
        public enum FlashMode{
            NEVER,
            ALWAYS,
            OPTIONAL
        }
        private float fov = 35;
        private FilmFormat filmFormat = FilmFormat._600;
        private boolean doubleExposure;
        private boolean AF;
        private boolean timer;
        private boolean selfieMirror;
        private FlashMode flashMode = FlashMode.NEVER;

        public CameraProperties withFOV(float fov){
            this.fov = fov;
            return this;
        }

        public CameraProperties withFilmFormat(FilmFormat format){
            this.filmFormat = format;
            return this;
        }

        public CameraProperties withDoubleExposure(boolean doubleExposure){
            this.doubleExposure = doubleExposure;
            return this;
        }

        public CameraProperties withDoubleExposure(){
            return withDoubleExposure(true);
        }

        public CameraProperties withAF(boolean AF){
            this.AF = AF;
            return this;
        }

        public CameraProperties withAF(){
            return withAF(true);
        }

        public CameraProperties withTimer(){
            return withTimer(true);
        }

        public CameraProperties withTimer(boolean timer){
            this.timer = timer;
            return this;
        }

        public CameraProperties withSelfieMirror(){
            return withSelfieMirror(true);
        }

        public CameraProperties withSelfieMirror(boolean selfieMirror){
            this.selfieMirror = selfieMirror;
            return this;
        }

        public float getFov() {
            return fov;
        }

        public FilmFormat getFilmFormat() {
            return filmFormat;
        }

        public boolean hasDoubleExposure() {
            return doubleExposure;
        }

        public boolean hasAF() {
            return AF;
        }

        public boolean hasTimer(){
            return this.timer;
        }

        public FlashMode getFlashMode() {
            return this.flashMode;
        }

        public boolean hasSelfieMirror(){
            return this.selfieMirror;
        }
    }

    public static int MAX_SLIDES = 10;
    public static int EXPOSURE_DELTA = 4;
    private static final int BAR_COLOR = ARGB.colorFromFloat(1.0F, 0.44F, 0.53F, 1.0F);

    public final CameraProperties cameraProperties;

    public CameraItem(Properties properties, CameraProperties cameraProperties) {
        super(properties);
        this.cameraProperties = cameraProperties;
    }

    /*public int getUseDuration(ItemStack itemStack, LivingEntity user) {
        return 2000;
    }*/


    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }
        if(!canShoot(itemStack)){
            playEmptySound(level, serverPlayer.blockPosition());
            return InteractionResult.PASS;
        }
        ServerPhotoTaker.takePhoto(serverPlayer, itemStack);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS;
    }
/*
    public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int remainingTime) {
        if(level.isClientSide()){
            return false;
        }
        if (!(entity instanceof ServerPlayer player)) {
            return false;
        }
        if (remainingTime < 200) return false;
        if(!canShoot(itemStack)){
            playEmptySound(level, player.blockPosition());
            return false;
        }
        ServerPhotoTaker.takePhoto(player, itemStack);

        if(level.isClientSide()){
            if(player.isCrouching()){
                ModClientEventHandler.selfieMode = !ModClientEventHandler.selfieMode;
            }
            else{
                PhotoTaker.instance().takePhoto(itemStack, null, -1);
            }
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return true;
    }*/

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        CameraFilm content = stack.getOrDefault(ModDataComponents.CAMERA_FILM.get(), CameraFilm.EMPTY);
        return content.count() > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        CameraFilm content = stack.getOrDefault(ModDataComponents.CAMERA_FILM.get(), CameraFilm.EMPTY);
        return Math.min(Mth.mulAndTruncate(content.fraction(), 13), 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess carriedItem) {
        if (self.getCount() != 1) return false;
        if (clickAction == ClickAction.PRIMARY && other.isEmpty()) {
            return false;
        } else {
            CameraFilm initialContents = self.get(ModDataComponents.CAMERA_FILM.get());
            if (initialContents == null) {
                return false;
            } else {
                CameraFilm.Mutable contents = new CameraFilm.Mutable(initialContents);
                if (clickAction == ClickAction.PRIMARY && !other.isEmpty()) {
                    if (slot.allowModification(player) && contents.tryInsert(other) > 0) {
                        //playInsertSound(player);
                    } else {
                        //playInsertFailSound(player);
                    }

                    self.set(ModDataComponents.CAMERA_FILM.get(), contents.toImmutable());
                    this.broadcastChangesOnContainerMenu(player);
                    return true;
                }
                return false;
            }
        }
    }

    private void broadcastChangesOnContainerMenu(Player player) {
        AbstractContainerMenu containerMenu = player.containerMenu;
        if (containerMenu != null) {
            containerMenu.slotsChanged(player.getInventory());
        }
    }

    public float getFov(){
        return this.cameraProperties.getFov();
    }

    public boolean twoHanded(){
        return this.cameraProperties.filmFormat.twoHanded();
    }

    public boolean canShoot(ItemStack cameraStack) {
        CameraFilm cameraFilm = cameraStack.get(ModDataComponents.CAMERA_FILM.get());
        if(cameraFilm == null){
            return false;
        }
        ItemStack filmStack = cameraFilm.getFilmStack();
        return !filmStack.isEmpty() && filmStack.getItem() instanceof FilmItem;
    }

    public void playEmptySound(Level level, BlockPos pos){
        level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.3F,
                1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
    }

}
