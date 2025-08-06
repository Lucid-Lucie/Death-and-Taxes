package lucie.deathtaxes.entity;

import com.mojang.serialization.Dynamic;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class Scavenger extends PathfinderMob
{

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

    public Pose getPoseData()
    {
        return Pose.IDLE;
    }

    public enum Pose
    {
        IDLE(true),
        ATTACKING(false),
        TRADING(true);

        private final boolean isArmsCrossed;

        Pose(boolean isArmsCrossed)
        {
            this.isArmsCrossed = isArmsCrossed;;
        }

        public boolean isArmsCrossed()
        {
            return this.isArmsCrossed;
        }

        public static void write(FriendlyByteBuf buffer, Pose pose)
        {
            buffer.writeByte(pose.ordinal());
        }

        public static Pose read(FriendlyByteBuf buffer)
        {
            return values()[buffer.readByte()];
        }
    }
}