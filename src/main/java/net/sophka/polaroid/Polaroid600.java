package net.sophka.polaroid;

import net.sophka.polaroid.config.ClientConfig;
import net.sophka.polaroid.config.CommonConfig;
import net.sophka.polaroid.config.ServerConfig;
import net.sophka.polaroid.init.*;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Polaroid600.MODID)
public class Polaroid600 {
    public static final String MODID = "polaroid600";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Polaroid600(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModFilmTransformations.TYPES.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

}
