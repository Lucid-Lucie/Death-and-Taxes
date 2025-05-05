package lucie.deathtaxes.entity.goal;

import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MakingAnEntranceGoal extends Goal
{
    private final Scavenger scavenger;

    public MakingAnEntranceGoal(Scavenger scavenger)
    {
        this.scavenger = scavenger;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canUse()
    {
        return this.scavenger.hasEntrance() && !this.scavenger.isAngry() && this.scavenger.onGround() && !this.scavenger.isInWater() && !this.scavenger.hurtMarked;
    }

    @Override
    public void start()
    {
        this.scavenger.getNavigation().stop();
    }
}
