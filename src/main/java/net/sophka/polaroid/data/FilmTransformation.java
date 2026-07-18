package net.sophka.polaroid.data;

import com.mojang.serialization.Codec;
import net.sophka.polaroid.init.ModFilmTransformations;

public interface FilmTransformation extends Transformation{

    static Codec<FilmTransformation> dispatchCodec() {
        return ModFilmTransformations.CODEC
                .dispatch(
                        FilmTransformation::type,
                        FilmTransformationType::codec
                );
    }

    FilmTransformationType<?> type();
}
