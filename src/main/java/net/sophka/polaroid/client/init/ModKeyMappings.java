package net.sophka.polaroid.client.init;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.sophka.polaroid.Polaroid600;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings {

    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(Polaroid600.MODID,"camera"));

    public static final KeyMapping CAMERA_INCREASE_EXPOSURE = new KeyMapping("camera.exposure.increase", GLFW.GLFW_KEY_KP_ADD, CATEGORY);
    public static final KeyMapping CAMERA_DECREASE_EXPOSURE = new KeyMapping("camera.exposure.decrease", GLFW.GLFW_KEY_KP_SUBTRACT, CATEGORY);
    public static final KeyMapping CAMERA_AUTOFOCUS_TOGGLE = new KeyMapping("camera.autofocus", GLFW.GLFW_KEY_KP_MULTIPLY, CATEGORY);
    public static final KeyMapping CAMERA_DOUBLE_EXPOSURE_TOGGLE = new KeyMapping("camera.double_exposure", GLFW.GLFW_KEY_KP_DIVIDE, CATEGORY);
    public static final KeyMapping CAMERA_SELFIE_MODE = new KeyMapping("camera.selfie_mode", GLFW.GLFW_KEY_R, CATEGORY);
    public static final KeyMapping CAMERA_FLASH_TOGGLE = new KeyMapping("camera.flash", GLFW.GLFW_KEY_KP_9, CATEGORY);

}
