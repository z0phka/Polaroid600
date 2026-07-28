package net.sophka.polaroid.world.entity.tripod;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import net.sophka.polaroid.Polaroid600;
import net.sophka.polaroid.config.ServerConfig;
import net.sophka.polaroid.init.ModItems;
import net.sophka.polaroid.init.ModSounds;
import net.sophka.polaroid.server.photo.ServerPhotoTaker;
import net.sophka.polaroid.world.item.CameraItem;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CameraTripodEntity extends LivingEntity {
    private static final EntityDataAccessor<ItemStack> DATA_CAMERA = SynchedEntityData.defineId(CameraTripodEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_PHOTO = SynchedEntityData.defineId(CameraTripodEntity.class, EntityDataSerializers.ITEM_STACK);

    private UUID viewThroughPlayerID = null;
    private int timer = -1;
    private boolean poweredPrev = false;
    public long lastHit;

    private final TripodCameraPartEntity cameraPartEntity;

    public CameraTripodEntity(EntityType<? extends CameraTripodEntity> type, Level level) {
        super(type, level);
        this.cameraPartEntity = new TripodCameraPartEntity(this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_CAMERA, ItemStack.EMPTY);
        entityData.define(DATA_PHOTO, ItemStack.EMPTY);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        ItemStack cameraStack = this.getCamera();
        if (!cameraStack.isEmpty()) {
            output.store("Camera", ItemStack.CODEC, cameraStack);
        }
        ItemStack photoStack = this.getPhoto();
        if (!photoStack.isEmpty()) {
            output.store("Photo", ItemStack.CODEC, photoStack);
        }
        output.store("Timer", Codec.INT, this.timer);
        output.store("YHeadRot", Codec.FLOAT, this.getYHeadRot());
        output.store("YBodyRot", Codec.FLOAT, this.yBodyRot);
        output.store("Powered", Codec.BOOL, this.poweredPrev);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setCamera(input.read("Camera", ItemStack.CODEC).orElse(ItemStack.EMPTY));
        this.setPhoto(input.read("Photo", ItemStack.CODEC).orElse(ItemStack.EMPTY));
        this.timer = input.read("Timer", Codec.INT).orElse(-1);
        this.setYHeadRot(input.read("YHeadRot", Codec.FLOAT).orElse(getYRot()));
        this.setYBodyRot(input.read("YBodyRot", Codec.FLOAT).orElse(getYRot()));
        this.poweredPrev = input.read("Powered", Codec.BOOL).orElse(false);

        this.yHeadRotO = this.getYHeadRot();
        this.yBodyRotO = this.yBodyRot;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.timer >= 0) {
            if (this.timer == 0) {
                shoot();
            } else if (this.timer % 20 == 0) {
                playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 2);
            }
            this.timer--;
        }

        if(ServerConfig.CAMERA_REDSTONE.get()){
            boolean redstonePowered = level().hasNeighborSignal(blockPosition());
            if (redstonePowered & !poweredPrev) {
                shoot();
            }
            poweredPrev = redstonePowered;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Vec3 cameraPosOld = position();

        Vec3 cameraPos = position().add(0,getBbHeight(),0);
        cameraPartEntity.setPos(cameraPos);
        cameraPartEntity.xOld = cameraPosOld.x();
        cameraPartEntity.yOld = cameraPosOld.y();
        cameraPartEntity.zOld = cameraPosOld.z();
        cameraPartEntity.xo = cameraPosOld.x();
        cameraPartEntity.yo = cameraPosOld.y();
        cameraPartEntity.zo = cameraPosOld.z();
    }

    public boolean canShoot() {
        if (!getPhoto().isEmpty()) {
            return false;
        }
        ItemStack cameraStack = getCamera();
        if (cameraStack.isEmpty() || !(cameraStack.getItem() instanceof CameraItem cameraItem)) {
            return false;
        }
        return cameraItem.canShoot(cameraStack);
    }

    public Optional<ServerPlayer> getViewThroughPlayer() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }
        MinecraftServer server = serverLevel.getServer();
        List<ServerPlayer> trackers = serverLevel.getChunkSource().chunkMap.getPlayers(this.chunkPosition(), false);

        if (viewThroughPlayerID != null) {
            ServerPlayer player = server.getPlayerList().getPlayer(viewThroughPlayerID);
            if (player != null && trackers.contains(player)) {
                return Optional.of(player);
            }
            viewThroughPlayerID = null;
        }

        if (trackers.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(trackers.getFirst());
    }

    public void shoot() {
        if (!canShoot()) {
            if(getCamera().getItem() instanceof CameraItem cameraItem){
                cameraItem.playEmptySound(level(), blockPosition());
            }
            return;
        }
        getViewThroughPlayer().ifPresent(viewThroughPlayer -> {
            Vec3 eyePosition = getEyePosition().add(getHeadLookAngle().scale(0.1875));
            ServerPhotoTaker.takePhoto(viewThroughPlayer, this::getCamera, this::ejectPhoto, eyePosition.x, eyePosition.y, eyePosition.z, 0, yHeadRot, 0);
            viewThroughPlayerID = viewThroughPlayer.getUUID();
        });
    }

    public void ejectPhoto(ItemStack photo) {
        this.setPhoto(photo);
        level().playSound(null, blockPosition(), ModSounds.CAMERA_SHUTTER.get(), SoundSource.PLAYERS, 0.3F,
                1.0F / (getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        Polaroid600.LOGGER.debug("{} {}", location, level().isClientSide());
        ItemStack itemStack = player.getItemInHand(hand);
        ItemStack cameraStack = getCamera();
        ItemStack photoStack = getPhoto();

        boolean clickedHead = location.y > 17/16f;
        boolean clickedCamera = location.y > this.getBbHeight();

        Polaroid600.LOGGER.debug("vec {} {} {}", location, clickedHead, clickedCamera);

        boolean hasCamera = !this.getCamera().isEmpty();
        boolean holdsCamera = !itemStack.isEmpty() && itemStack.getItem() instanceof CameraItem;
        boolean hasPhoto = !this.getPhoto().isEmpty();
        if (!player.level().isClientSide()) {
            viewThroughPlayerID = player.getUUID();
            if (clickedHead) {
                if (!hasCamera & holdsCamera) {
                    this.setCamera(itemStack);
                    itemStack.consume(1, player);
                    return InteractionResult.SUCCESS;
                } else if (hasCamera) {
                    if (clickedCamera) {
                        if (hasPhoto) {
                            if (!player.addItem(photoStack)) {
                                player.drop(photoStack, false);
                            }
                            this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1, 1);
                            setPhoto(ItemStack.EMPTY);
                        } else {
                            double dot = player.getHeadLookAngle().dot(getHeadLookAngle());
                            if(dot > 0){
                                if (!player.addItem(cameraStack)) {
                                    player.drop(cameraStack, false);
                                }
                                this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1, 1);
                                setCamera(ItemStack.EMPTY);
                            }
                            else{
                                if (player.isCrouching()) {
                                    this.timer = timerDuration();
                                } else {
                                    shoot();
                                }
                            }
                            Polaroid600.LOGGER.debug("DOT {}", dot);
                        }
                    } else {
                        this.setYHeadRot(player.getYRot());
                        this.yHeadRotO = this.getYHeadRot();
                    }
                }
                this.gameEvent(GameEvent.BLOCK_CHANGE, player);
            }
            return InteractionResult.SUCCESS;
        } else {
            return !hasCamera && !holdsCamera ? InteractionResult.PASS : InteractionResult.SUCCESS;
        }
    }

    private int timerDuration() {
        return 200;
    }

    @Override
    protected void tickHeadTurn(float yBodyRotT) {

    }

    @Override
    protected float getMaxHeadRotationRelativeToBody() {
        return 360.0F;
    }

    public ItemStack getCamera() {
        return this.getEntityData().get(DATA_CAMERA);
    }

    public void setCamera(ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            itemStack = itemStack.copyWithCount(1);
        }
        this.getEntityData().set(DATA_CAMERA, itemStack);
    }

    public ItemStack getPhoto() {
        return this.getEntityData().get(DATA_PHOTO);
    }

    public void setPhoto(ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            itemStack = itemStack.copyWithCount(1);
        }
        this.getEntityData().set(DATA_PHOTO, itemStack);
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.ARMOR_STAND_FALL, SoundEvents.ARMOR_STAND_FALL);
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    @Override
    public void kill(ServerLevel level) {
        this.remove(Entity.RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (this.isRemoved()) {
            return false;
        }

        if (!level.getGameRules().get(GameRules.MOB_GRIEFING) && source.getEntity() instanceof Mob) {
            return false;
        }

        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.kill(level);
            return false;
        }

        if (this.isInvulnerableTo(level, source)) {
            return false;
        }

        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            this.brokenByAnything(level, source);
            this.kill(level);
            return false;
        }

        if (source.is(DamageTypeTags.IGNITES_ARMOR_STANDS)) {
            if (this.isOnFire()) {
                this.causeDamage(level, source, 0.15F);
            } else {
                this.igniteForSeconds(5.0F);
            }

            return false;
        } else if (source.is(DamageTypeTags.BURNS_ARMOR_STANDS) && this.getHealth() > 0.5F) {
            this.causeDamage(level, source, 4.0F);
            return false;
        } else {
            boolean allowIncrementalBreaking = source.is(DamageTypeTags.CAN_BREAK_ARMOR_STAND);
            boolean shouldKill = source.is(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS);
            if (!allowIncrementalBreaking && !shouldKill) {
                return false;
            } else if (source.getEntity() instanceof Player player && !player.getAbilities().mayBuild) {
                return false;
            } else {
                if (source.isCreativePlayer()) {
                    this.playBrokenSound();
                    this.showBreakingParticles();
                    this.kill(level);
                    return true;
                }

                long time = level.getGameTime();
                if (time - this.lastHit > 5L && !shouldKill) {
                    level.broadcastEntityEvent(this, (byte)32);
                    this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
                    this.lastHit = time;
                } else {
                    this.brokenByPlayer(level, source);
                    this.showBreakingParticles();
                    this.kill(level);
                }

                return true;
            }
        }
    }

    private void brokenByPlayer(ServerLevel level, DamageSource source) {
        ItemStack result = new ItemStack(ModItems.CAMERA_TRIPOD.get());
        result.set(DataComponents.CUSTOM_NAME, this.getCustomName());
        Block.popResource(this.level(), this.blockPosition(), result);
        this.brokenByAnything(level, source);
    }

    private void brokenByAnything(ServerLevel level, DamageSource source) {
        this.playBrokenSound();
        this.dropAllDeathLoot(level, source);

        ItemStack camera = this.getCamera();
        ItemStack photo = this.getPhoto();

        if (!camera.isEmpty() && !EnchantmentHelper.has(camera, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
            Block.popResource(this.level(), this.blockPosition().above(), camera);
        }

        if (!photo.isEmpty() && !EnchantmentHelper.has(photo, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
            Block.popResource(this.level(), this.blockPosition().above(), photo);
        }
    }
    private void causeDamage(ServerLevel level, DamageSource source, float dmg) {
        float health = this.getHealth();
        health -= dmg;
        if (health <= 0.5F) {
            this.brokenByAnything(level, source);
            this.kill(level);
        } else {
            this.setHealth(health);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
        }
    }

    private void playBrokenSound() {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
    }

    private void showBreakingParticles() {
        if (this.level() instanceof ServerLevel) {
            ((ServerLevel)this.level())
                    .sendParticles(
                            new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ANVIL.defaultBlockState()),
                            this.getX(),
                            this.getY(0.6666666666666666),
                            this.getZ(),
                            10,
                            this.getBbWidth() / 4.0F,
                            this.getBbHeight() / 4.0F,
                            this.getBbWidth() / 4.0F,
                            0.05
                    );
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 32) {
            if (this.level().isClientSide()) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3F, 1.0F, false);
                this.lastHit = this.level().getGameTime();
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    @Nullable
    public PartEntity<?>[] getParts() {
        return new PartEntity[]{this.cameraPartEntity};
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        PartEntity[] subEntities = this.getParts();

        for (int i = 0; i < subEntities.length; i++) {
            subEntities[i].setId(i + packet.getId() + 1);
        }
    }
}
