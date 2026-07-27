package net.sophka.polaroid.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.sophka.polaroid.config.FilmMode;
import net.sophka.polaroid.config.ServerConfig;
import net.sophka.polaroid.data.darkslide.DarkslideManager;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.init.ModItems;
import net.sophka.polaroid.server.photo.ServerPhotoTaker;
import net.sophka.polaroid.world.item.component.CameraCartridge;
import net.sophka.polaroid.world.item.component.FilmContent;
import org.jspecify.annotations.Nullable;

import java.util.List;

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
        private boolean manualControl = false;

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

        public CameraProperties withManualControl(){
            return withManualControl(true);
        }

        public CameraProperties withManualControl(boolean manualControl){
            this.manualControl = manualControl;
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

        public boolean hasManualControl(){
            return this.manualControl;
        }
    }

    public static int MAX_SLIDES = 10;
    public static int EXPOSURE_DELTA = 4;
    private static final int BAR_COLOR = ARGB.colorFromFloat(1.0F, 0.44F, 0.53F, 1.0F);

    public final CameraProperties cameraProperties;

    public CameraItem(Properties properties, CameraProperties cameraProperties) {
        super(properties.component(ModDataComponents.FILM_CONTENT, FilmContent.EMPTY).component(ModDataComponents.EXPOSURE,0).component(ModDataComponents.CAMERA_CARTRIDGE,CameraCartridge.EMPTY).component(ModDataComponents.INITIALIZED, false));
        this.cameraProperties = cameraProperties;
    }

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

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
        return ItemUseAnimation.BOW;
    }

    public static FilmContent filmContent(ItemStack stack){
        if(ServerConfig.CAMERA_FILM_MODE.get() == FilmMode.DIRECT){
            return stack.getOrDefault(ModDataComponents.FILM_CONTENT.get(), FilmContent.EMPTY);
        }
        else{
            return stack.getOrDefault(ModDataComponents.CAMERA_CARTRIDGE.get(), CameraCartridge.EMPTY).filmContent();
        }
    }

    public static void updateFilmContent(ItemStack stack, FilmContent content){
        if(ServerConfig.CAMERA_FILM_MODE.get() == FilmMode.DIRECT){
            stack.set(ModDataComponents.FILM_CONTENT, content);
        }
        else {
            CameraCartridge cameraCartridge = stack.getOrDefault(ModDataComponents.CAMERA_CARTRIDGE.get(), CameraCartridge.EMPTY);
            CameraCartridge.Mutable mutable = new CameraCartridge.Mutable(cameraCartridge);
            if (!mutable.getCartridgeStack().isEmpty()) {
                mutable.getCartridgeStack().set(ModDataComponents.FILM_CONTENT, content);
                stack.set(ModDataComponents.CAMERA_CARTRIDGE, mutable.toImmutable());
            }
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return filmContent(stack).count() > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.min(Mth.mulAndTruncate(filmContent(stack).fraction(), 13), 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack self, Slot slot, ClickAction clickAction, Player player) {
        if(ServerConfig.CAMERA_FILM_MODE.get() != FilmMode.CARTRIDGE){
            return super.overrideStackedOnOther(self, slot, clickAction, player);
        }
        CameraCartridge cameraCartridge = self.getOrDefault(ModDataComponents.CAMERA_CARTRIDGE, CameraCartridge.EMPTY);
        ItemStack other = slot.getItem();

        CameraCartridge.Mutable mutable = new CameraCartridge.Mutable(cameraCartridge);
        if (clickAction == ClickAction.SECONDARY && other.isEmpty() && mutable.filmCount() == 0) {
            ItemStack itemStack = mutable.getCartridgeStack();
            slot.safeInsert(itemStack);
            self.set(ModDataComponents.CAMERA_CARTRIDGE, mutable.toImmutable());
            this.broadcastChangesOnContainerMenu(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess carriedItem) {
        if (self.getCount() != 1) return false;
        if (clickAction == ClickAction.PRIMARY && other.isEmpty()) {
            return false;
        } else {
            if(ServerConfig.CAMERA_FILM_MODE.get() == FilmMode.CARTRIDGE){
                return handleCartridgeInsert(self, other, slot, clickAction, player, carriedItem);
            }
            return handleFilmInsert(self, other, slot, clickAction, player, carriedItem);
        }
    }

    private boolean handleCartridgeInsert(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess carriedItem){
        CameraCartridge initialContents = self.get(ModDataComponents.CAMERA_CARTRIDGE.get());
        if (initialContents == null) {
            return false;
        } else {
            CameraCartridge.Mutable contents = new CameraCartridge.Mutable(initialContents);
            if (clickAction == ClickAction.PRIMARY && !other.isEmpty()) {
                contents.tryInsert(other);
                self.set(ModDataComponents.CAMERA_CARTRIDGE.get(), contents.toImmutable());
                if(contents.filmCount() >= 8){
                    List<Identifier> darkslides = DarkslideManager.INSTANCE.darkslideIdentifiers();
                    if(!darkslides.isEmpty()){
                        //TODO: Send packet because Mojang does not like consistency
                        ItemStack darkslide = new ItemStack(ModItems.DARKSLIDE.get());
                        darkslide.set(ModDataComponents.DARKSLIDE, darkslides.get(player.getRandom().nextInt(darkslides.size())));
                        ServerPhotoTaker.givePhoto(darkslide, player);
                    }
                }
                this.broadcastChangesOnContainerMenu(player);
                return true;
            }
            return false;
        }
    }

    private boolean handleFilmInsert(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess carriedItem){
        FilmContent initialContents = self.get(ModDataComponents.FILM_CONTENT.get());
        if (initialContents == null) {
            return false;
        } else {
            FilmContent.Mutable contents = new FilmContent.Mutable(initialContents);
            if (clickAction == ClickAction.PRIMARY && !other.isEmpty()) {
                contents.tryInsert(other);
                self.set(ModDataComponents.FILM_CONTENT.get(), contents.toImmutable());
                this.broadcastChangesOnContainerMenu(player);
                return true;
            }
            return false;
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
        FilmContent filmContent = filmContent(cameraStack);
        if(filmContent == null){
            return false;
        }
        ItemStack filmStack = filmContent.getFilmStack();
        return !filmStack.isEmpty() && filmStack.getItem() instanceof FilmItem;
    }

    public void playEmptySound(Level level, BlockPos pos){
        level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.3F,
                1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if(!itemStack.is(ModItems.CAMERA_660_AF) || itemStack.getOrDefault(ModDataComponents.INITIALIZED, false)){
            return;
        }
        itemStack.set(ModDataComponents.INITIALIZED, true);
        if(level.getRandom().nextFloat() < 0.05){
            itemStack.set(ModDataComponents.LAND_CAMERA, true);
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        if(stack.getOrDefault(ModDataComponents.LAND_CAMERA, false)){
            return Component.translatable(getDescriptionId() + ".land");
        }
        return super.getName(stack);
    }

}
