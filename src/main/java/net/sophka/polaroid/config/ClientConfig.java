package net.sophka.polaroid.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SELFIE_MIRROR = BUILDER
            .comment("Whether the rendering of selfie mirrors should be allowed. Decreases performance a bit. Causes unexpected graphical issues with some rendering mods. If you use Sodium or shaders, you almost certainly want this to be FALSE!")
            .translation("polaroid600.configuration.selfieMirrorAllowed")
            .define("selfieMirror.selfieMirrorAllowed", true);

    public static final ModConfigSpec.IntValue SELFIE_MIRROR_RESOLUTION = BUILDER
            .comment("The resolution of the selfie mirror texture.")
            .translation("polaroid600.configuration.selfieMirrorResolution")
            .defineInRange("selfieMirror.selfieMirrorResolution", 64, 1, 2048);

    public static final ModConfigSpec.IntValue SELFIE_MIRROR_UPDATE_PERIOD = BUILDER
            .comment("How many render ticks between selfie mirror updates.")
            .translation("polaroid600.configuration.selfieMirrorUpdatePeriod")
            .defineInRange("selfieMirror.selfieMirrorUpdatePeriod", 20, 1, 20000);

    public static final ModConfigSpec.BooleanValue CUSTOM_MARKER_FONT = BUILDER
            .comment("Whether should use a non-vanilla marker font for the text on photos.")
            .translation("polaroid600.configuration.customMarkerFont")
            .define("photo.customMarkerFont", true);

    public static final ModConfigSpec.BooleanValue OPACIFIER_OVERLAY = BUILDER
            .comment("Whether should the blue opacifier overlay be rendered. Some shaders have issues with it, so in such a case set to FALSE.")
            .translation("polaroid600.configuration.opacifierOverlay")
            .define("photo.opacifierOverlay", true);

    public static final ModConfigSpec.BooleanValue SOLARIZATION_OVERLAY = BUILDER
            .comment("Whether should the white solarization overlay be rendered. Some shaders have issues with it, so in such a case set to FALSE.")
            .translation("polaroid600.configuration.solarizationOverlay")
            .define("photo.solarizationOverlay", true);

    public static final ModConfigSpec.BooleanValue DARKSLIDES_IN_CREATIVE_TAB = BUILDER
            .comment("Whether should be darkslides available in the creative tab.")
            .translation("polaroid600.configuration.darkslidesInCreativeTab")
            .worldRestart()
            .define("misc.darkslidesInCreativeTab", false);
    public static final ModConfigSpec.BooleanValue DARKSLIDES_IN_CREATIVE_MODE = BUILDER
            .comment("Whether cameras should eject darkslides when in creative mode.")
            .translation("polaroid600.configuration.darkslidesInCreativeMode")
            .define("misc.darkslidesInCreativeMode", true);

    public static final ModConfigSpec.DoubleValue FLASH_RANGE = BUILDER
            .comment("The range of a camera flash in blocks.")
            .translation("polaroid600.configuration.flashRange")
            .defineInRange("flash.flashRange", 6d, 0d, 16d);

    public static final ModConfigSpec.DoubleValue FLASH_STRENGTH = BUILDER
            .comment("The strength of a camera flash in blocks.")
            .translation("polaroid600.configuration.flashStrength")
            .defineInRange("flash.flashStrength", 15d, 0d, 15d);

    public static final ModConfigSpec.BooleanValue FLASH_ENABLED = BUILDER
            .comment("Whether camera flash should be used.")
            .translation("polaroid600.configuration.flashEnabled")
            .define("flash.flashEnabled", true);


    public static final ModConfigSpec SPEC = BUILDER.build();
}
