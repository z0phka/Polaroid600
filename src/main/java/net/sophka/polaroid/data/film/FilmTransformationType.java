package net.sophka.polaroid.data.film;

import com.mojang.serialization.MapCodec;

public interface FilmTransformationType<T extends FilmTransformation> {
    MapCodec<T> codec();
}
