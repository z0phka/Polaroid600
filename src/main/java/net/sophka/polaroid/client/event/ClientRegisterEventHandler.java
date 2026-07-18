package net.sophka.polaroid.client.event;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.client.ClientState;
import net.sophka.polaroid.client.gui.screens.CameraFilmTooltipComponent;
import net.sophka.polaroid.client.init.ModKeyMappings;
import net.sophka.polaroid.client.model.FilmTypeProperty;
import net.sophka.polaroid.client.model.ModArmPose;
import net.sophka.polaroid.client.model.ModModelProvider;
import net.sophka.polaroid.client.model.entity.CameraTripodModel;
import net.sophka.polaroid.client.renderer.entity.CameraTripodRenderer;
import net.sophka.polaroid.client.renderer.level.block.PhotoBlockEntityRenderer;
import net.sophka.polaroid.init.ModBlockEntityTypes;
import net.sophka.polaroid.init.ModEntityTypes;
import net.sophka.polaroid.init.ModItems;
import net.sophka.polaroid.network.*;
import net.sophka.polaroid.world.item.CameraItem;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber(value = Dist.CLIENT, modid = Polaroid600.MODID)
public class ClientRegisterEventHandler {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntityTypes.PHOTO_BLOCK_ENTITY.get(), PhotoBlockEntityRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.CAMERA_TRIPOD.get(), CameraTripodRenderer::new);
    }
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CameraTripodModel.LAYER_LOCATION, CameraTripodModel::createCameraTripodModel);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        ModItems.ITEMS.getEntries().stream().map(DeferredHolder::get).filter(item -> item instanceof CameraItem).forEach(item ->
                event.registerItem(new IClientItemExtensions(){
                    @Override
                    public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                        return ClientState.selfieMode ? ModArmPose.selfieBothArmPose() : HumanoidModel.ArmPose.BOW_AND_ARROW;
                    }
                }, item)
        );
    }
    @SubscribeEvent 
    public static void register(RegisterClientPayloadHandlersEvent event) {
        event.register(
                PhotoDataPayload.TYPE,
                ClientPayloadHandler::handlePhotoData
        );
        event.register(
                PhotoCaptureRequestPayload.TYPE,
                ClientPayloadHandler::handlePhotoCaptureRequest
        );
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(ModModelProvider::new);
    }

    @SubscribeEvent
    public static void registerSelectProperties(RegisterSelectItemModelPropertyEvent event) {
        event.register(
                Identifier.fromNamespaceAndPath(Polaroid600.MODID, "film_type"),
                FilmTypeProperty.TYPE
        );
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(ModKeyMappings.CATEGORY);
        event.register(ModKeyMappings.CAMERA_INCREASE_EXPOSURE);
        event.register(ModKeyMappings.CAMERA_DECREASE_EXPOSURE);
        event.register(ModKeyMappings.CAMERA_AUTOFOCUS_TOGGLE);
        event.register(ModKeyMappings.CAMERA_DOUBLE_EXPOSURE_TOGGLE);
        event.register(ModKeyMappings.CAMERA_SELFIE_MODE);
    }


    @SubscribeEvent
    public static void registerClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(CameraFilmTooltipComponent.DataComponent.class, CameraFilmTooltipComponent::create);
    }
}
