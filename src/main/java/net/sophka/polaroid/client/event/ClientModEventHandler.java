package net.sophka.polaroid.client.event;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.RegisterTooltipAppendersEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.client.ClientState;
import net.sophka.polaroid.client.gui.screens.CameraFilmTooltipComponent;
import net.sophka.polaroid.client.gui.screens.DarkslideScreen;
import net.sophka.polaroid.client.gui.screens.PhotoScreen;
import net.sophka.polaroid.client.init.ModKeyMappings;
import net.sophka.polaroid.client.renderer.ClientPhotoTaker;
import net.sophka.polaroid.client.renderer.PhotoCache;
import net.sophka.polaroid.compatiblity.IrisHelper;
import net.sophka.polaroid.data.darkslide.DarkslideManager;
import net.sophka.polaroid.init.ModDataComponents;
import net.sophka.polaroid.network.*;
import net.sophka.polaroid.world.block.PhotoBlock;
import net.sophka.polaroid.world.block.entity.PhotoBlockEntity;
import net.sophka.polaroid.world.item.CameraItem;
import net.sophka.polaroid.world.item.DarkslideItem;
import net.sophka.polaroid.world.item.PhotoItem;
import net.sophka.polaroid.world.item.component.FilmContent;

import java.util.OptionalInt;

@EventBusSubscriber(value = Dist.CLIENT, modid = Polaroid600.MODID)
public class ClientModEventHandler {

