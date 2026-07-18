package net.sophka.polaroid.world.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.sophka.polaroid.init.ModEntityTypes;

public class CameraViewEntity extends Entity {

    public CameraViewEntity(EntityType<? extends CameraViewEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.blocksBuilding = false;
        this.setNoGravity(true);
        this.setDeltaMovement(Vec3.ZERO);
    }

    public CameraViewEntity(Level level) {
        super(ModEntityTypes.CAMERA_VIEW.get(), level);
    }

    @Override
    public void tick() {
        this.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public boolean isSpectator() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {

    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {

    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {

    }

    public void updateForPlayer(Player player) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 armsStart = player.position().add(0,0.85 * player.getBbHeight(),0);


        float lookYaw = player.yBodyRot;
        Vec3 forwardVector = Vec3.directionFromRotation(player.getXRot(), lookYaw);

        double armLength = 0.625;
        Vec3 selfieCamPos = armsStart.add(forwardVector.scale(armLength));

        setPositionAndRotation(selfieCamPos.x, selfieCamPos.y, selfieCamPos.z, -player.getXRot(), player.yBodyRot + 180.0F);
    }

    public void setPositionAndRotation(double x, double y, double z, float xRot, float yRot){
        this.setPos(x,y,z);
        this.xOld = x;
        this.yOld = y;
        this.zOld = z;

        this.setXRot(xRot);
        this.setYRot(yRot);
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }
}
