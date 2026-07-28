package net.sophka.polaroid.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.data.darkslide.DarkslideSeries;

import java.util.HashMap;
import java.util.Map;

public record DarkslideSyncPayload(Map<Identifier, DarkslideSeries> data) implements CustomPacketPayload {
    public static final Type<DarkslideSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Polaroid600.MODID, "darkslide_sync"));
    public static final StreamCodec<ByteBuf, DarkslideSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                ByteBufCodecs.map(
                        HashMap::new,
                        Identifier.STREAM_CODEC,
                        DarkslideSeries.STREAM_CODEC),
                DarkslideSyncPayload::data,
                DarkslideSyncPayload::new);

    @Override
    public Type<? extends DarkslideSyncPayload> type() {
        return TYPE;
    }
}
