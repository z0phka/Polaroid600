package net.sophka.polaroid.client.renderer.level.block;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class PhotoBlockEntityRenderState extends BlockEntityRenderState {
    public NonNullList<ItemStack> items;
    public Direction facing = Direction.NORTH;

}
