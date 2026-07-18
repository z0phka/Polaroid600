package net.sophka.polaroid.server.photo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.init.ModSounds;
import net.sophka.polaroid.network.PhotoCaptureRequestPayload;
import net.sophka.polaroid.world.item.CameraItem;
import net.sophka.polaroid.world.item.FilmItem;
import net.sophka.polaroid.world.item.component.CameraFilm;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ServerPhotoTaker {
    public static void takePhoto(ServerPlayer player, ItemStack cameraStack){
        if (!(cameraStack.getItem() instanceof CameraItem)) {
            return;
        }
        if (player.getCooldowns().isOnCooldown(cameraStack)) {
            return;
        }
        CameraFilm cameraFilm = cameraStack.get(ModDataComponents.CAMERA_FILM.get());
        if (cameraFilm == null) {
            return;
        }
        ItemStack filmStack = cameraFilm.getFilmStack();
        if (filmStack.isEmpty() || !(filmStack.getItem() instanceof FilmItem)) {
            return;
        }

        int token = PhotoTokenManager.getInstance().acquireTokenFor(player, () -> cameraStack, photoStack -> givePhoto(photoStack, player), !player.isCreative());
        PacketDistributor.sendToPlayer(player, new PhotoCaptureRequestPayload(cameraStack, true, 0,0,0,0,0,0, token));
    }

    public static void givePhoto(ItemStack photoStack, Player player) {
        Level level = player.level();
        level.playSound(null, player.blockPosition(), ModSounds.CAMERA_SHUTTER.get(), SoundSource.PLAYERS, 0.3F,
                1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
        if (!player.addItem(photoStack)) {
            player.drop(photoStack, false);
        }
    }

    public static void takePhoto(ServerPlayer player, Supplier<ItemStack> camera, Consumer<ItemStack> post){
        int token = PhotoTokenManager.getInstance().acquireTokenFor(player, camera, post);
        PacketDistributor.sendToPlayer(player, new PhotoCaptureRequestPayload(camera.get(), true, 0,0,0,0,0,0, token));
    }

    public static void takePhoto(ServerPlayer player, Supplier<ItemStack> camera, Consumer<ItemStack> post, double posX, double posY, double posZ, float xRot, float yRot, float roll){
        int token = PhotoTokenManager.getInstance().acquireTokenFor(player, camera, post);
        PacketDistributor.sendToPlayer(player, new PhotoCaptureRequestPayload(camera.get(), false, posX, posY, posZ, xRot, yRot, roll, token));
    }
}
