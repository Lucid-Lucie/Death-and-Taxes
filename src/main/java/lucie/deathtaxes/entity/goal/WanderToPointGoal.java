package lucie.deathtaxes.entity.goal;

import lucie.deathtaxes.entity.Scavenger;
import lucie.deathtaxes.registry.AttachmentTypeRegistry;
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
        this.scavenger.setData(AttachmentTypeRegistry.HOME_POS, Optional.empty());
        scavenger.getNavigation().stop();
    }

    @Override
    public boolean canUse()
    {
        Optional<BlockPos> home = this.scavenger.getData(AttachmentTypeRegistry.HOME_POS);
        return home.isPresent() && this.isTooFarAway(home.get(), this.stopDistance);
    }

    @Override
    public void tick()
    {
        Optional<BlockPos> blockPos = this.scavenger.getData(AttachmentTypeRegistry.HOME_POS);

        if (blockPos.isPresent() && this.scavenger.getNavigation().isDone())
        {
            BlockPos targetPos = blockPos.get();

            if (this.isTooFarAway(blockPos.get(), 10.0D))
            {
                Vec3 positionA = new Vec3(targetPos.getX() - this.scavenger.getX(), targetPos.getY() - this.scavenger.getY(), targetPos.getZ() - this.scavenger.getZ()).normalize();
                Vec3 positionB = positionA.scale(10.0).add(this.scavenger.getX(), this.scavenger.getY(), this.scavenger.getZ());
                this.scavenger.getNavigation().moveTo(positionB.x, positionB.y, positionB.z, this.speedModifier);
            }
            else
            {
                this.scavenger.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), this.speedModifier);
            }
        }
    }

    private boolean isTooFarAway(BlockPos blockPos, double distance)
    {
        return !blockPos.closerToCenterThan(this.scavenger.position(), distance);
    }
}
