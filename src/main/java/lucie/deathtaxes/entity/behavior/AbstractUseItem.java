package lucie.deathtaxes.entity.behavior;

import com.google.common.collect.ImmutableMap;
import lucie.deathtaxes.entity.Scavenger;
import lucie.deathtaxes.registry.MemoryModuleTypeRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public abstract class AbstractUseItem extends Behavior<Scavenger>
{
    private static final SoundEvent CONSUME_SOUND = SoundEvents.GENERIC_EAT;
    private static final SoundEvent CONSUME_FINISH_SOUND = SoundEvents.PLAYER_BURP;

    private final ItemStack consumeItem;

    private final int consumeDuration;
    private int consumeRemainder;

    public AbstractUseItem(ItemStack consumeItem, int consumeDuration)
    {
        super(ImmutableMap.of(MemoryModuleTypeRegistry.CONSUMING_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT), consumeDuration);
        this.consumeDuration = consumeDuration;
        this.consumeItem = consumeItem;
    }

    @Override
    protected void tick(@Nonnull ServerLevel level, @Nonnull Scavenger scavenger, long gameTime)
    {
        this.consumeRemainder--;
        boolean flag = this.consumeRemainder <= this.consumeDuration - 7;

        // Mimic player consuming behavior

        if (flag && scavenger.tickCount % 4 == 0)
        {
            scavenger.playSound(CONSUME_SOUND, 1.0F, 1.0F);
        }
    }

    @Override
    protected abstract boolean canStillUse(@Nonnull ServerLevel level, @Nonnull Scavenger scavenger, long gameTime);

    @Override
    protected void start(@Nonnull ServerLevel level, @Nonnull Scavenger scavenger, long gameTime)
    {
        this.consumeRemainder = this.consumeDuration;
        scavenger.setConsumingItemstack(this.consumeItem);
    }

    @Override
    protected final void stop(@Nonnull ServerLevel level, @Nonnull Scavenger scavenger, long gameTime)
    {
        scavenger.setConsumingItemstack(ItemStack.EMPTY);
        scavenger.getBrain().setMemory(MemoryModuleTypeRegistry.CONSUMING_COOLDOWN.get(), 1200);

        // Make sure behavior didn't stop prematurely

        if (this.consumeRemainder <= 0)
        {
            scavenger.playSound(CONSUME_FINISH_SOUND, 1.0F, 1.0F);
            this.finish(level, scavenger, gameTime);
        }
    }

    protected abstract void finish(@Nonnull ServerLevel level, @Nonnull Scavenger scavenger, long gameTime);
}
