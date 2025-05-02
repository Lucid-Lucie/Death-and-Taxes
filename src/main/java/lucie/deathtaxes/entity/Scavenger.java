package lucie.deathtaxes.entity;

import lucie.deathtaxes.entity.goal.WanderToPointGoal;
import lucie.deathtaxes.registry.SoundEventRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Scavenger extends PathfinderMob
{
    public Scavenger(EntityType<? extends PathfinderMob> entityType, Level level)
    {
        super(entityType, level);
        this.xpReward = 10;
    }

    public static AttributeSupplier attributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.FOLLOW_RANGE, 12.0F)
                .add(Attributes.MAX_HEALTH, 24.0F)
                .add(Attributes.ATTACK_DAMAGE, 0.5F)
                .build();
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(2, new WanderToPointGoal(this, 2.0D, 0.75D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.75));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    /* Sounds */

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSource)
    {
        return SoundEventRegistry.SCAVENGER_HURT.value();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEventRegistry.SCAVENGER_AMBIENT.value();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEventRegistry.SCAVENGER_DEATH.value();
    }
}