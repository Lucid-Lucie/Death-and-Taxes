package lucie.deathtaxes.entity.goal;

import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;

public class WatchTradingPlayerGoal extends LookAtPlayerGoal
{
    private final Scavenger scavenger;

    public WatchTradingPlayerGoal(Scavenger scavenger)
    {
        super(scavenger, Player.class, 8.0F);
        this.scavenger = scavenger;
    }

    @Override
    public boolean canUse()
    {
        Player player = this.scavenger.getTradingPlayer();

        if (player != null)
        {
            this.lookAt = player;

            return true;
        }
        else
        {
            return false;
        }
    }
}
