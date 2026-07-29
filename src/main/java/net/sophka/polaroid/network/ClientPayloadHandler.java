package net.sophka.polaroid.network;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.client.renderer.PhotoCache;
import net.sophka.polaroid.client.renderer.ClientPhotoTaker;
import net.sophka.polaroid.data.darkslide.DarkslideManager;
import net.sophka.polaroid.utils.Utils;
import net.sophka.polaroid.world.entity.CameraViewEntity;
import net.sophka.polaroid.world.item.FilmFormat;

import java.io.IOException;

public class ClientPayloadHandler {
    public static void handlePhotoData(final PhotoDataPayload data, final IPayloadContext context) {
        Polaroid600.LOGGER.debug("{} bytes received", data.data().length);

        FilmFormat format = data.format();
        if(format == FilmFormat.MISSING){
            return;
        }
        try(NativeImage scaled = toImage(data)){
            PhotoCache.getInstance().save(format, data.id(), scaled);
        }
    }

    private static NativeImage toImage(PhotoDataPayload payload){
        try {
            NativeImage scaled = new NativeImage(payload.format().width, payload.format().height, false);
            int[] pixels = Utils.decompressInts(payload.data(), payload.format().width * payload.format().height);
            for(int i = 0; i < payload.format().height; i++){
                for(int j = 0; j < payload.format().width; j++){
                    scaled.setPixel(j,i,pixels[i * payload.format().width + j]);
                }
            }
            return scaled;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handlePhotoCaptureRequest(final PhotoCaptureRequestPayload request, final IPayloadContext context) {
        context.enqueueWork(() -> {
            CameraViewEntity cameraViewEntity = null;
            if(!request.firstPerson()){
                cameraViewEntity = new CameraViewEntity(Minecraft.getInstance().level);
                cameraViewEntity.setPositionAndRotation(request.posX(), request.posY(), request.posZ(), request.xRot(), request.yRot());
            }
            ClientPhotoTaker.instance().takePhoto(request.camera(), cameraViewEntity, request.token());
        });
    }

    public static void handleDarkslideSync(final DarkslideSyncPayload darkslideSyncPayload, final IPayloadContext iPayloadContext) {
        DarkslideManager.CLIENT_INSTANCE.load(darkslideSyncPayload.data());
    }
}
