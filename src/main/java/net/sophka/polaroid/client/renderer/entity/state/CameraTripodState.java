package net.sophka.polaroid.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;

public class CameraTripodState extends LivingEntityRenderState {
    public ItemStack cameraStack = ItemStack.EMPTY;
    public ItemStackRenderState cameraStackState = new ItemStackRenderState();
    public ItemStack photoStack;
    public float headRot;
}
