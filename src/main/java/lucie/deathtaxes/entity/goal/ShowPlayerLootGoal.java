package lucie.deathtaxes.entity.goal;

import com.google.common.collect.Lists;
import lucie.deathtaxes.entity.Scavenger;
import lucie.deathtaxes.registry.SoundEventRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class ShowPlayerLootGoal extends LookAtPlayerGoal
{
    private final Scavenger scavenger;
    
    @Nullable
    private ItemStack playerItem;

    private final List<ItemStack> displayItems = Lists.newArrayList();

    private int cycleCounter, displayIndex, lookTime;
    
    public ShowPlayerLootGoal(Scavenger scavenger)
    {
        super(scavenger, Player.class, 8.0F);
        this.scavenger = scavenger;
    }

    @Override
    public boolean canUse()
    {
        if (this.scavenger.level() instanceof ServerLevel serverLevel)
        {
            this.lookAt = serverLevel.getNearestPlayer(this.scavenger, 16.0D);
        }

        return this.lookAt != null && this.lookAt.isAlive() && this.scavenger.isAlive() && !this.scavenger.isAngry() && this.scavenger.getTradingPlayer() == null;
    }

    @Override
    public boolean canContinueToUse()
    {
        return this.canUse() && this.lookTime > 0;
    }

    @Override
    public void start()
    {
        super.start();
        this.setDisplayItems(ItemStack.EMPTY);
        this.cycleCounter = 0;
        this.displayIndex = 0;
        this.lookTime = 40;
    }

    @Override
    public void stop()
    {
        super.stop();
        this.setDisplayItems(ItemStack.EMPTY);
        this.playerItem = null;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.lookAt != null && this.lookAt.getType().equals(EntityType.PLAYER))
        {
            Player player = (Player) this.lookAt;
            this.getDisplayItems(player);

            if (!this.displayItems.isEmpty())
            {
                this.cycleDisplayItems();
            }
            else
            {
                this.setDisplayItems(ItemStack.EMPTY);
                this.lookTime = Math.min(this.lookTime, 40);
            }

            this.lookTime -= 1;
        }

    }

    private void cycleDisplayItems()
    {
        if (this.displayItems.size() >= 2 && ++this.cycleCounter > 40)
        {
            this.displayIndex += 1;
            this.cycleCounter = 0;

            if (this.displayIndex >= this.displayItems.size())
            {
                this.displayIndex = 0;
            }

            this.setDisplayItems(this.displayItems.get(this.displayIndex));
            this.scavenger.swing(InteractionHand.MAIN_HAND);
            this.scavenger.makeSound(SoundEventRegistry.SCAVENGER_TRADE.value());
        }
    }
    
    private void getDisplayItems(Player player)
    {
        ItemStack itemStack = player.getMainHandItem();

        boolean flag = false;
        
        if (this.playerItem == null || !ItemStack.isSameItem(this.playerItem, itemStack))
        {
            this.playerItem = itemStack;
            this.displayItems.clear();
            flag = true;
        }

        if (flag && !this.playerItem.isEmpty())
        {
            this.updateDisplayItems();

            if (!this.displayItems.isEmpty())
            {
                this.lookTime = 900;
                this.setDisplayItems(this.displayItems.getFirst());
            }
        }
    }

    private void updateDisplayItems()
    {
        if (this.playerItem != null && !this.playerItem.isEmpty())
        {
            this.scavenger.getOffers().stream()
                    .filter(merchantOffer -> !merchantOffer.isOutOfStock())
                    .filter(merchantOffer -> ItemStack.isSameItem(merchantOffer.getCostA(), this.playerItem) || ItemStack.isSameItem(merchantOffer.getCostB(), this.playerItem))
                    .forEach(merchantOffer -> this.displayItems.add(merchantOffer.assemble()));
        }
    }

    private void setDisplayItems(ItemStack itemStack)
    {
        this.scavenger.setDisplayItem(itemStack);
    }
}
