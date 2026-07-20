package net.sophka.polaroid.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SELFIE_MIRROR = BUILDER
            .comment("Whether the rendering of selfie mirrors should be allowed. Decreases performance a bit. Causes unexpected graphical issues with some rendering mods. If you use Sodium or shaders, you almost certainly want this to be FALSE!")
            .define("selfieMirror.allowed", true);

    public static final ModConfigSpec.IntValue SELFIE_MIRROR_RESOLUTION = BUILDER
            .comment("The resolution of the selfie mirror texture.")
            .defineInRange("selfieMirror.resolution", 64, 1, 2048);

    public static final ModConfigSpec.IntValue SELFIE_MIRROR_UPDATE_PERIOD = BUILDER
            .comment("How many render ticks between selfie mirror updates.")
            .defineInRange("selfieMirror.period", 20, 1, 20000);

    public static final ModConfigSpec.BooleanValue CUSTOM_MARKER_FONT = BUILDER
            .comment("Whether should use a non-vanilla marker font for the text on photos.")
            .define("photo.customMarkerFont", true);


    public static final ModConfigSpec SPEC = BUILDER.build();
}
