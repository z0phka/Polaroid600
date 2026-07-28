package net.sophka.polaroid.data.darkslide;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.IntFunction;

public class Darkslide{
    private final String identifier;
    private final String text;
    private final Identifier image;
    private final Type type;

    private DarkslideSeries series = null;

    public Darkslide(String identifier, String text, Optional<Identifier> image, Type type){
        this.identifier = identifier;
        this.text = text;
        this.image = image.orElse(null);
        this.type = type;
    }

    public static final Codec<Darkslide> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("id").forGetter(Darkslide::identifier),
                    Codec.STRING.optionalFieldOf("text", "").forGetter(Darkslide::text),
                    Identifier.CODEC.optionalFieldOf("image").forGetter(Darkslide::image),
                    Type.COODEC.optionalFieldOf("type", Type.TEXT_ONLY).forGetter(Darkslide::type)
            ).apply(instance, Darkslide::new));

    public static final StreamCodec<ByteBuf, Darkslide> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.STRING_UTF8,
                    Darkslide::identifier,
                    ByteBufCodecs.STRING_UTF8,
                    Darkslide::text,
                    ByteBufCodecs.optional(Identifier.STREAM_CODEC),
                    Darkslide::image,
                    Type.STREAM_CODEC,
                    Darkslide::type,
                    Darkslide::new);

    public enum Type implements StringRepresentable {
        TEXT_ONLY("text_only"),
        WITH_IMAGE("with_image");
        public static final Codec<Type> COODEC = StringRepresentable.fromEnum(Type::values);

        private static final IntFunction<Type> BY_ID = ByIdMap.continuous(Type::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, Type> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Type::ordinal);

        public final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    public String identifier() {
        return identifier;
    }

    public String text() {
        return text;
    }

    public Optional<Identifier> image() {
        return Optional.ofNullable(image);
    }

    public Type type() {
        return type;
    }

    public DarkslideSeries series(){
        return series;
    }

    public int ordinal(){
        return series == null ? 0 : series.indexOf(this) + 1;
    }

    protected void setSeries(DarkslideSeries series){
        this.series = series;
    }
}
