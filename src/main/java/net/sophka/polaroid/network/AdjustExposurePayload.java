package net.sophka.polaroid.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.sophka.polaroid.Polaroid600;

public record AdjustExposurePayload(int delta) implements CustomPacketPayload {
    public static final Type<AdjustExposurePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Polaroid600.MODID, "adjust_exposure"));
    public static final StreamCodec<ByteBuf, AdjustExposurePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            AdjustExposurePayload::delta,
            AdjustExposurePayload::new
    );

    @Override
    public Type<? extends AdjustExposurePayload> type() {
        return TYPE;
    }
}
