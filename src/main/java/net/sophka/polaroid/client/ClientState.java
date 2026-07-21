package net.sophka.polaroid.client;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.client.renderer.ClientPhotoTaker;
import net.sophka.polaroid.client.renderer.SelfieMirrorTexture;
import net.sophka.polaroid.client.renderer.level.LevelToTargetRenderer;
import net.sophka.polaroid.config.ClientConfig;
import net.sophka.polaroid.world.entity.CameraViewEntity;
import net.sophka.polaroid.world.item.CameraItem;

//Really dirty client constants and.... general stuff class that should not be allowed to exist
public class ClientState {
    //TODO: Move to player data or even on the server or something, as this is really dirty and cheap
    public static boolean selfieMode = false;
    private static CameraViewEntity selfieViewEntity;

    private static RenderTarget selfieMirrorTarget;
    private static SelfieMirrorTexture selfieMirrorTexture;
    private static LevelToTargetRenderer levelToTargetRenderer = new LevelToTargetRenderer(Minecraft.getInstance());
    public static final Identifier selfieMirrorTextureIdentifier = Identifier.fromNamespaceAndPath(Polaroid600.MODID, "selfie_mirror");

    private static boolean selfieCameraPass = false;
    private static int renderCounter = 0;

    public static void toggleSelfieMode(){
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if(!(player.getActiveItem().getItem() instanceof CameraItem) || (!player.getMainHandItem().isEmpty() && !player.getOffhandItem().isEmpty())){
            selfieMode = false;
            return;
        }
        selfieMode = !selfieMode;
        minecraft.gameRenderer.itemInHandRenderer.itemUsed(InteractionHand.MAIN_HAND);
        minecraft.gameRenderer.itemInHandRenderer.itemUsed(InteractionHand.OFF_HAND);
    }

    private static void initSelfieMirrorTexture(){
        if(selfieMirrorTarget == null){
            selfieMirrorTarget = new MainTarget(ClientConfig.SELFIE_MIRROR_RESOLUTION.get(),ClientConfig.SELFIE_MIRROR_RESOLUTION.get());
        }
        if(selfieMirrorTexture == null){
            selfieMirrorTexture = new SelfieMirrorTexture(selfieMirrorTarget);
        }
        Minecraft.getInstance().getTextureManager().register(selfieMirrorTextureIdentifier, selfieMirrorTexture);
    }

    public static SelfieMirrorTexture selfieMirrorTexture(){
        initSelfieMirrorTexture();
        return selfieMirrorTexture;
    }

    public static RenderTarget selfieMirrorTarget(){
        initSelfieMirrorTexture();
        return selfieMirrorTarget;
    }

    public static void renderSelfieMirrorPass(){
        if(shouldDoSelfieMirrorPass()){
            selfieCameraPass = true;
            levelToTargetRenderer.render(selfieMirrorTarget(),selfieViewEntity());
            selfieCameraPass = false;
        }
    }

    public static CameraViewEntity selfieViewEntity(){
        validateSelfieViewEntity();
        if(selfieViewEntity == null && Minecraft.getInstance().level != null){
            selfieViewEntity = new CameraViewEntity(Minecraft.getInstance().level);
        }
        updateSelfieViewEntity();
        return selfieViewEntity;
    }

    public static void updateSelfieViewEntity(){
        if(selfieViewEntity == null){
            return;
        }
        selfieViewEntity.updateForPlayer(Minecraft.getInstance().player);
    }

    private static void validateSelfieViewEntity(){
        if(selfieViewEntity == null){
            return;
        }
        if(selfieViewEntity.level() != Minecraft.getInstance().level){
            selfieViewEntity = null;
        }
    }

    public static boolean shouldDoSelfieMirrorPass(){
        if(ClientConfig.SELFIE_MIRROR.isFalse()){
            return false;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.isPaused()){
            return false;
        }
        Player player = minecraft.player;
        if(player == null){
            return false;
        }
        if(player.getActiveItem().getItem() instanceof CameraItem cameraItem && cameraItem.cameraProperties.hasSelfieMirror() && selfieMode){
            return renderCounter % ClientConfig.SELFIE_MIRROR_UPDATE_PERIOD.get() == 0;
        }
        return false;
    }

    public static void update(){
        renderCounter++;
    }

    public static boolean isSelfieTextureReady(){
        return selfieMirrorTexture != null;
    }

    public static boolean isSelfieCameraPass(){
        return selfieCameraPass || (selfieMode && ClientPhotoTaker.instance().getState() == ClientPhotoTaker.State.TAKING_PHOTO);
    }
}
