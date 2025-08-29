package lucie.deathtaxes.entity.behavior;

import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;

public class UseHealingItem extends AbstractUseItem
{
    public UseHealingItem()
    {
        super(new ItemStack(Items.BREAD), 32);
    }

    @Override
    protected boolean checkExtraStartConditions(@Nonnull ServerLevel level, @Nonnull Scavenger scavenger)
    {
        return scavenger.getHealth() < scavenger.getMaxHealth() && !scavenger.isAggressive() && scavenger.isAlive();
    }

    @Override
    protected boolean canStillUse(@Nonnull ServerLevel level, @Nonnull Scavenger scavenger, long gameTime)
    {
        return this.checkExtraStartConditions(level, scavenger);
    }

    @Override
    protected void finish(@Nonnull ServerLevel level, @Nonnull Scavenger scavenger, long gameTime)
    {
        scavenger.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 1, false, false, false));
    }
}
