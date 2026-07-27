package net.sophka.polaroid.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.world.item.FilmFormat;
import net.sophka.polaroid.world.item.FilmType;
import net.sophka.polaroid.world.item.component.CameraCartridge;
import net.sophka.polaroid.world.item.component.FilmContent;
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
                    .persistent(FilmType.CODEC)
                    .networkSynchronized(FilmType.STREAM_CODEC));

    public static final Supplier<DataComponentType<FilmFormat>> FILM_FORMAT = DATA_COMPONENTS.registerComponentType(
            "film_format",
            builder -> builder
                    .persistent(FilmFormat.CODEC)
                    .networkSynchronized(FilmFormat.STREAM_CODEC));

    public static final Supplier<DataComponentType<FilmContent>> FILM_CONTENT = DATA_COMPONENTS.registerComponentType("film_content",
            builder -> builder.persistent(FilmContent.CODEC).networkSynchronized(FilmContent.STREAM_CODEC));

    public static final Supplier<DataComponentType<CameraCartridge>> CAMERA_CARTRIDGE = DATA_COMPONENTS.registerComponentType("camera_cartridge",
            builder -> builder.persistent(CameraCartridge.CODEC).networkSynchronized(CameraCartridge.STREAM_CODEC));

    public static final Supplier<DataComponentType<DoubleExposure>> DOUBLE_EXPOSURE = DATA_COMPONENTS.registerComponentType("double_exposure",
            builder -> builder.persistent(DoubleExposure.CODEC).networkSynchronized(DoubleExposure.STREAM_CODEC));

    public static final Supplier<DataComponentType<Boolean>> AF = DATA_COMPONENTS.registerComponentType(
            "autofocus",
            builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL));

    public static final Supplier<DataComponentType<Identifier>> DARKSLIDE = DATA_COMPONENTS.registerComponentType(
            "darkslide",
            builder -> builder
                    .persistent(Identifier.CODEC)
                    .networkSynchronized(Identifier.STREAM_CODEC));

    public static final Supplier<DataComponentType<Boolean>> INITIALIZED = DATA_COMPONENTS.registerComponentType(
            "initialized",
            builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL));

    public static final Supplier<DataComponentType<Boolean>> LAND_CAMERA = DATA_COMPONENTS.registerComponentType(
            "land_camera",
            builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL));
}
