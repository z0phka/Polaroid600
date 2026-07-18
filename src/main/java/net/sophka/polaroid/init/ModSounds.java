package net.sophka.polaroid.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sophka.polaroid.Polaroid600;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT,
            Polaroid600.MODID);
    public static final Supplier<SoundEvent> CAMERA_SHUTTER = SOUNDS.register("camera.shutter",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Polaroid600.MODID, "camera.shutter")));
}
