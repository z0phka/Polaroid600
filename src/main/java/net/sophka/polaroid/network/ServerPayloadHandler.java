package net.sophka.polaroid.network;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.init.ModItems;
import net.sophka.polaroid.server.photo.PhotoTokenManager;
import net.sophka.polaroid.server.photo.ServerPhotoTaker;
import net.sophka.polaroid.utils.Utils;
import net.sophka.polaroid.world.item.CameraItem;
import net.sophka.polaroid.world.item.FilmFormat;
import net.sophka.polaroid.world.item.FilmItem;
import net.sophka.polaroid.world.item.component.FilmContent;
import net.sophka.polaroid.world.item.component.DoubleExposure;

import java.io.*;
import java.nio.file.Path;
import java.util.Optional;

public class ServerPayloadHandler {

    private static boolean verifyIncomingPhotoPayload(PhotoDataPayload data, Player player){
        if(data.token() != -1){
            return PhotoTokenManager.getInstance().verify(data.token(), player);
        }
        return false;
    }

    public static void handlePhotoData(final PhotoDataPayload data, final IPayloadContext context) {
        Polaroid600.LOGGER.debug("{} bytes received", data.data().length);
        Player player = context.player();
        if(!verifyIncomingPhotoPayload(data, player)){
            return;
        }

        PhotoTokenManager.getInstance().get(data.token()).ifPresent(entry -> {
            PhotoTokenManager.getInstance().invalidateToken(data.token());
            handlePhotoData(data, context, entry);
        });
    }

