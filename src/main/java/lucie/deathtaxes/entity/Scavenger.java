package lucie.deathtaxes.entity;

import com.mojang.serialization.Dynamic;
import lucie.deathtaxes.client.particle.FootprintParticleOption;
import lucie.deathtaxes.registry.SoundEventRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Scavenger extends PathfinderMob
{
    private static final EntityDataAccessor<ItemStack> CONSUMING_ITEMSTACK_ID = SynchedEntityData.defineId(Scavenger.class, EntityDataSerializers.ITEM_STACK);

    public Scavenger(EntityType<? extends PathfinderMob> entityType, Level level)
    {
        super(entityType, level);
        this.getNavigation().setCanFloat(true);
        this.entityData.define(CONSUMING_ITEMSTACK_ID, ItemStack.EMPTY);
    }

    public static AttributeSupplier registerAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.FOLLOW_RANGE, 12.0F)
                .add(Attributes.MAX_HEALTH, 24.0F)
                .add(Attributes.ATTACK_DAMAGE, 0.5F)
                .build();
    }

    @Nonnull
    @Override
    protected Brain.Provider<?> brainProvider()
    {
        return Brain.provider(ScavengerAi.MEMORY_TYPES, ScavengerAi.SENSOR_TYPES);
    }

    @Nonnull
    @Override
    protected Brain<?> makeBrain(@Nonnull Dynamic<?> dynamic)
    {
        return ScavengerAi.makeBrain(this, dynamic);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Brain<Scavenger> getBrain()
    {
        return (Brain<Scavenger>) super.getBrain();
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount)
    {
        if (super.hurt(source, amount))
        {
            Entity entity = source.getEntity();

            if (entity instanceof LivingEntity livingEntity && this.canAttack(livingEntity))
            {
                this.brain.setMemory(MemoryModuleType.ANGRY_AT, livingEntity.getUUID());
                this.brain.setMemory(MemoryModuleType.ATTACK_TARGET, livingEntity);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entity)
    {
        if (super.doHurtTarget(entity))
        {
            if (entity instanceof LivingEntity livingEntity)
            {
                float difficulty = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, Mth.floor(140.0F * difficulty)), this);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean canBeAffected(@Nonnull MobEffectInstance effectInstance)
    {
        return effectInstance.getEffect() != MobEffects.WITHER && super.canBeAffected(effectInstance);
    }

    @Override
    public boolean canBeLeashed(@Nonnull Player player)
    {
        return false;
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public SpawnGroupData finalizeSpawn(@Nonnull ServerLevelAccessor level, @Nonnull DifficultyInstance difficulty, @Nonnull MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag compoundTag)
    {
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);

        return super.finalizeSpawn(level, difficulty, reason, spawnData, compoundTag);
    }

    @Override
    protected void populateDefaultEquipmentSlots(@Nonnull RandomSource random, @Nonnull DifficultyInstance difficulty)
    {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));

        super.populateDefaultEquipmentSlots(random, difficulty);
    }

    @Override
    protected void customServerAiStep()
    {
        ProfilerFiller profilerfiller = this.level().getProfiler();
        profilerfiller.push("scavengerBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        profilerfiller.popPush("scavengerActivityUpdate");
        ScavengerAi.updateActivities(this);
        profilerfiller.pop();
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        this.updateSwingTime();

        if (!this.level().isClientSide) return;

        Level level = this.level();
        BlockPos blockPos = this.getOnPos();
        BlockState blockState = level.getBlockState(blockPos);
        ScavengerPose pose = ScavengerPose.getInstance(this);

        // Walking leaves muddy footprints
        if (this.walkAnimation.isMoving() && this.tickCount % 6 == 0 && blockState.isFaceSturdy(level, blockPos, Direction.UP))
        {
            float yaw = this.yBodyRot * Mth.DEG_TO_RAD;
            double stride = ((this.tickCount / 6) % 2 == 0 ? -0.25D : 0.25D);
            this.level().addParticle(new FootprintParticleOption(this.yBodyRot), this.getX() + Mth.cos(yaw) * stride, this.getY() + this.random.nextDouble() * 0.02D, this.getZ() + Mth.sin(yaw) * stride, 0, 0, 0);
        }

        // Try to spawn particle related to the current pose
        if (pose.canUse(this.tickCount))
        {
            pose.tryUse(this, level, this.random, this.tickCount);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEventRegistry.SCAVENGER_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSource)
    {
        return SoundEventRegistry.SCAVENGER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEventRegistry.SCAVENGER_DEATH.get();
    }

    public ScavengerPose getScavengerPose()
    {
        return ScavengerPose.getInstance(this);
    }

    public void setConsumingItemstack(ItemStack itemstack)
    {
        this.entityData.set(CONSUMING_ITEMSTACK_ID, itemstack);
    }

    public ItemStack getConsumingItemstack()
    {
        return this.entityData.get(CONSUMING_ITEMSTACK_ID);
    }

    public boolean isOffering()
    {
        return false;
    }

    public boolean isAppearing()
    {
        return false;
    }
}