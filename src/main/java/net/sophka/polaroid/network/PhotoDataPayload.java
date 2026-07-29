package net.sophka.polaroid.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.world.item.FilmFormat;


public record PhotoDataPayload(byte[] data, FilmFormat format, String id, int token) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PhotoDataPayload> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Polaroid600.MODID, "photo_data"));
    public static final StreamCodec<FriendlyByteBuf, PhotoDataPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE_ARRAY,
            PhotoDataPayload::data,
            NeoForgeStreamCodecs.enumCodec(FilmFormat.class),
            PhotoDataPayload::format,
            ByteBufCodecs.STRING_UTF8,
            PhotoDataPayload::id,
            ByteBufCodecs.INT,
            PhotoDataPayload::token,
            PhotoDataPayload::new
    );

    @Override
    public Type<? extends PhotoDataPayload> type() {
        return TYPE;
    }
}
