package net.sophka.polaroid.data;

import com.mojang.serialization.MapCodec;

public interface FilmTransformationType<T extends FilmTransformation> {
    MapCodec<T> codec();
}
