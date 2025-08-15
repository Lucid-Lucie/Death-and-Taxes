package lucie.deathtaxes.entity;

import com.mojang.serialization.Dynamic;
import lucie.deathtaxes.registry.ParticleTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Scavenger extends PathfinderMob
{
    private static final EntityDataAccessor<ItemStack> CONSUMING_ITEMSTACK = SynchedEntityData.defineId(Scavenger.class, EntityDataSerializers.ITEM_STACK);

    public Scavenger(EntityType<? extends PathfinderMob> entityType, Level level)
    {
        super(entityType, level);
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
    @SuppressWarnings("unchecked")
    protected Brain<?> makeBrain(@Nonnull Dynamic<?> dynamic)
    {
        return ScavengerAi.makeBrain(this, (Brain<Scavenger>) this.brainProvider().makeBrain(dynamic));
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Brain<Scavenger> getBrain()
    {
        return (Brain<Scavenger>) super.getBrain();
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

        Level level = this.level();
        long gameTime = level.getGameTime();

        if (this.getPoseData() == Pose.LANTERN && level.isClientSide && gameTime % 10 == 0)
        {
            double r = (this.getBbWidth() / 2) + 0.2 + this.level().random.nextDouble() * 0.5;
            double a = this.level().random.nextDouble() * Math.PI * 2;
            double y = this.getY() + (this.level().random.nextDouble() * this.getBbHeight());

            this.level().addParticle((SimpleParticleType) ParticleTypeRegistry.EMBER.get(), this.getX() + Math.cos(a) * r, y, this.getZ() + Math.sin(a) * r, 0, 0, 0);
        }
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
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(CONSUMING_ITEMSTACK, ItemStack.EMPTY);
    }

    public void setConsumingItemstack(ItemStack itemstack)
    {
        this.entityData.set(CONSUMING_ITEMSTACK, itemstack);
    }

    public ItemStack getConsumingItemstack()
    {
        return this.entityData.get(CONSUMING_ITEMSTACK);
    }

    public Pose getPoseData()
    {
        if (this.isAggressive())
        {
            return Pose.ATTACKING;
        }

        if (!this.getConsumingItemstack().isEmpty())
        {
            return Pose.CONSUMING;
        }

        if (this.level().getDayTime() >= 13000L && this.level().getDayTime() <= 24000L)
        {
            return Pose.LANTERN;
        }

        return Pose.IDLE;
    }

    public enum Pose
    {
        OFFERING,
        ATTACKING,
        APPEARING,
        CONSUMING,
        LANTERN,
        IDLE
    }
}