    private static void handlePhotoData(final PhotoDataPayload data, final IPayloadContext context, PhotoTokenManager.Entry entry) {
        ItemStack cameraStack = entry.camera().get();
        FilmContent filmContent = CameraItem.filmContent(cameraStack);
        FilmContent.Mutable mutableCameraFilm = new FilmContent.Mutable(filmContent);
        DoubleExposure doubleExposure = cameraStack.getOrDefault(ModDataComponents.DOUBLE_EXPOSURE, DoubleExposure.OFF);
        DoubleExposure.Mutable mutableDoubleExposure = new DoubleExposure.Mutable(doubleExposure);

        MinecraftServer server = context.player().level().getServer();
        Path path = server.getWorldPath(Utils.PHOTOS);

        try (NativeImage scaled = data.toImage()) {
            File file = doubleExposure.isPrimed() ?
                    new File(path.toFile(), doubleExposure.getPartialImage()) :
                    getFile(data.format(), path.toFile());

            if(doubleExposure.isPrimed()){
                doubleExposure(file, scaled);
            }

            file.getParentFile().mkdirs();
            scaled.writeToFile(file);
            String id = data.format().name + '/' + file.getName();
            Player player = context.player();
            context.enqueueWork(() -> {
                if (doubleExposure.getState() == DoubleExposure.State.ON) {
                    mutableDoubleExposure.storePartialImage(id);
                    cameraStack.set(ModDataComponents.DOUBLE_EXPOSURE, mutableDoubleExposure.toImmutable());
                }
                else{
                    ItemStack photoStack = photoStack(id, cameraStack, player);
                    if(photoStack.isEmpty()){
                        return;
                    }
                    if(entry.consumeFilm()){
                        mutableCameraFilm.getFilm().shrink(1);
                    }
                    CameraItem.updateFilmContent(cameraStack, mutableCameraFilm.toImmutable());
                    if (doubleExposure.isOn()) {
                        mutableDoubleExposure.turnOff();
                        cameraStack.set(ModDataComponents.DOUBLE_EXPOSURE, mutableDoubleExposure.toImmutable());
                    }
                    entry.post().accept(photoStack);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void doubleExposure(File file, NativeImage scaled) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            NativeImage nativeImage = NativeImage.read(fis);
            for(int x = 0; x < scaled.getWidth(); x++){
                for (int y = 0; y < scaled.getHeight(); y++){
                    int colorA = nativeImage.getPixel(x,y);
                    int colorB = scaled.getPixel(x,y);

                    int red = Math.clamp(ARGB.red(colorA) + ARGB.red(colorB),0,255);
                    int green = Math.clamp(ARGB.green(colorA) + ARGB.green(colorB),0,255);
                    int blue = Math.clamp(ARGB.blue(colorA) + ARGB.blue(colorB),0,255);

                    scaled.setPixel(x,y,ARGB.color(
                            255,
                            red,
                            green,
                            blue));
                }
            }
        }
    }

    private static ItemStack photoStack(String id, ItemStack cameraStack, Player player){
        if (cameraStack.isEmpty() || !(cameraStack.getItem() instanceof CameraItem cameraItem)) {
            return ItemStack.EMPTY;
        }
        Level level = player.level();
        FilmContent filmContent = CameraItem.filmContent(cameraStack);
        if (filmContent == null) {
            return ItemStack.EMPTY;
        }
        ItemStack filmStack = filmContent.getFilmStack();
        if (filmStack.isEmpty() || !(filmStack.getItem() instanceof FilmItem filmItem)) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = new ItemStack(ModItems.PHOTO.get());
        itemStack.set(ModDataComponents.PHOTO, id);
        itemStack.set(ModDataComponents.CREATED_TIME, level.getGameTime());
        itemStack.set(ModDataComponents.FILM_TYPE, filmItem.filmType);
        itemStack.set(ModDataComponents.FILM_FORMAT, cameraItem.cameraProperties.getFilmFormat());

        return itemStack;
    }

    private static File getFile(FilmFormat format, File picDir) {
        String name = Util.getFilenameFormattedDateTime();
        int count = 1;

        while (true) {
            File file = new File(picDir, format.name + "/" + name + (count == 1 ? "" : "_" + count) + ".png");
            if (!file.exists()) {
                return file;
            }

            count++;
        }
    }

    public static void handlePhotoRequest(final PhotoRequestPayload data, final IPayloadContext context) {
        MinecraftServer server = context.player().level().getServer();
        Path path = server.getWorldPath(Utils.PHOTOS);
        int formatSeparator = data.id().indexOf('/');
        if (formatSeparator == -1){
            return;
        }
        String formatId = data.id().substring(0, formatSeparator);
        try {

            FilmFormat format = FilmFormat.byName(formatId);

            File file = path.resolve(data.id()).toFile();
            if (!file.exists()) {
                context.reply(new PhotoDataPayload(new byte[0], FilmFormat.MISSING, data.id(), -1));
            }
            try (FileInputStream fis = new FileInputStream(file)) {
                NativeImage nativeImage = NativeImage.read(fis);
                context.reply(new PhotoDataPayload(Utils.compressInts(nativeImage.getPixels()), format, data.id(), -1));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        catch (IllegalArgumentException e){
            throw new RuntimeException(e);
        }
    }


    public static void adjustExposure(final AdjustExposurePayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();
            ItemStack stack = player.getActiveItem();
            if (!(stack.getItem() instanceof CameraItem)) {
                return;
            }
            int oldExposure = stack.getOrDefault(ModDataComponents.EXPOSURE, 0);
            int newExposure = Math.clamp(
                    oldExposure + data.delta(),
                    -CameraItem.EXPOSURE_DELTA,
                    CameraItem.EXPOSURE_DELTA);

            if (oldExposure == newExposure) {
                return;
            }

            player.sendOverlayMessage(Component.translatable("exposure_adjustment.message", newExposure));
            stack.set(ModDataComponents.EXPOSURE, newExposure);
            level.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.3F,
                    1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
        });
    }


    public static void cameraToggle(final CameraTogglePayload data, final IPayloadContext context) {
        switch (data.toggleType()){
            case DOUBLE_EXPOSURE -> doubleExposureToggle(data, context);
            case AF -> autofocusToggle(data, context);
        }
    }

    public static void doubleExposureToggle(final CameraTogglePayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();
            ItemStack stack = player.getActiveItem();
            if (!(stack.getItem() instanceof CameraItem cameraItem) || !cameraItem.cameraProperties.hasDoubleExposure()) {
                return;
            }

            DoubleExposure doubleExposure = stack.getOrDefault(ModDataComponents.DOUBLE_EXPOSURE, DoubleExposure.OFF);
            DoubleExposure.Mutable mutable = new DoubleExposure.Mutable(doubleExposure);
            if (doubleExposure.isOn()) {
                Optional<String> partialImage = mutable.turnOff();
                partialImage.ifPresent(id -> ServerPhotoTaker.givePhoto(photoStack(id, stack, player), player));
                player.sendOverlayMessage(Component.translatable("double_exposure.off.message"));
            } else {
                mutable.turnOn();
                player.sendOverlayMessage(Component.translatable("double_exposure.on.message"));
            }
            stack.set(ModDataComponents.DOUBLE_EXPOSURE, mutable.toImmutable());
            level.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.3F,
                    1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
        });
    }

    public static void autofocusToggle(final CameraTogglePayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();
            ItemStack stack = player.getActiveItem();
            if (!(stack.getItem() instanceof CameraItem cameraItem) || !cameraItem.cameraProperties.hasAF()) {
                return;
            }
            boolean state = stack.getOrDefault(ModDataComponents.AF,false);
            player.sendOverlayMessage(Component.translatable(state ? "autofocus.off.message" : "autofocus.on.message"));
            stack.set(ModDataComponents.AF,!state);
            level.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.3F,
                    1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
        });
    }
}
