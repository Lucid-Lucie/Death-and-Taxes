package lucie.deathtaxes.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import lucie.deathtaxes.entity.behavior.UseHealingItem;
import lucie.deathtaxes.registry.MemoryModuleTypeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.ANGRY_AT,
            MemoryModuleTypeRegistry.CONSUMING_COOLDOWN.get()
    );

    protected static final ImmutableList<SensorType<? extends Sensor<? super Scavenger>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.HURT_BY,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_LIVING_ENTITIES
    );

    @SuppressWarnings("unchecked")
    protected static Brain<?> makeBrain(Scavenger scavenger, Dynamic<?> dynamic)
    {
        Brain<Scavenger> brain = (Brain<Scavenger>) scavenger.brainProvider().makeBrain(dynamic);
        initCoreActivity(brain);
        initIdleActivity(brain);
        initFightActivity(scavenger, brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    protected static void updateActivities(Scavenger scavenger)
    {
        Brain<Scavenger> brain = scavenger.getBrain();
        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
        scavenger.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    private static void initCoreActivity(Brain<Scavenger> brain)
    {
        brain.addActivity(Activity.CORE, ImmutableList.of(
                Pair.of(0, new Swim(0.8F)),
                Pair.of(0, new CountDownCooldownTicks(MemoryModuleTypeRegistry.CONSUMING_COOLDOWN.get())),
                Pair.of(1, new LookAtTargetSink(45, 90)),
                Pair.of(1, new MoveToTargetSink())
        ));
    }

    private static void initIdleActivity(Brain<Scavenger> brain)
    {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(2, SetEntityLookTarget.create(EntityType.PLAYER, 8.0F)),
                Pair.of(3, new UseHealingItem())
        ));
    }

    private static void initFightActivity(Scavenger scavenger, Brain<Scavenger> brain)
    {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
                StopAttackingIfTargetInvalid.create(livingEntity -> !ScavengerAi.isNearestValidAttackTarget(scavenger, livingEntity)),
                SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0F),
                MeleeAttack.create(20)
        ), MemoryModuleType.ATTACK_TARGET);
    }

    private static void initTradeActivity()
    {

    }

    private static boolean isNearestValidAttackTarget(Scavenger scavenger, LivingEntity target)
    {
        return findNearestValidAttackTarget(scavenger)
                .filter(entity -> entity == target)
                .isPresent();
    }

    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(Scavenger scavenger)
    {
        Optional<LivingEntity> optionalEntity = BehaviorUtils.getLivingEntityFromUUIDMemory(scavenger, MemoryModuleType.ANGRY_AT);

        if (optionalEntity.isPresent() && Sensor.isEntityAttackable(scavenger, optionalEntity.get()))
        {
            return optionalEntity;
        }

        return Optional.empty();
    }
}
