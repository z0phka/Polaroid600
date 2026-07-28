package net.sophka.polaroid.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue DEVELOPMENT_TIME = BUILDER
            .comment("The amount of time in ticks it takes for a photo to develop.")
            .translation("polaroid600.configuration.developmentTime")
            .defineInRange("film.developmentTime", 1200, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SOLARIZATION_TIME = BUILDER
            .comment("The amount of time in ticks after ejection, during which the photo is susceptible to sun damage")
            .translation("polaroid600.configuration.solarizationTime")
            .defineInRange("film.solarizationTime", 400, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SOLARIZATION_GRACE_AMOUNT = BUILDER
            .comment("For how many ticks can the photo be under the sun, before solarization effects are visible. Should be less than Solarization Time")
            .translation("polaroid600.configuration.solarizationGraceAmount")
            .defineInRange("film.solarizationGraceAmount", 100, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue SOLARIZATION_ENABLED = BUILDER
            .comment("Whether solarization should be enabled.")
            .translation("polaroid600.configuration.solarizationEnabled")
            .define("film.solarizationEnabled", true);

    public static final ModConfigSpec.EnumValue<FilmMode> CAMERA_FILM_MODE = BUILDER
            .comment("Whether should cameras directly accept film like a bundle, or they should accept only cartridges filled with film")
            .translation("polaroid600.configuration.filmMode")
            .defineEnum("camera.filmMode", FilmMode.CARTRIDGE);

    public static final ModConfigSpec.BooleanValue CAMERA_REDSTONE = BUILDER
            .comment("Whether redstone interactions with a tripod should be enabled.")
            .translation("polaroid600.configuration.redstone")
            .define("camera.redstone", true);

    public static final ModConfigSpec.IntValue CAMERA_COOLDOWN = BUILDER
            .comment("How many ticks should a cooldown for a camera after taking a photo take.")
            .translation("polaroid600.configuration.cameraCooldown")
            .defineInRange("camera.cameraCooldown", 60,0,Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue DARKSLIDE_EJECTION = BUILDER
            .comment("Whether darkslides should be ejected when filing camera with a full cartridge. Requires Camera Film Mode to be set to CARTRIDGE")
            .translation("polaroid600.configuration.darkslideEjection")
            .define("camera.darkslideEjection", true);



    public static final ModConfigSpec SPEC = BUILDER.build();

}
