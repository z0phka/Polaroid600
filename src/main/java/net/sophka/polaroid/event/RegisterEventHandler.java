package net.sophka.polaroid.event;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.init.ModItems;
import net.sophka.polaroid.network.*;

@EventBusSubscriber(modid = Polaroid600.MODID)
public class RegisterEventHandler {
    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
                PhotoDataPayload.TYPE,
                PhotoDataPayload.STREAM_CODEC,
                ServerPayloadHandler::handlePhotoData
        );
        registrar.playToServer(
                PhotoRequestPayload.TYPE,
                PhotoRequestPayload.STREAM_CODEC,
                ServerPayloadHandler::handlePhotoRequest
        );
        registrar.playToServer(
                AdjustExposurePayload.TYPE,
                AdjustExposurePayload.STREAM_CODEC,
                ServerPayloadHandler::adjustExposure
        );
        registrar.playToServer(
                CameraTogglePayload.TYPE,
                CameraTogglePayload.STREAM_CODEC,
                ServerPayloadHandler::cameraToggle
        );
        registrar.playToClient(
                PhotoCaptureRequestPayload.TYPE,
                PhotoCaptureRequestPayload.STREAM_CODEC);

        registrar.playToClient(
                DarkslideSyncPayload.TYPE,
                DarkslideSyncPayload.STREAM_CODEC);
    }
}
