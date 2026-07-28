package net.sophka.polaroid.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.sophka.polaroid.Polaroid600;

public record PhotoCaptureRequestPayload(ItemStack camera, boolean firstPerson, double posX, double posY, double posZ, float xRot, float yRot, float roll, int token) implements CustomPacketPayload {
    public static final Type<PhotoCaptureRequestPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Polaroid600.MODID, "photo_capture_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PhotoCaptureRequestPayload> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            PhotoCaptureRequestPayload::camera,
            ByteBufCodecs.BOOL,
            PhotoCaptureRequestPayload::firstPerson,
            ByteBufCodecs.DOUBLE,
            PhotoCaptureRequestPayload::posX,
            ByteBufCodecs.DOUBLE,
            PhotoCaptureRequestPayload::posY,
            ByteBufCodecs.DOUBLE,
            PhotoCaptureRequestPayload::posZ,
            ByteBufCodecs.FLOAT,
            PhotoCaptureRequestPayload::xRot,
            ByteBufCodecs.FLOAT,
            PhotoCaptureRequestPayload::yRot,
            ByteBufCodecs.FLOAT,
            PhotoCaptureRequestPayload::roll,
            ByteBufCodecs.INT,
            PhotoCaptureRequestPayload::token,
            PhotoCaptureRequestPayload::new);

    @Override
    public Type<? extends PhotoCaptureRequestPayload> type() {
        return TYPE;
    }
}
