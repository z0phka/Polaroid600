package net.sophka.polaroid.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.world.item.FilmFormat;
import net.sophka.polaroid.world.item.FilmType;
import net.sophka.polaroid.world.item.component.CameraFilm;
import net.sophka.polaroid.world.item.component.DoubleExposure;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE,Polaroid600.MODID);
    public static final Supplier<DataComponentType<String>> PHOTO = DATA_COMPONENTS.registerComponentType(
            "photo",
            builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8));

    public static final Supplier<DataComponentType<Integer>> EXPOSURE = DATA_COMPONENTS.registerComponentType(
            "exposure",
            builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT));

    public static final Supplier<DataComponentType<Integer>> SUN_DAMAGE = DATA_COMPONENTS.registerComponentType(
            "sun_damage",
            builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT));

    public static final Supplier<DataComponentType<Long>> CREATED_TIME = DATA_COMPONENTS.registerComponentType(
            "created_time",
            builder -> builder
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.LONG));

    public static final Supplier<DataComponentType<FilmType>> FILM_TYPE = DATA_COMPONENTS.registerComponentType(
            "film_type",
            builder -> builder
                    .persistent(FilmType.COODEC)
                    .networkSynchronized(FilmType.STREAM_CODEC));

    public static final Supplier<DataComponentType<FilmFormat>> FILM_FORMAT = DATA_COMPONENTS.registerComponentType(
            "film_format",
            builder -> builder
                    .persistent(FilmFormat.CODEC)
                    .networkSynchronized(FilmFormat.STREAM_CODEC));

    public static final Supplier<DataComponentType<CameraFilm>> CAMERA_FILM = DATA_COMPONENTS.registerComponentType("camera_film",
            builder -> builder.persistent(CameraFilm.CODEC).networkSynchronized(CameraFilm.STREAM_CODEC));

    public static final Supplier<DataComponentType<DoubleExposure>> DOUBLE_EXPOSURE = DATA_COMPONENTS.registerComponentType("double_exposure",
            builder -> builder.persistent(DoubleExposure.CODEC).networkSynchronized(DoubleExposure.STREAM_CODEC));

    public static final Supplier<DataComponentType<Boolean>> AF = DATA_COMPONENTS.registerComponentType(
            "autofocus",
            builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL));
}
