package lucie.deathtaxes.entity.behavior;

import com.google.common.collect.ImmutableMap;
import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;

public class FollowTradingPlayer extends Behavior<Scavenger>
{
    public FollowTradingPlayer()
    {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), Integer.MAX_VALUE);
    }

    @Override
    protected boolean checkExtraStartConditions(@Nonnull ServerLevel level, @Nonnull Scavenger owner)
    {
        Player player = owner.getTradingPlayer();

        return player != null
                && player.hasContainerOpen()
                && owner.distanceToSqr(player) <= 16.0D
                && owner.isAlive()
                && !owner.isAggressive()
                && !owner.isInWater()
                && !owner.hurtMarked;
    }

    @Override
    protected boolean canStillUse(@Nonnull ServerLevel level, @Nonnull Scavenger entity, long gameTime)
    {
        return this.checkExtraStartConditions(level, entity);
    }

    @Override
    protected void start(@Nonnull ServerLevel level, @Nonnull Scavenger entity, long gameTime)
    {
        if (entity.getTradingPlayer() != null)
        {
            Brain<?> brain = entity.getBrain();
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(entity.getTradingPlayer(), false), 0.5F, 2));
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(entity.getTradingPlayer(), true));
        }
    }

    @Override
    protected void stop(@Nonnull ServerLevel level, @Nonnull Scavenger entity, long gameTime)
    {
        Brain<?> brain = entity.getBrain();
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
    }

    @Override
    protected void tick(@Nonnull ServerLevel level, @Nonnull Scavenger owner, long gameTime)
    {
        // Spawn flies while trading.
        if (gameTime % 5 == 0 && level.random.nextBoolean())
        {
            level.broadcastEntityEvent(owner, (byte)22);
        }
    }

    @Override
    protected boolean timedOut(long gameTime)
    {
        return false;
    }
}
