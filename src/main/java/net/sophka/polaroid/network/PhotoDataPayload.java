package net.sophka.polaroid.network;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.utils.Utils;
import net.sophka.polaroid.world.item.FilmFormat;
import net.sophka.polaroid.world.item.FilmType;

import java.io.IOException;

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

    public NativeImage toImage(){
        try {
            NativeImage scaled = new NativeImage(format.width, format.height, false);
            int[] pixels = Utils.decompressInts(data(), format.width * format.height);
            for(int i = 0; i < format.height; i++){
                for(int j = 0; j < format.width; j++){
                    scaled.setPixel(j,i,pixels[i * format.width + j]);
                }
            }
            return scaled;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
