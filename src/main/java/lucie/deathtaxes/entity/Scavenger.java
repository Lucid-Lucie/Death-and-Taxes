package lucie.deathtaxes.entity;

import com.mojang.serialization.Dynamic;
import lucie.deathtaxes.registry.ParticleTypeRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.phys.Vec3;

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

    private void customClientAiStep(ClientLevel level)
    {
        long gameTime = level.getGameTime();

        // Spawn ember particles
        if (this.getPoseData() == Pose.LANTERN && gameTime % 10 == 0)
        {
            double r = (this.getBbWidth() / 2) + 0.2 + this.level().random.nextDouble() * 0.5;
            double a = this.level().random.nextDouble() * Math.PI * 2;
            double y = this.getY() + (this.level().random.nextDouble() * this.getBbHeight());
            this.level().addParticle((SimpleParticleType) ParticleTypeRegistry.EMBER.get(), this.getX() + Math.cos(a) * r, y, this.getZ() + Math.sin(a) * r, 0, 0, 0);
        }

        // Spawn fly particles
        if (this.getPoseData() == Pose.OFFERING && gameTime % 30 == 0)
        {
            double x = this.getX() + this.random.nextDouble() * (double) 2.5F - (double) 1.25F;
            double y = this.getY() + this.random.nextDouble() * (double) 2.5F;
            double z = this.getZ() + this.random.nextDouble() * (double) 2.5F - (double) 1.25F;
            this.level().addParticle((SimpleParticleType) ParticleTypeRegistry.FLY.get(), x, y, z, 0.0F, 0.0F, 0.0F);
        }

        // Spawn glowing particles
        if (this.getPoseData() == Pose.APPEARING && gameTime % 4 == 0)
        {
            float f = this.yBodyRot * ((float)Math.PI / 180F) + Mth.cos((float)this.tickCount * 0.6662F) * 0.5F;
            float f1 = Mth.cos(f);
            float f2 = Mth.sin(f);
            this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + (double)f1 * 0.7D, this.getY() + 1.9D, this.getZ() + (double)f2 * 0.7D, 0.6F, 0.6F, 0.4F);
            this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() - (double)f1 * 0.7D, this.getY() + 1.9D, this.getZ() - (double)f2 * 0.7D, 0.6F, 0.6F, 0.4F);
        }

        // Spawn eating particles
        if (this.getPoseData() == Pose.CONSUMING && gameTime % 8 == 0)
        {
            ItemStack itemStack = this.getConsumingItemstack();

            if (!itemStack.isEmpty())
            {
                for (int i = 0; i < 4; i++)
                {
                    Vec3 velocity = new Vec3((this.random.nextFloat() - 0.5F) * 0.1F,this.random.nextFloat() * 0.1F + 0.05F,(this.random.nextFloat() - 0.5F) * 0.1F);
                    Vec3 forwardOffset = new Vec3(0, 0, 0.5).yRot(-this.yBodyRot * ((float)Math.PI / 180F));
                    Vec3 mouthPos = this.position().add(0, this.getBbHeight() * 0.85F, 0).add(forwardOffset).add(0, -0.25F, 0);
                    this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemStack), mouthPos.x, mouthPos.y, mouthPos.z, velocity.x, velocity.y, velocity.z);
                }
            }
        }

        // Spawn muddy footprints
        if (this.getDeltaMovement().horizontalDistanceSqr() > 0.001 && gameTime % 6 == 0 && level.getBlockState(this.getOnPos()).isFaceSturdy(level, this.getOnPos(), Direction.UP))
        {
            float yawRad = (float)Math.toRadians(this.yBodyRot);
            float cos = Mth.cos(yawRad);
            float sin = Mth.sin(yawRad);

            double stride = 0.25D;
            double offX = cos * stride;
            double offZ = sin * stride;

            if (((gameTime / 6) % 2 == 0)) {
                offX = -offX;
                offZ = -offZ;
            }

            double fx = this.getX() + offX;
            double fy = this.getY();
            double fz = this.getZ() + offZ;

            this.level().addParticle((ParticleOptions) ParticleTypeRegistry.FOOTPRINT.get(), fx, fy, fz, this.yBodyRot, 0, 0);
        }
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        if (this.level() instanceof ClientLevel level)
        {
            this.customClientAiStep(level);
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