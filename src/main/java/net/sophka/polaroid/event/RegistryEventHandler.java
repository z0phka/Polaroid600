package net.sophka.polaroid.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.init.ModRegistries;

@EventBusSubscriber(modid = Polaroid600.MODID)
public class RegistryEventHandler {

    @SubscribeEvent
    public static void createNewRegistries(NewRegistryEvent event) {
        event.register(ModRegistries.FILM_TRANSFORMATION_TYPE);
    }

}