    @SubscribeEvent
    public static void onItemUseEvent(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity() != Minecraft.getInstance().player) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof PhotoItem) {
            Minecraft.getInstance().setScreenAndShow(new PhotoScreen(stack));
        } else if (stack.getItem() instanceof DarkslideItem) {
            Minecraft.getInstance().setScreenAndShow(new DarkslideScreen(stack));
        }
    }

    @SubscribeEvent
    public static void onItemUseEvent(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() != Minecraft.getInstance().player || !event.getEntity().isCrouching() || event.getHand().equals(InteractionHand.OFF_HAND)) {
            return;
        }
        BlockPos pos = event.getPos();
        BlockState state = event.getLevel().getBlockState(pos);
        if(!(state.getBlock() instanceof PhotoBlock photoBlock)){
            return;
        }
        BlockEntity blockEntity = event.getLevel().getBlockEntity(pos);
        if(!(blockEntity instanceof PhotoBlockEntity photoBlockEntity)){
            return;
        }

        OptionalInt hitSlot = photoBlock.getHitSlot(event.getHitVec(), state.getValue(PhotoBlock.FACING));
        if(hitSlot.isEmpty()){
            return;
        }

        ItemStack stack = photoBlockEntity.getItem(hitSlot.getAsInt());
        if(stack.isEmpty() || !(stack.getItem() instanceof PhotoItem)){
            return;
        }
        Minecraft.getInstance().setScreenAndShow(new PhotoScreen(stack));
    }

    @SubscribeEvent
    public static void onItemUseOnBlockEvent(UseItemOnBlockEvent event) {
        if (event.getUsePhase() == UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK &&
                event.getPlayer() == Minecraft.getInstance().player &&
                event.getItemStack().getItem() instanceof PhotoItem &&
                event.getPlayer().isCrouching()) {
            Minecraft.getInstance().setScreenAndShow(new PhotoScreen(event.getItemStack()));
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        ClientState.selfieMode = false;
        PhotoCache.getInstance().clearCache();
    }

    @SubscribeEvent
    public static void onRenderPre(RenderFrameEvent.Pre event) {
        ClientPhotoTaker.instance().handleFlash();
    }

    @SubscribeEvent
    public static void onRenderPost(RenderFrameEvent.Post event) {
        ClientPhotoTaker.instance().process();
        ClientState.renderSelfieMirrorPass();
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent.AfterLevel event) {
        if (ClientPhotoTaker.instance().getState() != ClientPhotoTaker.State.TAKING_PHOTO) {
            return;
        }
    }


    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        ClientPhotoTaker photoTaker = ClientPhotoTaker.instance();
        if (photoTaker.getState() != ClientPhotoTaker.State.TAKING_PHOTO) {
            return;
        }
        event.setFOV(photoTaker.getFov());
    }

    @SubscribeEvent
    public static void onComputerCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        ClientPhotoTaker photoTaker = ClientPhotoTaker.instance();
        if (photoTaker.getState() != ClientPhotoTaker.State.TAKING_PHOTO || !ClientState.selfieMode) {
            return;
        }
        event.setRoll((float) (2 * (Minecraft.getInstance().level.getRandom().nextGaussian() - 0.5f) * 3f + event.getRoll()));
    }

    @SubscribeEvent
    public static void comps(RenderTooltipEvent.GatherComponents event) {
        ItemStack itemStack = event.getItemStack();
        FilmContent content = itemStack.getItem() instanceof CameraItem ? CameraItem.filmContent(itemStack) : itemStack.getOrDefault(ModDataComponents.FILM_CONTENT.get(), FilmContent.EMPTY);
        if (content.count() > 0) {
            event.getTooltipElements().add(Either.right(new CameraFilmTooltipComponent.DataComponent(content)));
        }
    }

    @SubscribeEvent
    public static void registerTooltipAppenders(RegisterTooltipAppendersEvent event) {
        event.registerComponentAppenderAfterAll(ModDataComponents.DARKSLIDE,
                (stack, context, display, player, tooltipFlag, builder)
                        -> DarkslideManager.CLIENT_INSTANCE.get(stack.get(ModDataComponents.DARKSLIDE)).ifPresent(
                        darkslide -> builder.accept(
                                Component.literal(String.format("%s - %02d/%02d", darkslide.series().seriesName(), darkslide.ordinal(), darkslide.series().size()))
                                        .withColor(TextColor.GRAY))
                ));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return;
        }
        ClientState.update();
        if (!(player.getActiveItem().getItem() instanceof CameraItem) || (!player.getMainHandItem().isEmpty() && !player.getOffhandItem().isEmpty())) {
            ClientState.selfieMode = false;
        }
    }

    @SubscribeEvent
    public static void onKeyPressed(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return;
        }
        ItemStack itemStack = player.getActiveItem();
        if (itemStack.getItem() instanceof CameraItem) {
            if (ModKeyMappings.CAMERA_INCREASE_EXPOSURE.consumeClick()) {
                ClientPacketDistributor.sendToServer(new AdjustExposurePayload(1));
            }
            if (ModKeyMappings.CAMERA_DECREASE_EXPOSURE.consumeClick()) {
                ClientPacketDistributor.sendToServer(new AdjustExposurePayload(-1));
            }
            if (ModKeyMappings.CAMERA_DOUBLE_EXPOSURE_TOGGLE.consumeClick()) {
                ClientPacketDistributor.sendToServer(new CameraTogglePayload(CameraTogglePayload.ToggleType.DOUBLE_EXPOSURE));
            }
            if (ModKeyMappings.CAMERA_AUTOFOCUS_TOGGLE.consumeClick()) {
                ClientPacketDistributor.sendToServer(new CameraTogglePayload(CameraTogglePayload.ToggleType.AF));
            }
            if (ModKeyMappings.CAMERA_FLASH_TOGGLE.consumeClick()) {
                ClientPacketDistributor.sendToServer(new CameraTogglePayload(CameraTogglePayload.ToggleType.FLASH));
            }
            if (ModKeyMappings.CAMERA_SELFIE_MODE.consumeClick()) {
                if (player.getActiveItem().getItem() instanceof CameraItem) {
                    ClientState.toggleSelfieMode();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onAfterLevel(RenderLevelStageEvent.AfterLevel event) {
        RenderTarget main = Minecraft.getInstance().gameRenderer.mainRenderTarget();
        ClientPhotoTaker photoTaker = ClientPhotoTaker.instance();
        if(photoTaker.getState() != ClientPhotoTaker.State.TAKING_PHOTO){
            return;
        }

        if(!ModList.get().isLoaded("iris") || !IrisHelper.isShaderPackInUse()) {
            Identifier effect = photoTaker.getAutofocus() ? ClientPhotoTaker.dofAutofocusEffect : ClientPhotoTaker.dofEffect;

            PostChain dof = Minecraft.getInstance()
                    .getShaderManager()
                    .getPostChain(effect, LevelTargetBundle.MAIN_TARGETS);

            if (dof == null) {
                return;
            }
            dof.process(main, GraphicsResourceAllocator.UNPOOLED);
        }
        ClientPhotoTaker.instance().releaseRenderTarget();
    }
}
