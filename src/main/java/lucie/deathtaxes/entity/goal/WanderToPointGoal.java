package lucie.deathtaxes.entity.goal;

import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Optional;

public class WanderToPointGoal extends Goal
{
    private final Scavenger scavenger;

    private final double stopDistance, speedModifier;

    public WanderToPointGoal(Scavenger scavenger, double stopDistance, double speedModifier)
    {
        this.scavenger = scavenger;
        this.stopDistance = stopDistance;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public void stop()
    {
        this.scavenger.homePosition = null;
        scavenger.getNavigation().stop();
    }

    @Override
    public boolean canUse()
    {
        return this.scavenger.homePosition != null && this.isTooFarAway(this.scavenger.homePosition, this.stopDistance) && !scavenger.isAngry();
    }

    @Override
    public void tick()
    {
        if (this.scavenger.homePosition != null && this.scavenger.getNavigation().isDone())
        {
            BlockPos blockPos = this.scavenger.homePosition;

            if (this.isTooFarAway(blockPos, 10.0D))
            {
                Vec3 positionA = new Vec3(blockPos.getX() - this.scavenger.getX(), blockPos.getY() - this.scavenger.getY(), blockPos.getZ() - this.scavenger.getZ()).normalize();
                Vec3 positionB = positionA.scale(10.0).add(this.scavenger.getX(), this.scavenger.getY(), this.scavenger.getZ());
                this.scavenger.getNavigation().moveTo(positionB.x, positionB.y, positionB.z, this.speedModifier);
            }
            else
            {
                this.scavenger.getNavigation().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.speedModifier);
            }
        }
    }

    private boolean isTooFarAway(BlockPos blockPos, double distance)
    {
        return !blockPos.closerToCenterThan(this.scavenger.position(), distance);
    }
}
