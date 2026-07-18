package net.sophka.polaroid.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.sophka.polaroid.world.item.CameraItem;

public class ClientState {
    //TODO: Move to player data or even on the server or something, as this is really dirty and cheap
    public static boolean selfieMode = false;

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
}
