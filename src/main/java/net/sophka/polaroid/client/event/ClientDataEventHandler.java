package net.sophka.polaroid.client.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.data.film.FilmDataManager;

@EventBusSubscriber(modid = Polaroid600.MODID, value = Dist.CLIENT)
public class ClientDataEventHandler {
    @SubscribeEvent
    public static void addReloadListener(AddClientReloadListenersEvent event) {
        event.addListener(FilmDataManager.IDENTIFIER,FilmDataManager.INSTANCE);
    }
}
