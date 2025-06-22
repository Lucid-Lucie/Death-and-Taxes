package lucie.deathtaxes.entity.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class AdvertisePlayerLoot extends Behavior<Scavenger>
{
    private ItemStack playerItemStack;

    private final List<ItemStack> displayItems = Lists.newArrayList();

    private int lookTime, cycleCounter, displayIndex;

    public AdvertisePlayerLoot(int minDuration, int maxDuration)
    {
        super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_PRESENT), minDuration, maxDuration);
    }

    @Override
    protected boolean checkExtraStartConditions(@Nonnull ServerLevel level, @Nonnull Scavenger owner)
    {
        Brain<?> brain = owner.getBrain();

        if (brain.getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent())
        {
            LivingEntity livingEntity = brain.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
            return livingEntity.getType() == EntityType.PLAYER && owner.isAlive() && livingEntity.isAlive() && !owner.isAggressive() && owner.distanceToSqr(livingEntity) <= (double)17.0F && owner.getTradingPlayer() == null;
        }

        return false;
    }

    @Override
    protected boolean canStillUse(@Nonnull ServerLevel level, @Nonnull Scavenger entity, long gameTime)
    {
        return this.checkExtraStartConditions(level, entity) && this.lookTime > 0 && entity.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    @Override
    protected void start(@Nonnull ServerLevel level, @Nonnull Scavenger entity, long gameTime)
    {
        super.start(level, entity, gameTime);
        this.lookAtTarget(entity);
        this.cycleCounter = 0;
        this.displayIndex = 0;
        this.lookTime = 40;
    }

    @Override
    protected void tick(@Nonnull ServerLevel level, @Nonnull Scavenger owner, long gameTime)
    {
        LivingEntity livingEntity = this.lookAtTarget(owner);
        this.findItemsToDisplay(livingEntity, owner);

        if (!this.displayItems.isEmpty())
        {
            this.displayCyclingItems(owner);
        }
        else
        {
            this.setDisplayItem(owner, ItemStack.EMPTY);
            this.lookTime = Math.min(this.lookTime, 40);
        }

        this.lookTime -= 1;
    }

    @Override
    protected void stop(@Nonnull ServerLevel level, @Nonnull Scavenger entity, long gameTime)
    {
        super.stop(level, entity, gameTime);
        entity.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        this.setDisplayItem(entity, ItemStack.EMPTY);
        this.playerItemStack = null;
    }

    private void findItemsToDisplay(LivingEntity entity, Scavenger scavenger)
    {
        boolean flag = false;
        ItemStack itemstack = entity.getMainHandItem();

        if (this.playerItemStack == null || !ItemStack.isSameItem(this.playerItemStack, itemstack))
        {
            this.playerItemStack = itemstack;
            flag = true;
            this.displayItems.clear();
        }

        if (flag && !this.playerItemStack.isEmpty())
        {
            scavenger.getOffers().stream()
                    .filter(merchantOffer -> !merchantOffer.isOutOfStock())
                    .filter(merchantOffer -> ItemStack.isSameItem(merchantOffer.getCostA(), this.playerItemStack) || ItemStack.isSameItem(merchantOffer.getCostB(), this.playerItemStack))
                    .forEach(merchantOffer -> this.displayItems.add(merchantOffer.assemble()));

            if (!this.displayItems.isEmpty())
            {
                this.lookTime = 900;
                this.setDisplayItem(scavenger, this.displayItems.get(0));
            }
        }
    }



    private void setDisplayItem(Scavenger scavenger, ItemStack itemStack)
    {
        scavenger.setDisplayItem(itemStack);
    }

    private LivingEntity lookAtTarget(Scavenger scavenger)
    {
        Brain<?> brain = scavenger.getBrain();
        LivingEntity livingentity = brain.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(livingentity, true));
        return livingentity;
    }

    private void displayCyclingItems(Scavenger scavenger)
    {
        if (this.displayItems.size() >= 2 && ++this.cycleCounter >= 40)
        {
            this.displayIndex += 1;
            this.cycleCounter = 0;

            if (this.displayIndex > this.displayItems.size() - 1)
            {
                this.displayIndex = 0;
            }

            this.setDisplayItem(scavenger, this.displayItems.get(this.displayIndex));
        }
    }
}
