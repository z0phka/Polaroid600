package net.sophka.polaroid.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.data.darkslide.DarkslideManager;
import net.sophka.polaroid.network.DarkslideSyncPayload;

@EventBusSubscriber(modid = Polaroid600.MODID)
public class DataEventHandler {
    @SubscribeEvent
    public static void addReloadListener(AddServerReloadListenersEvent event) {
        event.addListener(DarkslideManager.IDENTIFIER,DarkslideManager.INSTANCE);
    }
    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        event.getRelevantPlayers().forEach(player ->
                PacketDistributor.sendToPlayer(player, new DarkslideSyncPayload(DarkslideManager.INSTANCE.darkslideSeries())));
    }
}
