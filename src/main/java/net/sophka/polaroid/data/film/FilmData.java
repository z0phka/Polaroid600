package net.sophka.polaroid.data.film;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.sophka.polaroid.world.item.FilmFormat;

import java.util.List;

public record FilmData(FilmFormat format, List<FilmTransformation> transformations) {

    public static final Codec<FilmData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StringRepresentable.fromEnum(FilmFormat::values).fieldOf("format").forGetter(FilmData::format),
            FilmTransformation.dispatchCodec().listOf().fieldOf("transformations").forGetter(FilmData::transformations))
            .apply(instance, FilmData::new));
}
