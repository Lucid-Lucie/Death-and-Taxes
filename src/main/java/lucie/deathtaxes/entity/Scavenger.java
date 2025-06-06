package lucie.deathtaxes.entity;

import lucie.deathtaxes.client.state.ScavengerRenderState;
import lucie.deathtaxes.entity.goal.ShowPlayerLootGoal;
import lucie.deathtaxes.entity.goal.TradingWithPlayerGoal;
import lucie.deathtaxes.entity.goal.WanderToPointGoal;
import lucie.deathtaxes.entity.goal.WatchTradingPlayerGoal;
import lucie.deathtaxes.registry.AttachmentTypeRegistry;
import lucie.deathtaxes.registry.ParticleTypeRegistry;
import lucie.deathtaxes.registry.SoundEventRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ambient.Bat;
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
import java.util.UUID;

public class Scavenger extends PathfinderMob implements Merchant, NeutralMob
{
    @Nullable
    private Player tradingPlayer;

    @Nullable
    public BlockPos homePosition;

    @Nullable
    public MerchantOffers merchantOffers;

    @Nullable
    private UUID persistentAngerTarget;

    private long despawnDelay = 0;

    private static final EntityDataAccessor<Integer> DATA_PERSISTENT_ANGER_TIME = SynchedEntityData.defineId(Scavenger.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Long> DATA_UNHAPPY_COUNTER = SynchedEntityData.defineId(Scavenger.class, EntityDataSerializers.LONG);

    private static final EntityDataAccessor<Long> DATA_HANDS_RAISED = SynchedEntityData.defineId(Scavenger.class, EntityDataSerializers.LONG);

    public static final EntityDataAccessor<Boolean> DATA_DRAMATIC_ENTRANCE = SynchedEntityData.defineId(Scavenger.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<ItemStack> DATA_DISPLAY_ITEM = SynchedEntityData.defineId(Scavenger.class, EntityDataSerializers.ITEM_STACK);

    public Scavenger(EntityType<? extends PathfinderMob> entityType, Level level)
    {
        super(entityType, level);
        this.xpReward = 10;
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

    public void registerRenderState(ScavengerRenderState renderState, float partialTick)
    {
        renderState.mainArm = this.getMainArm();
        renderState.attackAnim = this.getAttackAnim(partialTick);
        renderState.isAggressive = this.isAngry();
        renderState.isDramatic = this.entityData.get(Scavenger.DATA_DRAMATIC_ENTRANCE);
        renderState.isUnhappy = this.entityData.get(Scavenger.DATA_UNHAPPY_COUNTER) > this.level().getGameTime();
        renderState.isHandsRaised = this.entityData.get(Scavenger.DATA_HANDS_RAISED) > this.level().getGameTime();
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new TradingWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new WatchTradingPlayerGoal(this));
        this.goalSelector.addGoal(1, new ShowPlayerLootGoal(this));
        this.goalSelector.addGoal(2, new WanderToPointGoal(this, 2.0D, 0.75D));
        this.goalSelector.addGoal(3, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.75));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0F, 1.0F));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
    }

    @Override
    protected void customServerAiStep(ServerLevel level)
    {
        this.updatePersistentAnger(level, true);

        // Make dramatic entrance.
        if (!this.hasEffect(MobEffects.INVISIBILITY) && this.entityData.get(Scavenger.DATA_DRAMATIC_ENTRANCE) && this.isAlive())
        {
            this.entityData.set(Scavenger.DATA_DRAMATIC_ENTRANCE, false);
            this.entityData.set(Scavenger.DATA_HANDS_RAISED, this.level().getGameTime() + 30);
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.makeSound(SoundEventRegistry.SOMETHING_TELEPORTS.value());
            this.makeSound(SoundEventRegistry.SCAVENGER_YES.value());

            // Spawn two bats.
            for (int i = 0; i < 2; i++)
            {
                Bat bat = EntityType.BAT.spawn(level, this.blockPosition().above(), EntitySpawnReason.TRIGGERED);
                if (bat != null)
                {
                    bat.setData(AttachmentTypeRegistry.DESPAWN_TIME.get(), this.level().getGameTime() + 120 + (10 * i));
                    bat.restrictTo(this.blockPosition(), 16);
                }
            }
        }
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

        if (id == 0)
        {
            double x = this.getX() + this.random.nextDouble() * (double) 5.0F - (double) 2.5F;
            double y = this.getY() + this.random.nextDouble() * (double) 2.5F;
            double z = this.getZ() + this.random.nextDouble() * (double) 5.0F - (double) 2.5F;
            this.level().addParticle((SimpleParticleType) ParticleTypeRegistry.FLY.get(), x, y, z, 0.0F, 0.0F, 0.0F);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeAffected(@Nonnull MobEffectInstance effectInstance)
    {
        return !MobEffects.WITHER.equals(effectInstance.getEffect()) && super.canBeAffected(effectInstance);
    }

    public void setUnhappy()
    {
        long time = this.entityData.get(Scavenger.DATA_UNHAPPY_COUNTER);

        if (this.level().getGameTime() > time)
        {
            this.ambientSoundTime = 0;
            this.entityData.set(Scavenger.DATA_UNHAPPY_COUNTER, this.level().getGameTime() + 40);
            this.makeSound(SoundEventRegistry.SCAVENGER_NO.value());
        }
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
                this.makeSound(SoundEventRegistry.SOMETHING_TELEPORTS.value());
                this.level().broadcastEntityEvent(this, (byte) 60);
                this.discard();
                return;
            }
        }

        super.checkDespawn();
    }

    @Override
    public boolean doHurtTarget(@Nonnull ServerLevel serverLevel, @Nonnull Entity entity)
    {
        if (super.doHurtTarget(serverLevel, entity))
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

    @Nonnull
    @Override
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand)
    {
        if (this.isAlive() && !this.isAngry() && !this.isInvisible() && this.tradingPlayer == null)
        {
            if (!this.level().isClientSide)
            {
                if (this.getOffers().isEmpty())
                {
                    // Shake head.
                    this.setUnhappy();
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

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer)
    {
        return false;
    }

    /* Data */

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public SpawnGroupData finalizeSpawn(@Nonnull ServerLevelAccessor level, @Nonnull DifficultyInstance difficulty, @Nonnull EntitySpawnReason spawnReason, @Nullable SpawnGroupData spawnGroupData)
    {
        // Give entity a shovel.
        this.populateDefaultEquipmentSlots(this.random, difficulty);
        this.populateDefaultEquipmentEnchantments(level, this.random, difficulty);

        // Set dramatic effect.
        if (spawnReason == EntitySpawnReason.TRIGGERED)
        {
            this.despawnDelay = this.level().getGameTime() + 28000;
            this.restrictTo(this.blockPosition(), 16);
            this.entityData.set(Scavenger.DATA_DRAMATIC_ENTRANCE, true);
            this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 80));
        }

        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    @Override
    protected void populateDefaultEquipmentSlots(@Nonnull RandomSource randomSource, @Nonnull DifficultyInstance difficultyInstance)
    {
        super.populateDefaultEquipmentSlots(randomSource, difficultyInstance);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
    }

    @Override
    protected void defineSynchedData(@Nonnull SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(Scavenger.DATA_PERSISTENT_ANGER_TIME, 0);
        builder.define(Scavenger.DATA_HANDS_RAISED, 0L);
        builder.define(Scavenger.DATA_UNHAPPY_COUNTER, 0L);
        builder.define(Scavenger.DATA_DRAMATIC_ENTRANCE, false);
        builder.define(Scavenger.DATA_DISPLAY_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag compoundTag)
    {
        super.addAdditionalSaveData(compoundTag);
        RegistryOps<Tag> registryops = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        this.addPersistentAngerSaveData(compoundTag);
        compoundTag.putLong("UnhappyCounter", this.entityData.get(Scavenger.DATA_UNHAPPY_COUNTER));
        compoundTag.putLong("HandsRaised", this.entityData.get(Scavenger.DATA_HANDS_RAISED));
        compoundTag.putBoolean("DramaticEntrance", this.entityData.get(Scavenger.DATA_DRAMATIC_ENTRANCE));
        compoundTag.putLong("DespawnDelay", this.despawnDelay);
        compoundTag.storeNullable("HomePosition", BlockPos.CODEC, this.homePosition);
        compoundTag.storeNullable("MerchantOffers", MerchantOffers.CODEC, registryops, this.merchantOffers);
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag compoundTag)
    {
        super.readAdditionalSaveData(compoundTag);
        RegistryOps<Tag> registryops = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        this.readPersistentAngerSaveData(this.level(), compoundTag);
        this.entityData.set(Scavenger.DATA_UNHAPPY_COUNTER, compoundTag.getLongOr("UnhappyCounter", 0L));
        this.entityData.set(Scavenger.DATA_HANDS_RAISED, compoundTag.getLongOr("HandsRaised", 0L));
        this.entityData.set(Scavenger.DATA_DRAMATIC_ENTRANCE, compoundTag.getBooleanOr("DramaticEntrance", false));
        this.despawnDelay = compoundTag.getLongOr("DespawnDelay", 0L);
        this.homePosition = compoundTag.read("HomePosition", BlockPos.CODEC).orElse(null);
        this.merchantOffers = compoundTag.read("MerchantOffers", MerchantOffers.CODEC, registryops).orElse(null);
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

    /* Neutral Mob */

    @Override
    public int getRemainingPersistentAngerTime()
    {
        return this.entityData.get(Scavenger.DATA_PERSISTENT_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time)
    {
        this.entityData.set(Scavenger.DATA_PERSISTENT_ANGER_TIME, time);
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget()
    {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid)
    {
        this.persistentAngerTarget = uuid;
    }

    @Override
    public void startPersistentAngerTimer()
    {
        this.setRemainingPersistentAngerTime(TimeUtil.rangeOfSeconds(20, 39).sample(this.random));
    }
}