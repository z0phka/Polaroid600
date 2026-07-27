package net.sophka.polaroid.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue DEVELOPMENT_TIME = BUILDER
            .comment("The amount of time in ticks it takes for a photo to develop.")
            .defineInRange("film.developmentTime", 1200, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SOLARIZATION_TIME = BUILDER
            .comment("The amount of time in ticks after ejection, during which the photo is susceptible to sun damage")
            .defineInRange("film.solarizationTime", 300, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.EnumValue<FilmMode> CAMERA_FILM_MODE = BUILDER
            .comment("Whether should cameras directly accept film like a bundle, or they should accept only cartridges filled with film")
            .defineEnum("camera.filmMode", FilmMode.CARTRIDGE);

    public static final ModConfigSpec.BooleanValue CAMERA_REDSTONE = BUILDER
            .comment("Whether redstone interactions with a tripod should be enabled.")
            .define("camera.redstone", true);


    public static final ModConfigSpec SPEC = BUILDER.build();

}
