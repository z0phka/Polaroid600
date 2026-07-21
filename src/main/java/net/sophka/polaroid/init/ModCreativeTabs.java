package net.sophka.polaroid.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.config.ClientConfig;
import net.sophka.polaroid.data.darkslide.Darkslide;
import net.sophka.polaroid.data.darkslide.DarkslideManager;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Polaroid600.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("polaroid_600_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.polaroid600"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.FILM_600.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.CAMERA_600.get());
                output.accept(ModItems.CAMERA_600_AF.get());
                output.accept(ModItems.CAMERA_SPECTRA.get());
                output.accept(ModItems.CAMERA_SX_70.get());
                output.accept(ModItems.CAMERA_NOW.get());
                output.accept(ModItems.CAMERA_GO.get());
                output.accept(ModItems.CAMERA_TRIPOD.get());

                output.accept(ModItems.FILM_CARTRIDGE.get());
                output.accept(ModItems.FILM_600.get());
                output.accept(ModItems.FILM_600_BW.get());
                output.accept(ModItems.FILM_600_BLUE.get());
                output.accept(ModItems.FILM_600_GREEN.get());
                output.accept(ModItems.FILM_600_PURPLE.get());

                output.accept(ModItems.BW_NEGATIVE.get());
                output.accept(ModItems.COLOR_NEGATIVE.get());

                output.accept(ModItems.BW_REAGENT.get());
                output.accept(ModItems.COLOR_REAGENT.get());

                output.accept(ModItems.RED_LIGHT_SENSITIVE_LAYER.get());
                output.accept(ModItems.GREEN_LIGHT_SENSITIVE_LAYER.get());
                output.accept(ModItems.BLUE_LIGHT_SENSITIVE_LAYER.get());
                output.accept(ModItems.LIGHT_SENSITIVE_LAYER.get());

                output.accept(ModItems.CYAN_DEVELOPER.get());
                output.accept(ModItems.MAGENTA_DEVELOPER.get());
                output.accept(ModItems.YELLOW_DEVELOPER.get());

                if(ClientConfig.DARKSLIDES_IN_CREATIVE_TAB.get()) {
                    DarkslideManager.CLIENT_INSTANCE.darkslideSeries().entrySet().stream().flatMap(entry ->
                                    entry.getValue().darkslides().stream().map(darkslide -> entry.getKey().withSuffix("/" + darkslide.identifier())))
                            .forEach(identifier -> {
                                ItemStack stack = new ItemStack(ModItems.DARKSLIDE.get());
                                stack.set(ModDataComponents.DARKSLIDE, identifier);
                                output.accept(stack);
                            });
                }
            }).build());
}
