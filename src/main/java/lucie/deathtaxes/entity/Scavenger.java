package lucie.deathtaxes.entity;

import com.mojang.serialization.Dynamic;
import lucie.deathtaxes.registry.ParticleTypeRegistry;
import lucie.deathtaxes.registry.SoundEventRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class Scavenger extends PathfinderMob implements Merchant
{
    @Nullable
    private Player tradingPlayer;

    @Nullable
    public MerchantOffers merchantOffers;

    public long unhappyCounter, handCounter, despawnDelay;

    public static final EntityDataAccessor<ItemStack> DATA_DISPLAY_ITEM = SynchedEntityData.defineId(Scavenger.class, EntityDataSerializers.ITEM_STACK);

    public Scavenger(EntityType<? extends PathfinderMob> entityType, Level level)
    {
        super(entityType, level);
        this.xpReward = 10;
    }

    /* Brain */

    @Override
    @Nonnull
    protected Brain.Provider<?> brainProvider()
    {
        return Brain.provider(ScavengerAi.MEMORY_TYPES, ScavengerAi.SENSOR_TYPES);
    }

    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    protected Brain<?> makeBrain(@Nonnull Dynamic<?> dynamic)
    {
        return ScavengerAi.makeBrain(this, (Brain<Scavenger>) this.brainProvider().makeBrain(dynamic));
    }

    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public Brain<Scavenger> getBrain()
    {
        return (Brain<Scavenger>) super.getBrain();
    }

    public static AttributeSupplier registerAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.FOLLOW_RANGE, 12.0F)
                .add(Attributes.MAX_HEALTH, 24.0F)
                .add(Attributes.ATTACK_DAMAGE, 0.5F)
                .build();
    }

    @Override
    protected void customServerAiStep()
    {
        ProfilerFiller profilerfiller = this.level().getProfiler();
        profilerfiller.push("scavengerBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        profilerfiller.popPush("scavengerActivityUpdate");
        ScavengerAi.updateActivity(this);
        profilerfiller.pop();
    }

    @Override
    public void aiStep()
    {
        super.aiStep();
        this.updateSwingTime();
    }

    @Override
    public void handleEntityEvent(byte id)
    {
        super.handleEntityEvent(id);

        if (id == 22)
        {
            // Spawn fly particle.
            double x = this.getX() + this.random.nextDouble() * (double) 5.0F - (double) 2.5F;
            double y = this.getY() + this.random.nextDouble() * (double) 2.5F;
            double z = this.getZ() + this.random.nextDouble() * (double) 5.0F - (double) 2.5F;
            this.level().addParticle((SimpleParticleType) ParticleTypeRegistry.FLY.get(), x, y, z, 0.0F, 0.0F, 0.0F);

            // Play buzzing sounds.
            if (this.random.nextInt(8) == 0)
            {
                this.level().playLocalSound(this.blockPosition(), SoundEventRegistry.FLIES_BUZZING.get(), SoundSource.NEUTRAL, 0.25F, 1.0F, false);
            }
        }
        else if (id == 23)
        {
            if (this.level().getGameTime() > this.unhappyCounter)
            {
                this.unhappyCounter = this.level().getGameTime() + 40;
                this.ambientSoundTime = 0;
                this.level().playLocalSound(this.blockPosition(), SoundEventRegistry.SCAVENGER_NO.get(), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
            }
        }
        else if (id == 24)
        {
            super.handleEntityEvent((byte)60);
            this.level().playLocalSound(this.blockPosition(), SoundEventRegistry.SOMETHING_TELEPORTS.get(), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
            this.level().playLocalSound(this.blockPosition(), SoundEventRegistry.SCAVENGER_YES.get(), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
            this.handCounter = this.level().getGameTime() + 30;
        }
    }

    @Override
    public boolean canBeAffected(@Nonnull MobEffectInstance effectInstance)
    {
        return !MobEffects.WITHER.equals(effectInstance.getEffect()) && super.canBeAffected(effectInstance);
    }

    public ItemStack getDisplayItem()
    {
        return this.entityData.get(Scavenger.DATA_DISPLAY_ITEM);
    }

    public void setDisplayItem(ItemStack itemStack)
    {
        this.entityData.set(Scavenger.DATA_DISPLAY_ITEM, itemStack);
    }

    @Override
    public boolean requiresCustomPersistence()
    {
        return (this.merchantOffers != null && !this.merchantOffers.isEmpty()) || super.requiresCustomPersistence();
    }

    @Override
    public void checkDespawn()
    {
        if (this.requiresCustomPersistence())
        {
            // Despawn entity if no more trades are available or if the despawn timer has run out.
            boolean shouldDespawn = (this.merchantOffers != null && this.merchantOffers.stream().allMatch(MerchantOffer::isOutOfStock)) || (this.despawnDelay != 0 && this.level().getGameTime() > this.despawnDelay);

            // Don't despawn while trading.
            if (shouldDespawn && tradingPlayer == null)
            {
                this.playSound(SoundEventRegistry.SOMETHING_TELEPORTS.get());
                this.level().broadcastEntityEvent(this, (byte) 60);
                this.discard();
                return;
            }
        }

        super.checkDespawn();
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entity)
    {
        if (super.doHurtTarget(entity))
        {
            if (entity instanceof LivingEntity livingEntity)
            {
                float duration = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 140 * (int) duration), this);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean hurt(@Nonnull DamageSource damageSource, float amount)
    {
        if (super.hurt(damageSource, amount))
        {
            Entity entity = damageSource.getEntity();

            if (entity instanceof LivingEntity livingEntity)
            {
                if (entity instanceof Player player && player.isCreative()) return true;

                this.brain.setMemory(MemoryModuleType.ANGRY_AT, livingEntity.getUUID());
                this.brain.setMemory(MemoryModuleType.ATTACK_TARGET, livingEntity);
            }

            return true;
        }

        return false;
    }

    @Nonnull
    @Override
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand)
    {
        if (this.isAlive() && !this.isAggressive() && !this.isInvisible() && this.tradingPlayer == null)
        {
            if (!this.level().isClientSide)
            {
                if (this.getOffers().isEmpty())
                {
                    // Shake head.
                    this.level().broadcastEntityEvent(this, (byte)23);
                }
                else
                {
                    // Open trade menu.
                    this.setTradingPlayer(player);
                    this.openTradingScreen(player, this.getDisplayName(), 0);
                }
            }

            return InteractionResult.SUCCESS;
        }
        else
        {
            return super.mobInteract(player, hand);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSource)
    {
        return SoundEventRegistry.SCAVENGER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEventRegistry.SCAVENGER_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEventRegistry.SCAVENGER_DEATH.get();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer)
    {
        return false;
    }

    /* Data */

    @Override
    @Nullable
    @SuppressWarnings("deprecation")
    public SpawnGroupData finalizeSpawn(@Nonnull ServerLevelAccessor level, @Nonnull DifficultyInstance difficulty, @Nonnull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag)
    {
        // Give entity a shovel.
        this.populateDefaultEquipmentSlots(this.random, difficulty);
        this.populateDefaultEquipmentEnchantments(this.random, difficulty);

        if (spawnType == MobSpawnType.TRIGGERED)
        {
            // Scavenger only stays for a day when summoned by respawning.
            this.despawnDelay = this.level().getGameTime() + 24000;

            // Scavenger enters dramatically when summoned by respawning.
            this.getBrain().setMemory(MemoryModuleType.DANCING, true);
        }
        else
        {
            // Fix scavenger pathing when no home is set.
            this.restrictTo(this.blockPosition(), 16);
        }

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, compoundTag);
    }

    @Override
    protected void populateDefaultEquipmentSlots(@Nonnull RandomSource randomSource, @Nonnull DifficultyInstance difficultyInstance)
    {
        super.populateDefaultEquipmentSlots(randomSource, difficultyInstance);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(Scavenger.DATA_DISPLAY_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag compoundTag)
    {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putLong("DespawnDelay", this.despawnDelay);
        compoundTag.put("MerchantOffers", this.getOffers().createTag());
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag compoundTag)
    {
        super.readAdditionalSaveData(compoundTag);
        merchantOffers = Optional.of(compoundTag.getCompound("MerchantOffers")).map(MerchantOffers::new).orElse(new MerchantOffers());
        despawnDelay = compoundTag.getLong("DespawnDelay");
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
        return this.merchantOffers != null ? this.merchantOffers : new MerchantOffers();
    }

    @Override
    public void overrideOffers(@Nonnull MerchantOffers merchantOffers)
    {
        this.merchantOffers = merchantOffers;
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
            this.playSound((itemStack.isEmpty() ? SoundEventRegistry.SCAVENGER_NO.get() : SoundEventRegistry.SCAVENGER_YES.get()));
        }
    }

    @Nonnull
    @Override
    public SoundEvent getNotifyTradeSound()
    {
        return SoundEventRegistry.SCAVENGER_TRADE.get();
    }

    @Override
    public boolean isClientSide()
    {
        return this.level().isClientSide;
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

    @Override
    public boolean canBeLeashed(@Nonnull Player player)
    {
        return false;
    }

    @Override
    public void restrictTo(@Nonnull BlockPos blockPos, int distance)
    {
        super.restrictTo(blockPos, distance);
        this.brain.setMemory(MemoryModuleType.HOME, GlobalPos.of(this.level().dimension(), blockPos));
    }
}