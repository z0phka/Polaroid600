package net.sophka.polaroid.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue DEVELOPMENT_TIME = BUILDER
            .comment("The amount of time in ticks it takes for a photo to develop.")
            .defineInRange("developmentTime", 1200, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SOLARIZATION_TIME = BUILDER
            .comment("The amount of time in ticks after ejection, during which the photo is susceptible to sun damage")
            .defineInRange("solarizationTime", 300, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue WALL_GRID_SIZE = BUILDER
            .comment("The width and height of the grid of photos on a single block.")
            .defineInRange("wallGridSize", 5, 0, 7);


    public static final ModConfigSpec SPEC = BUILDER.build();

}
