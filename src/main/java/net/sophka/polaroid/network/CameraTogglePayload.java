package net.sophka.polaroid.network;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.world.item.FilmFormat;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public record CameraTogglePayload(ToggleType toggleType) implements CustomPacketPayload {
    public enum ToggleType implements StringRepresentable {
        DOUBLE_EXPOSURE("double_exposure"),
        AF("af");

        public static final Codec<ToggleType> COODEC = StringRepresentable.fromEnum(ToggleType::values);
        private static final IntFunction<ToggleType> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, ToggleType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

        public final String name;

        ToggleType(String name){
            this.name = name;
        }


        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    public static final Type<CameraTogglePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Polaroid600.MODID, "camera_toggle"));
    public static final StreamCodec<FriendlyByteBuf, CameraTogglePayload> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(ToggleType.class),
            CameraTogglePayload::toggleType,
            CameraTogglePayload::new
    );

    @Override
    public Type<? extends CameraTogglePayload> type() {
        return TYPE;
    }
}
