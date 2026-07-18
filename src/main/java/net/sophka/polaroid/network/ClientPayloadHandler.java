package net.sophka.polaroid.network;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.client.renderer.PhotoCache;
import net.sophka.polaroid.client.renderer.ClientPhotoTaker;
import net.sophka.polaroid.world.entity.CameraViewEntity;
import net.sophka.polaroid.world.item.FilmFormat;

import java.io.File;
import java.io.IOException;

public class ClientPayloadHandler {
    public static void handlePhotoData(final PhotoDataPayload data, final IPayloadContext context) {
        Polaroid600.LOGGER.debug("{} bytes received", data.data().length);

        FilmFormat format = data.format();
        if(format == FilmFormat.MISSING){
            return;
        }
        try(NativeImage scaled = data.toImage()){
            PhotoCache.getInstance().save(format, data.id(), scaled);
        }
    }

    public static void handlePhotoCaptureRequest(final PhotoCaptureRequestPayload request, final IPayloadContext context) {
        Polaroid600.LOGGER.debug("MEANT TO TAKE A PHOTO HERE :3");
        context.enqueueWork(() -> {
            CameraViewEntity cameraViewEntity = null;
            if(!request.firstPerson()){
                cameraViewEntity = new CameraViewEntity(Minecraft.getInstance().level);
                cameraViewEntity.setPositionAndRotation(request.posX(), request.posY(), request.posZ(), request.xRot(), request.yRot());
            }
            ClientPhotoTaker.instance().takePhoto(request.camera(), cameraViewEntity, request.token());
        });
    }
}
