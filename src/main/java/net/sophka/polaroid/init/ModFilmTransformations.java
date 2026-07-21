package net.sophka.polaroid.init;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.data.film.FilmTransformationType;
import net.sophka.polaroid.data.film.transformations.*;

import java.util.function.Supplier;

public class ModFilmTransformations {
    public static final DeferredRegister<FilmTransformationType<?>> TYPES =
            DeferredRegister.create(ModRegistries.FILM_TRANSFORMATION_TYPE_KEY, Polaroid600.MODID);

    public static final Codec<FilmTransformationType<?>> CODEC = Codec.lazyInitialized(ModRegistries.FILM_TRANSFORMATION_TYPE::byNameCodec);

    public static final Supplier<FilmTransformationType<LinearColorTransformation>> LINEAR_COLOR_TRANSFORMATION =
            TYPES.register(
                    "linear_color",
                    () -> () -> LinearColorTransformation.CODEC
            );

    public static final Supplier<FilmTransformationType<LinearCombinationTransformation>> LINEAR_COMBINATION_TRANSFORMATION =
            TYPES.register(
                    "linear_combination",
                    () -> () -> LinearCombinationTransformation.CODEC
            );

    public static final Supplier<FilmTransformationType<VignetteTransformation>> VIGNETTE =
            TYPES.register(
                    "vignette",
                    () -> () -> VignetteTransformation.CODEC
            );

    public static final Supplier<FilmTransformationType<QuantizationTransformation>> QUANTIZATION =
            TYPES.register(
                    "quantization",
                    () -> () -> QuantizationTransformation.CODEC
            );

    public static final Supplier<FilmTransformationType<ShiftTransformation>> SHIFT =
            TYPES.register(
                    "shift",
                    () -> () -> ShiftTransformation.CODEC
            );

    public static final Supplier<FilmTransformationType<AberrationTransformation>> ABERRATION =
            TYPES.register(
                    "aberration",
                    () -> () -> AberrationTransformation.CODEC
            );

    public static final Supplier<FilmTransformationType<BlendTransformation>> BLEND =
            TYPES.register(
                    "blend",
                    () -> () -> BlendTransformation.CODEC
            );

    public static final Supplier<FilmTransformationType<HalationTransformation>> HALATION =
            TYPES.register(
                    "halation",
                    () -> () -> HalationTransformation.CODEC
            );

    public static final Supplier<FilmTransformationType<PowerColorTransformation>> POWER_COLOR_TRANSFORMATION =
            TYPES.register(
                    "power_color",
                    () -> () -> PowerColorTransformation.CODEC
            );

    public static final Supplier<FilmTransformationType<GrainTransformation>> GRAIN =
            TYPES.register(
                    "grain",
                    () -> () -> GrainTransformation.CODEC
            );

    public static final Supplier<FilmTransformationType<ExposureAdjustmentTransformation>> EXPOSURE_ADJUSTMENT =
            TYPES.register(
                    "exposure_adjustment",
                    () -> () -> ExposureAdjustmentTransformation.CODEC
            );
}
