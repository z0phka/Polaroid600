package net.sophka.polaroid.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.client.ClientState;
import net.sophka.polaroid.client.renderer.CameraInHandRenderer;
import net.sophka.polaroid.client.renderer.PhotoInHandRenderer;
import net.sophka.polaroid.world.item.CameraItem;
import net.sophka.polaroid.world.item.PhotoItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// There now seems to be an event for this now, but I guess keeping it here might make potential Fabric port easier
@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer {

    @Unique
    private PhotoInHandRenderer polaroid600$photoInHandRenderer;
    @Unique
    private CameraInHandRenderer polaroid600$cameraInHandRenderer;

    @Shadow
    private ItemStack offHandItem;
    @Shadow
    private ItemStack mainHandItem;

    @Inject(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    public void submitArmWithItem(AbstractClientPlayer player, float frameInterp, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci){
        if(polaroid600$photoInHandRenderer == null){
            Object self = this;
            polaroid600$photoInHandRenderer = new PhotoInHandRenderer((ItemInHandRenderer)self);
        }
        if(polaroid600$cameraInHandRenderer == null){
            Object self = this;
            polaroid600$cameraInHandRenderer = new CameraInHandRenderer((ItemInHandRenderer)self);
        }
        boolean isMainHand = hand == InteractionHand.MAIN_HAND;
        HumanoidArm arm = isMainHand ? player.getMainArm() : player.getMainArm().getOpposite();
        if (itemStack.getItem() instanceof PhotoItem) {
            if (isMainHand && this.offHandItem.isEmpty()) {
                polaroid600$photoInHandRenderer.renderTwoHandedPhoto(poseStack, submitNodeCollector, lightCoords, xRot, inverseArmHeight, attack, mainHandItem);
            } else {
                polaroid600$photoInHandRenderer.renderOneHandedPhoto(poseStack, submitNodeCollector, lightCoords, inverseArmHeight, arm, attack, itemStack);
            }
            poseStack.pushPose();
            ci.cancel();
        }
        else if (itemStack.getItem() instanceof CameraItem cameraItem && ClientState.selfieMode){
            if(cameraItem.twoHanded()){
                if (isMainHand && this.offHandItem.isEmpty()) {
                    polaroid600$cameraInHandRenderer.renderTwoHandedCamera(poseStack, submitNodeCollector, lightCoords, xRot, inverseArmHeight, attack, mainHandItem);
                }
            }
            else{

                polaroid600$cameraInHandRenderer.renderOneHandedCamera(poseStack, submitNodeCollector, lightCoords, inverseArmHeight, arm, attack, itemStack);
            }
            poseStack.pushPose();
            ci.cancel();
        }
    }
}
