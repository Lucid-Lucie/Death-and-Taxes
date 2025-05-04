package lucie.deathtaxes.entity;

import lucie.deathtaxes.entity.goal.TradingWithPlayerGoal;
import lucie.deathtaxes.entity.goal.WanderToPointGoal;
import lucie.deathtaxes.entity.goal.WatchTradingPlayerGoal;
import lucie.deathtaxes.registry.AttachmentTypeRegistry;
import lucie.deathtaxes.registry.ItemRegistry;
import lucie.deathtaxes.registry.SoundEventRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class Scavenger extends PathfinderMob implements Merchant
{
    @Nullable
    private Player tradingPlayer;

    public Scavenger(EntityType<? extends PathfinderMob> entityType, Level level)
    {
        super(entityType, level);
        this.xpReward = 10;
    }

    public static AttributeSupplier attributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.FOLLOW_RANGE, 12.0F)
                .add(Attributes.MAX_HEALTH, 24.0F)
                .add(Attributes.ATTACK_DAMAGE, 0.5F)
                .build();
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new TradingWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new WatchTradingPlayerGoal(this));
        this.goalSelector.addGoal(2, new WanderToPointGoal(this, 2.0D, 0.75D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.75));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    /* Interaction */

    @Nonnull
    @Override
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand)
    {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!itemStack.is(ItemRegistry.SCAVENGER_SPAWN_EGG) && this.isAlive() && this.tradingPlayer == null)
        {
            if (hand == InteractionHand.MAIN_HAND)
            {
                player.awardStat(Stats.TALKED_TO_VILLAGER);
            }

            if (!this.level().isClientSide)
            {
                if (this.getOffers().isEmpty())
                {
                    return InteractionResult.CONSUME;
                }

                this.setTradingPlayer(player);
                this.openTradingScreen(player, Optional.ofNullable(this.getDisplayName()).orElse(this.getName()), 1);
            }

            return InteractionResult.SUCCESS;
        }
        else
        {
            return super.mobInteract(player, hand);
        }
    }

    /* Sounds */

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSource)
    {
        return SoundEventRegistry.SCAVENGER_HURT.value();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEventRegistry.SCAVENGER_AMBIENT.value();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEventRegistry.SCAVENGER_DEATH.value();
    }

    /* Merchant */

    @Override
    public void setTradingPlayer(@Nullable Player player)
    {
        this.tradingPlayer = player;
    }

    @Nullable
    @Override
    public Player getTradingPlayer()
    {
        return this.tradingPlayer;
    }

    @Nonnull
    @Override
    public MerchantOffers getOffers()
    {
        return this.getData(AttachmentTypeRegistry.SCAVENGED_GOODS);
    }

    @Override
    public void overrideOffers(@Nonnull MerchantOffers merchantOffers)
    {
        this.setData(AttachmentTypeRegistry.SCAVENGED_GOODS, merchantOffers);
    }

    @Override
    public void notifyTrade(@Nonnull MerchantOffer merchantOffer)
    {
        merchantOffer.increaseUses();

        if (merchantOffer.shouldRewardExp())
        {
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5F, this.getZ(), merchantOffer.getXp()));
        }
    }

    @Override
    public void notifyTradeUpdated(@Nonnull ItemStack itemStack)
    {
        if (!this.level().isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20)
        {
            this.ambientSoundTime = -this.getAmbientSoundInterval();
            this.makeSound((itemStack.isEmpty() ? SoundEventRegistry.SCAVENGER_NO.value() : SoundEventRegistry.SCAVENGER_YES.value()));
        }
    }

    @Nonnull
    @Override
    public SoundEvent getNotifyTradeSound()
    {
        return SoundEventRegistry.SCAVENGER_TRADE.value();
    }

    @Override
    public boolean isClientSide()
    {
        return this.level().isClientSide;
    }

    @Override
    public boolean stillValid(@Nonnull Player player)
    {
        return this.getTradingPlayer() == player && this.isAlive() && player.canInteractWithEntity(this, 4.0);
    }

    @Override
    public int getVillagerXp()
    {
        return 0;
    }

    @Override
    public void overrideXp(int i)
    {

    }

    @Override
    public boolean showProgressBar()
    {
        return false;
    }
}