package net.sophka.polaroid.world.entity.tripod;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

public class TripodCameraPartEntity extends PartEntity<CameraTripodEntity> {
    private final EntityDimensions dimensions;

    public TripodCameraPartEntity(CameraTripodEntity parent) {
        super(parent);
        this.dimensions = EntityDimensions.scalable(0.4f, 0.4f);
        refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.dimensions;
    }

    @Override
    public final boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return this.getParent().hurtServer(level, source, damage);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {

    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || this.getParent() == entity;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {

    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {

    }
    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        return this.getParent().interact(player, hand, location.add(0,this.getParent().getBbHeight(),0));
    }
}
