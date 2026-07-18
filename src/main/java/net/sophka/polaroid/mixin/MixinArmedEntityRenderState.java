package net.sophka.polaroid.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.client.ClientState;
import net.sophka.polaroid.client.renderer.ClientPhotoTaker;
import net.sophka.polaroid.world.item.CameraItem;
import net.sophka.polaroid.world.item.ModItemDisplayContexts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmedEntityRenderState.class)
public class MixinArmedEntityRenderState {
    @Inject(method = "extractArmedEntityRenderState",  at = @At("RETURN"))
    private static void extractArmedEntityRenderState(LivingEntity entity, ArmedEntityRenderState state, ItemModelResolver itemModelResolver, float partialTicks, CallbackInfo ci) {
        HumanoidArm arm = state.attackArm;
        ItemStack stack = entity.getItemHeldByArm(arm);
        if(stack.getItem() instanceof CameraItem && ClientState.selfieMode){
            if(entity == Minecraft.getInstance().player && ClientPhotoTaker.instance().getState() == ClientPhotoTaker.State.TAKING_PHOTO){
                stack = ItemStack.EMPTY;
            }
            itemModelResolver.updateForLiving(arm == HumanoidArm.LEFT ? state.leftHandItemState : state.rightHandItemState, stack, ModItemDisplayContexts.selfieModeThirdPerson(), entity);
        }
    }
}
