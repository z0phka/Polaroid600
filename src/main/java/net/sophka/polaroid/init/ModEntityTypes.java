package net.sophka.polaroid.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.world.entity.tripod.CameraTripodEntity;
import net.sophka.polaroid.world.entity.CameraViewEntity;

import java.util.function.Supplier;

public class ModEntityTypes {
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(Polaroid600.MODID);

    public static final Supplier<EntityType<CameraViewEntity>> CAMERA_VIEW = ENTITY_TYPES.registerEntityType(
            "camera_view", CameraViewEntity::new, MobCategory.MISC,
            builder -> builder.sized(0f, 0f).eyeHeight(0).noSummon().noSave());

    public static final Supplier<EntityType<CameraTripodEntity>> CAMERA_TRIPOD = ENTITY_TYPES.registerEntityType(
            "camera_tripod", CameraTripodEntity::new, MobCategory.MISC,
            builder -> builder.sized(0.5f, 1.3125f).eyeHeight(1.45f));

}
