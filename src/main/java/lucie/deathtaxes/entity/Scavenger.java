package lucie.deathtaxes.entity;

import lucie.deathtaxes.client.state.ScavengerRenderState;
import lucie.deathtaxes.entity.goal.TradingWithPlayerGoal;
import lucie.deathtaxes.entity.goal.WanderToPointGoal;
import lucie.deathtaxes.entity.goal.WatchTradingPlayerGoal;
import lucie.deathtaxes.loot.ItemEvaluation;
import lucie.deathtaxes.registry.EntityTypeRegistry;
import lucie.deathtaxes.registry.ParticleTypeRegistry;
import lucie.deathtaxes.registry.SoundEventRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
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

    private static final EntityDataAccessor<Integer> DATA_PERSISTENT_ANGER_TIME = SynchedEntityData.defineId(Scavenger.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Long> DATA_UNHAPPY_COUNTER = SynchedEntityData.defineId(Scavenger.class, EntityDataSerializers.LONG);

    private static final EntityDataAccessor<Long> DATA_HANDS_RAISED = SynchedEntityData.defineId(Scavenger.class, EntityDataSerializers.LONG);

    public static final EntityDataAccessor<Boolean> DATA_DRAMATIC_ENTRANCE = SynchedEntityData.defineId(Scavenger.class, EntityDataSerializers.BOOLEAN);

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
        this.goalSelector.addGoal(3, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.75));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        this.updateSwingTime();

        if (!this.level().isClientSide)
        {
            this.updatePersistentAnger((ServerLevel) this.level(), true);

            // Make dramatic entrance.
            if (!this.hasEffect(MobEffects.INVISIBILITY) && this.entityData.get(Scavenger.DATA_DRAMATIC_ENTRANCE) && this.isAlive())
            {
                this.entityData.set(Scavenger.DATA_DRAMATIC_ENTRANCE, false);
                this.entityData.set(Scavenger.DATA_HANDS_RAISED, this.level().getGameTime() + 30);
                this.level().broadcastEntityEvent(this, (byte) 0);
                this.makeSound(SoundEventRegistry.SCAVENGER_TELEPORT.value());
                this.makeSound(SoundEventRegistry.SCAVENGER_YES.value());

                // Spawn two bats.
                for (int i = 0; i < 2; i++)
                {
                    EntityType.BAT.spawn((ServerLevel) this.level(), this.blockPosition().above(), EntitySpawnReason.TRIGGERED);
                }
            }

            // Spawn flies.
            if (this.level().getGameTime() % 80 == 0 && this.isAlive() && !this.isInvisible())
            {
                this.level().broadcastEntityEvent(this, (byte) 1);
            }
        }
    }

    @Override
    public void tick()
    {
        super.tick();

        // Check if the entity is still unhappy.
        Optional.of(this.entityData.get(Scavenger.DATA_UNHAPPY_COUNTER))
                .filter(time -> time > 0)
                .filter(time -> this.level().getGameTime() > time)
                .ifPresent(time -> this.entityData.set(Scavenger.DATA_UNHAPPY_COUNTER, 0L));

        // Check if the entity is still raising hands.
        Optional.of(this.entityData.get(Scavenger.DATA_HANDS_RAISED))
                .filter(time -> time > 0)
                .filter(time -> this.level().getGameTime() > time)
                .ifPresent(time -> this.entityData.set(Scavenger.DATA_HANDS_RAISED, 0L));
    }

    @Override
    public void handleEntityEvent(byte id)
    {
        super.handleEntityEvent(id);

        if (id == 0)
        {
            this.makePoofParticles();
        } else if (id == 1)
        {
            double x = this.getX() + this.random.nextDouble() * (double) 5.0F - (double) 2.5F;
            double y = this.getY() + this.random.nextDouble() * (double) 2.5F;
            double z = this.getZ() + this.random.nextDouble() * (double) 5.0F - (double) 2.5F;
            this.level().addParticle((SimpleParticleType) ParticleTypeRegistry.FLY.get(), x, y, z, 0.0F, 0.0F, 0.0F);
        }
    }

    public void setRenderState(ScavengerRenderState renderState, float partialTick)
    {
        renderState.mainArm = this.getMainArm();
        renderState.attackAnim = this.getAttackAnim(partialTick);
        renderState.isAggressive = this.isAngry();
        renderState.isDramatic = this.entityData.get(Scavenger.DATA_DRAMATIC_ENTRANCE);
        renderState.isUnhappy = this.entityData.get(Scavenger.DATA_UNHAPPY_COUNTER) > this.level().getGameTime();
        renderState.isHandsRaised = this.entityData.get(Scavenger.DATA_HANDS_RAISED) > this.level().getGameTime();
    }

    public void setUnhappy()
    {
        if (this.entityData.get(Scavenger.DATA_UNHAPPY_COUNTER) == 0)
        {
            this.ambientSoundTime = 0;
            this.entityData.set(Scavenger.DATA_UNHAPPY_COUNTER, this.level().getGameTime() + 40);
            this.makeSound(SoundEventRegistry.SCAVENGER_NO.value());
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull ServerLevel serverLevel, @Nonnull Entity entity)
    {
        boolean flag = super.doHurtTarget(serverLevel, entity);

        if (flag && entity instanceof LivingEntity livingEntity)
        {
            float duration = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 140 * (int) duration), this);
        }

        return flag;
    }

    @Nonnull
    @Override
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand)
    {
        if (this.isAlive() && !this.isAngry() && this.tradingPlayer == null)
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
        } else
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

    public static void spawn(ServerLevel level, ServerPlayer player, ItemContainerContents content)
    {
        // Use player respawn location as the home position.
        BlockPos target = player.blockPosition();

        // Find suitable spawnpoint for entity.
        BlockPos spawnpoint = Scavenger.locatePosition(level, target, level.random).orElse(target);

        // Spawn entity with extra data.
        Optional.ofNullable(EntityTypeRegistry.SCAVENGER.value().spawn(level, spawnpoint, EntitySpawnReason.TRIGGERED)).ifPresent(scavenger ->
        {
            scavenger.merchantOffers = ItemEvaluation.evaluateItems(player, level, content);
            scavenger.homePosition = target;
        });
    }

    private static Optional<BlockPos> locatePosition(LevelReader level, BlockPos blockPos, RandomSource randomSource)
    {
        SpawnPlacementType spawnPlacementType = SpawnPlacements.getPlacementType(EntityTypeRegistry.SCAVENGER.value());

        for (int i = 0; i < 16; i++)
        {
            int x = blockPos.getX() + randomSource.nextInt(64) - 32;
            int z = blockPos.getZ() + randomSource.nextInt(64) - 32;
            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

            BlockPos randomPos = new BlockPos(x, y, z);

            if (spawnPlacementType.isSpawnPositionOk(level, randomPos, EntityTypeRegistry.SCAVENGER.value()) && Scavenger.hasEnoughSpace(level, randomPos))
            {
                return Optional.of(randomPos);
            }
        }

        return Optional.empty();
    }

    private static boolean hasEnoughSpace(BlockGetter level, BlockPos pos)
    {
        for (BlockPos blockpos : BlockPos.betweenClosed(pos, pos.offset(1, 2, 1)))
        {
            if (!level.getBlockState(blockpos).getCollisionShape(level, blockpos).isEmpty())
            {
                return false;
            }
        }

        return true;
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
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag compoundTag)
    {
        super.addAdditionalSaveData(compoundTag);
        this.addPersistentAngerSaveData(compoundTag);
        compoundTag.putLong("UnhappyCounter", this.entityData.get(Scavenger.DATA_UNHAPPY_COUNTER));
        compoundTag.putLong("HandsRaised", this.entityData.get(Scavenger.DATA_HANDS_RAISED));
        compoundTag.putBoolean("DramaticEntrance", this.entityData.get(Scavenger.DATA_DRAMATIC_ENTRANCE));
        compoundTag.storeNullable("HomePosition", BlockPos.CODEC, this.homePosition);
        compoundTag.storeNullable("MerchantOffers", MerchantOffers.CODEC, this.merchantOffers);
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag compoundTag)
    {
        super.readAdditionalSaveData(compoundTag);
        this.readPersistentAngerSaveData(this.level(), compoundTag);
        this.entityData.set(Scavenger.DATA_UNHAPPY_COUNTER, compoundTag.getLongOr("UnhappyCounter", 0L));
        this.entityData.set(Scavenger.DATA_HANDS_RAISED, compoundTag.getLongOr("HandsRaised", 0L));
        this.entityData.set(Scavenger.DATA_DRAMATIC_ENTRANCE, compoundTag.getBooleanOr("DramaticEntrance", false));
        this.homePosition = compoundTag.read("HomePosition", BlockPos.CODEC).orElse(null);
        this.merchantOffers = compoundTag.read("MerchantOffers", MerchantOffers.CODEC).orElse(null);
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