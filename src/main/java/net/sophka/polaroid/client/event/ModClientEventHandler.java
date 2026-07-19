package net.sophka.polaroid.client.event;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.client.ClientState;
import net.sophka.polaroid.client.gui.screens.CameraFilmTooltipComponent;
import net.sophka.polaroid.client.gui.screens.PhotoScreen;
import net.sophka.polaroid.client.init.ModKeyMappings;
import net.sophka.polaroid.client.renderer.ClientPhotoTaker;
import net.sophka.polaroid.client.renderer.PhotoCache;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.network.*;
import net.sophka.polaroid.world.item.CameraItem;
import net.sophka.polaroid.world.item.PhotoItem;
import net.sophka.polaroid.world.item.component.CameraFilm;

@EventBusSubscriber(value = Dist.CLIENT, modid = Polaroid600.MODID)
public class ModClientEventHandler {

    @SubscribeEvent
    public static void onItemUseEvent(PlayerInteractEvent.RightClickItem event){
        if(event.getEntity() == Minecraft.getInstance().player && event.getItemStack().getItem() instanceof PhotoItem){
            Minecraft.getInstance().setScreenAndShow(new PhotoScreen(event.getItemStack()));
        }
    }

    @SubscribeEvent
    public static void onItemUseOnBlockEvent(UseItemOnBlockEvent event){
        if(event.getUsePhase() == UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK  &&
                event.getPlayer() == Minecraft.getInstance().player &&
                event.getItemStack().getItem() instanceof PhotoItem &&
                event.getPlayer().isCrouching()){
            Minecraft.getInstance().setScreenAndShow(new PhotoScreen(event.getItemStack()));
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event){
        ClientState.selfieMode = false;
        PhotoCache.getInstance().clearCache();
    }

    @SubscribeEvent
    public static void onRenderPost(RenderFrameEvent.Post event){
        ClientPhotoTaker.instance().process();
        ClientState.renderSelfieMirrorPass();
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent.AfterLevel event){
        if(ClientPhotoTaker.instance().getState() != ClientPhotoTaker.State.TAKING_PHOTO){
            return;
        }
        ClientPhotoTaker.instance().releaseRenderTarget();
    }


    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event){
        ClientPhotoTaker photoTaker = ClientPhotoTaker.instance();
        if(photoTaker.getState() != ClientPhotoTaker.State.TAKING_PHOTO) {
            return;
        }
        event.setFOV(photoTaker.getFov());
    }

    @SubscribeEvent
    public static void onComputerCameraAngles(ViewportEvent.ComputeCameraAngles event){
        ClientPhotoTaker photoTaker = ClientPhotoTaker.instance();
        if(photoTaker.getState() != ClientPhotoTaker.State.TAKING_PHOTO || !ClientState.selfieMode) {
            return;
        }
        event.setRoll((float) (2 * (Minecraft.getInstance().level.getRandom().nextGaussian() - 0.5f) * 3f + event.getRoll()));
    }

    @SubscribeEvent
    public static void comps(RenderTooltipEvent.GatherComponents event) {
        ItemStack itemStack = event.getItemStack();
        if(itemStack.getItem() instanceof CameraItem){
            CameraFilm content = itemStack.getOrDefault(ModDataComponents.CAMERA_FILM.get(), CameraFilm.EMPTY);
            if(content.count() > 0){
                event.getTooltipElements().add(Either.right(new CameraFilmTooltipComponent.DataComponent(content)));
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event){
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if(player == null){
            return;
        }
        ClientState.update();
        if(!(player.getActiveItem().getItem() instanceof CameraItem) || (!player.getMainHandItem().isEmpty() && !player.getOffhandItem().isEmpty())){
            ClientState.selfieMode = false;
        }
    }

    @SubscribeEvent
    public static void onKeyPressed(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if(player == null){
            return;
        }
        ItemStack itemStack = player.getActiveItem();
        if(itemStack.getItem() instanceof CameraItem){
            if(ModKeyMappings.CAMERA_INCREASE_EXPOSURE.consumeClick()){
                ClientPacketDistributor.sendToServer(new AdjustExposurePayload(1));
            }
            if(ModKeyMappings.CAMERA_DECREASE_EXPOSURE.consumeClick()){
                ClientPacketDistributor.sendToServer(new AdjustExposurePayload(-1));
            }
            if(ModKeyMappings.CAMERA_DOUBLE_EXPOSURE_TOGGLE.consumeClick()){
                ClientPacketDistributor.sendToServer(new CameraTogglePayload(CameraTogglePayload.ToggleType.DOUBLE_EXPOSURE));
            }
            if(ModKeyMappings.CAMERA_AUTOFOCUS_TOGGLE.consumeClick()){
                ClientPacketDistributor.sendToServer(new CameraTogglePayload(CameraTogglePayload.ToggleType.AF));
            }
            if(ModKeyMappings.CAMERA_SELFIE_MODE.consumeClick()){
                if(player.getActiveItem().getItem() instanceof CameraItem){
                    ClientState.toggleSelfieMode();
                }
            }
        }
    }

}
