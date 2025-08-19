package lucie.deathtaxes.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

public class ScavengerAi
{
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH
    );

    protected static final ImmutableList<SensorType<? extends Sensor<? super Scavenger>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.HURT_BY,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_LIVING_ENTITIES
    );

    protected static Brain<?> makeBrain(Scavenger scavenger, Brain<Scavenger> brain)
    {
        initCoreActivity(brain);
        initIdleActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    protected static void updateActivities(Scavenger scavenger)
    {
        Brain<Scavenger> brain = scavenger.getBrain();
        brain.setActiveActivityIfPossible(Activity.IDLE);
    }

    private static void initCoreActivity(Brain<Scavenger> brain)
    {
        brain.addActivity(Activity.CORE, ImmutableList.of(
                Pair.of(0, new LookAtTargetSink(45, 90)),
                Pair.of(1, new MoveToTargetSink()),
                Pair.of(2, SetWalkTargetFromLookTarget.create(0.75F, 3))
        ));
    }

    private static void initIdleActivity(Brain<Scavenger> brain)
    {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(2, SetEntityLookTarget.create(EntityType.PLAYER, 8.0F))
        ));
    }

    private static void initFightActivity()
    {

    }

    private static void initTradeActivity()
    {

    }
}
