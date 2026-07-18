package net.sophka.polaroid.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.sophka.polaroid.Polaroid600;

public record PhotoRequestPayload(String id) implements CustomPacketPayload {
    public static final Type<PhotoRequestPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Polaroid600.MODID, "photo_request"));
    public static final StreamCodec<ByteBuf, PhotoRequestPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            PhotoRequestPayload::id,
            PhotoRequestPayload::new
    );

    @Override
    public Type<? extends PhotoRequestPayload> type() {
        return TYPE;
    }
}
