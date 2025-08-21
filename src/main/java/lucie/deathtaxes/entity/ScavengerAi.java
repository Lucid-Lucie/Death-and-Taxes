package lucie.deathtaxes.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
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

    @SuppressWarnings("unchecked")
    protected static Brain<?> makeBrain(Scavenger scavenger, Dynamic<?> dynamic)
    {
        Brain<Scavenger> brain = (Brain<Scavenger>) scavenger.brainProvider().makeBrain(dynamic);
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
                Pair.of(0, new Swim(0.8F))
        ));
    }

    private static void initIdleActivity(Brain<Scavenger> brain)
    {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(2, SetEntityLookTarget.create(EntityType.PLAYER, 8.0F)),
                Pair.of(0, RandomStroll.stroll(1.0F))
        ));
    }

    private static void initFightActivity()
    {

    }

    private static void initTradeActivity()
    {

    }
}
