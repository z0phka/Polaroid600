package net.sophka.polaroid.world.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum FilmType implements StringRepresentable {
    MISSING(0,"missing"),
    COLOR(1,"color"),
    BW(2,"bw"),
    BLUE(3,"blue"),
    GREEN(4,"green"),
    PURPLE(5,"purple");

    public static final Codec<FilmType> CODEC = StringRepresentable.fromEnum(FilmType::values);
    private static final IntFunction<FilmType> BY_ID = ByIdMap.continuous(FilmType::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, FilmType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, FilmType::getId);

    private final int id;
    public final String name;

    FilmType(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
