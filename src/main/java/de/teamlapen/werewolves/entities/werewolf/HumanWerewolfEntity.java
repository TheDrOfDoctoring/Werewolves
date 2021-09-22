package de.teamlapen.werewolves.entities.werewolf;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.entity.goals.LookAtClosestVisibleGoal;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.werewolves.config.WerewolvesConfig;
import de.teamlapen.werewolves.core.ModEntities;
import de.teamlapen.werewolves.player.WerewolfForm;
import de.teamlapen.werewolves.util.Helper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

public class HumanWerewolfEntity extends CreatureEntity implements WerewolfTransformable {
    private static final DataParameter<Integer> FORM = EntityDataManager.defineId(HumanWerewolfEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> TYPE = EntityDataManager.defineId(HumanWerewolfEntity.class, DataSerializers.INT);

    private final EntityClassType classType;
    private final EntityActionTier actionTier;

    protected int rage;

    public HumanWerewolfEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
        this.classType = EntityClassType.getRandomClass(this.getRandom());
        this.actionTier = EntityActionTier.Medium;
    }

    public static boolean spawnPredicateHumanWerewolf(EntityType<? extends CreatureEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        if (world.getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) return false;
        if (!MobEntity.checkMobSpawnRules(entityType, world, spawnReason, blockPos, random)) return false;
        if (random.nextInt(3) != 0) return false;
        if (world.canSeeSkyFromBelowWater(blockPos) && MonsterEntity.isDarkEnoughToSpawn(world, blockPos, random))  {
            return true;
        }
        return Helper.isInWerewolfBiome(world, blockPos) && blockPos.getY() >= world.getSeaLevel();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(FORM, -1);
        this.getEntityData().define(TYPE, -1);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.2));

        this.goalSelector.addGoal(9, new RandomWalkingGoal(this, 0.7));
        this.goalSelector.addGoal(10, new LookAtClosestVisibleGoal(this, PlayerEntity.class, 20F, 0.6F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, HunterBaseEntity.class, 17F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, VampireBaseEntity.class, 17F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        if (super.hurt(source, amount)) {
            this.rage += amount * 10;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.rage > 150) {
            WerewolfTransformable werewolf = this.transformToWerewolf(TransformType.TIME_LIMITED);
            ((MobEntity) werewolf).setLastHurtByMob(this.getTarget());
        }
        if (this.level.getGameTime() % 100 == 10) {
            if (Helper.isFullMoon(this.level)) {
                this.transformToWerewolf(TransformType.FULL_MOON);
            }
        }
    }

    @Override
    public void reset() {
        this.rage = 0;
    }

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return CreatureEntity.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, WerewolvesConfig.BALANCE.MOBPROPS.human_werewolf_speed.get())
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.ATTACK_DAMAGE, WerewolvesConfig.BALANCE.MOBPROPS.human_werewolf_attack_damage.get())
                .add(Attributes.MAX_HEALTH, WerewolvesConfig.BALANCE.MOBPROPS.human_werewolf_max_health.get());
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("form")) {
            int t = compound.getInt("form");
            this.getEntityData().set(FORM, t < 2 && t >= 0 ? t : -1);
        }
        if (compound.contains("type")) {
            int t = compound.getInt("type");
            this.getEntityData().set(TYPE, t < 126 && t >= 0 ? t : -1);
        }
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("form", this.getEntityData().get(FORM));
        compound.putInt("type", this.getEntityData().get(TYPE));
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.getEntityData().get(FORM) == -1) {
            this.getEntityData().set(FORM, this.getRandom().nextInt(2));
        }
        if (this.getEntityData().get(TYPE) == -1) {
            this.getEntityData().set(TYPE, this.getRandom().nextInt(126));
        }
    }

    @Override
    public int getEntityTextureType() {
        int i = this.getEntityData().get(TYPE);
        return Math.max(i, 0);
    }

    @Override
    public int getEyeTextureType() {
        return 0;
    }

    @Override
    public BasicWerewolfEntity _transformToWerewolf() {
        EntityType<? extends BasicWerewolfEntity> type;
        if (this.getEntityData().get(FORM) == 0) {
            type = ModEntities.werewolf_beast;
        } else {
            type = ModEntities.werewolf_survivalist;
        }
        BasicWerewolfEntity werewolf = WerewolfTransformable.copyData(type, this);
        werewolf.setSourceEntity(this);
        return werewolf;
    }

    @Override
    public EntityActionTier getEntityTier() {
        return this.actionTier;
    }

    @Override
    public EntityClassType getEntityClass() {
        return this.classType;
    }

    @Override
    public WerewolfTransformable _transformBack() {
        return this;
    }

    @Override
    public boolean canTransform() {
        return !this.level.isClientSide && Helper.isNight(this.level) && this.rage > 0;
    }

    @Nonnull
    @Override
    public WerewolfForm getWerewolfForm() {
        switch (this.getEntityData().get(FORM)) {
            case 0:
                return WerewolfForm.BEAST;
            case 1:
                return WerewolfForm.SURVIVALIST;
            default:
                throw new IllegalStateException("Werewolf form is not set");
        }
    }
}
