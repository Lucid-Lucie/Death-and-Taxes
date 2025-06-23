package lucie.deathtaxes.entity.behavior;

import com.google.common.collect.ImmutableMap;
import lucie.deathtaxes.capability.DespawnTimerCapability;
import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ambient.Bat;

import javax.annotation.Nonnull;
import java.util.Optional;

public class DramaticEntrance extends Behavior<Scavenger>
{
    public DramaticEntrance()
    {
        super(ImmutableMap.of(MemoryModuleType.DANCING, MemoryStatus.VALUE_PRESENT), Integer.MAX_VALUE);
    }

    @Override
    protected boolean canStillUse(@Nonnull ServerLevel level, @Nonnull Scavenger entity, long gameTime)
    {
        return entity.getBrain().getMemory(MemoryModuleType.DANCING).isPresent() && entity.hasEffect(MobEffects.INVISIBILITY) && entity.tickCount < 128;
    }

    @Override
    protected void start(@Nonnull ServerLevel level, @Nonnull Scavenger entity, long gameTime)
    {
        entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 120, 0));
    }

    @Override
    protected void stop(@Nonnull ServerLevel level, @Nonnull Scavenger entity, long gameTime)
    {
        super.stop(level, entity, gameTime);

        // Remove dramatic entrance.
        entity.getBrain().eraseMemory(MemoryModuleType.DANCING);
        entity.setInvisible(false);

        // Spawn teleportation cloud.
        level.broadcastEntityEvent(entity, (byte)24);

        // Spawn two bats.
        for (int i = 0; i < 2; i++)
        {
            Bat bat = EntityType.BAT.spawn(level.getLevel(), entity.blockPosition().above(), MobSpawnType.TRIGGERED);
            if (bat != null)
            {
                final int index = i;
                bat.getCapability(DespawnTimerCapability.DESPAWN_TIMER_CAPABILITY).ifPresent(cap -> cap.despawnTime = gameTime + 120 + (10 * index));
                bat.restrictTo(entity.blockPosition(), 16);
            }
        }
    }

    @Override
    protected boolean timedOut(long gameTime)
    {
        return false;
    }
}
