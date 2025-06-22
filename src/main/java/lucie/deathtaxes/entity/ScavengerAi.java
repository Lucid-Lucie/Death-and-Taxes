package lucie.deathtaxes.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import lucie.deathtaxes.entity.behavior.AdvertisePlayerLoot;
import lucie.deathtaxes.entity.behavior.DramaticEntrance;
import lucie.deathtaxes.entity.behavior.FollowTradingPlayer;
import lucie.deathtaxes.entity.behavior.MoveAroundPoint;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Optional;

public class ScavengerAi
{
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.INTERACTION_TARGET,
            MemoryModuleType.HOME,
            MemoryModuleType.ANGRY_AT,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.DANCING
    );

    protected static final ImmutableList<SensorType<? extends Sensor<? super Scavenger>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.HURT_BY
    );

    protected static Brain<?> makeBrain(Scavenger scavenger, Brain<Scavenger> brain)
    {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initFightActivity(brain, scavenger);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<Scavenger> brain)
    {
        brain.addActivity(Activity.CORE, ImmutableList.of(
                Pair.of(0, new LookAtTargetSink(45, 90)),
                Pair.of(0, new DramaticEntrance()),
                Pair.of(1, new MoveToTargetSink()),
                Pair.of(4, new FollowTradingPlayer()))
        );
    }

    private static void initIdleActivity(Brain<Scavenger> brain)
    {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(2, SetEntityLookTarget.create(EntityType.PLAYER, 8.0F)),
                Pair.of(3, new AdvertisePlayerLoot(400, 1600)),
                Pair.of(3, MoveAroundPoint.create(MemoryModuleType.HOME, 0.75F, 16)),
                Pair.of(3, SetLookAndInteract.create(EntityType.PLAYER, 4)),
                Pair.of(1, StartAttacking.create(Mob::isAggressive, ScavengerAi::findNearestValidAttackTarget)))
        );
    }

    private static void initFightActivity(Brain<Scavenger> brain, Scavenger scavenger)
    {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
                MeleeAttack.create(20),
                StopAttackingIfTargetInvalid.create((livingEntity) -> !ScavengerAi.isNearestValidAttackTarget(scavenger, livingEntity)),
                SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0F)
        ), MemoryModuleType.ATTACK_TARGET);
    }

    public static void updateActivity(Scavenger scavenger)
    {
        scavenger.setAggressive(scavenger.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
        scavenger.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
    }

    public static Optional<? extends LivingEntity> findNearestValidAttackTarget(Scavenger scavenger)
    {
        Optional<LivingEntity> optionalEntity = BehaviorUtils.getLivingEntityFromUUIDMemory(scavenger, MemoryModuleType.ANGRY_AT);

        if (optionalEntity.isPresent() && Sensor.isEntityAttackable(scavenger, optionalEntity.get()))
        {
            return optionalEntity;
        }

        return Optional.empty();
    }

    private static boolean isNearestValidAttackTarget(Scavenger scavenger, LivingEntity target)
    {
        return findNearestValidAttackTarget(scavenger)
                .filter(livingEntity -> livingEntity == target)
                .isPresent();
    }
}
