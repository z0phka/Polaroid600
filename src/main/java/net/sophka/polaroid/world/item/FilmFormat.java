package net.sophka.polaroid.world.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum FilmFormat implements StringRepresentable {
    MISSING(0,"missing", 0,0),
    _600(1,"600",512,512),
    _SX_70(2,"sx-70",512,512),
    _GO(3,"go",512,512),
    _1200(4,"1200",604,476),
    _20x24(5,"20x24",512,512);

    public static final Codec<FilmFormat> CODEC = StringRepresentable.fromEnum(FilmFormat::values);
    private static final IntFunction<FilmFormat> BY_ID = ByIdMap.continuous(FilmFormat::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, FilmFormat> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, FilmFormat::getId);

    private final int id;
    public final int width;
    public final int height;
    public final String name;

    FilmFormat(int id, String name, int width, int height){
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public static FilmFormat byName(String name){
        for(FilmFormat format : values()){
            if(format.name.equals(name)){
                return format;
            }
        }
        return MISSING;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
