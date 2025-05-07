package lucie.deathtaxes.entity.goal;

import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.Optional;

public class TradingWithPlayerGoal extends Goal
{
    private final Scavenger scavenger;

    public TradingWithPlayerGoal(Scavenger scavenger)
    {
        this.scavenger = scavenger;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canUse()
    {
        return Optional.ofNullable(this.scavenger.getTradingPlayer())
                .filter(player -> this.scavenger.isAlive())
                .filter(player -> this.scavenger.onGround())
                .filter(player -> !this.scavenger.isAngry())
                .filter(player -> !this.scavenger.isInWater())
                .filter(player -> !this.scavenger.hurtMarked)
                .filter(player -> this.scavenger.distanceToSqr(player) <= 16.0D)
                .filter(Player::hasContainerOpen)
                .isPresent();
    }

    @Override
    public void start()
    {
        this.scavenger.getNavigation().stop();
    }

    @Override
    public void stop()
    {
        this.scavenger.setTradingPlayer(null);
    }
}
