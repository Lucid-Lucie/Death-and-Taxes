package lucie.deathtaxes.event.hooks;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.capability.DroppedLootCapability;
import lucie.deathtaxes.loot.ItemEvaluation;
import lucie.deathtaxes.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class PlayerHooks
{
    /* Player Drops */

    public static boolean collectDrops(ServerLevel level, ServerPlayer player, Collection<ItemEntity> drops)
    {
        // Only process drops if keep inventory is disabled and drops exist.
        if (!level.getServer().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).get() && !drops.isEmpty())
        {
            // Collect a list of all loot that is not blacklisted.
            TagKey<Item> blacklist = ItemTags.create(DeathTaxes.withModNamespace("blacklisted_loot"));
            List<ItemStack> loot = drops.stream().map(ItemEntity::getItem).filter(itemStack -> !itemStack.is(blacklist)).toList();

            // Store valid drops to the player's capability.
            player.getCapability(DroppedLootCapability.DROPPED_LOOT_CAPABILITY).ifPresent(capability -> capability.droppedLoot = loot);
            return true;
        }

        return false;
    }

    public static void copyDrops(Player original, Player current, boolean isDeath)
    {
        if (isDeath)
        {
            original.reviveCaps();
            original.getCapability(DroppedLootCapability.DROPPED_LOOT_CAPABILITY).ifPresent(oldCapability ->
                    current.getCapability(DroppedLootCapability.DROPPED_LOOT_CAPABILITY).ifPresent(newCapability ->
                            newCapability.copyFrom(oldCapability)));
            original.invalidateCaps();
        }
    }

    public static void checkDrops(ServerLevel level, ServerPlayer player)
    {
        // Check if the player has any dropped loot.
        player.getCapability(DroppedLootCapability.DROPPED_LOOT_CAPABILITY).filter(cap -> !cap.droppedLoot.isEmpty()).ifPresent(cap -> PlayerHooks.spawn(player, level, cap.droppedLoot));

        // Clear the dropped loot capability.
        player.getCapability(DroppedLootCapability.DROPPED_LOOT_CAPABILITY).ifPresent(cap -> cap.droppedLoot = new ArrayList<>());
    }

    /* Scavenger Spawning */

    private static void spawn(ServerPlayer player, ServerLevel level, List<ItemStack> contents)
    {
        // Generate merchant offers from the item container.
        MerchantOffers offers = ItemEvaluation.evaluateItems(player, level, contents);

        // Spawn Scavenger.
        if (!offers.isEmpty())
        {
            // Use player respawn location as the home position.
            BlockPos target = player.blockPosition();

            // Find suitable spawnpoint for entity.
            BlockPos spawnpoint = PlayerHooks.locate(level, target, level.random).orElse(target);

            // Add merchant offers and a home position.
            Optional.ofNullable(EntityTypeRegistry.SCAVENGER.get().spawn(level, spawnpoint, MobSpawnType.TRIGGERED)).ifPresent(scavenger ->
            {
                scavenger.merchantOffers = offers;
                scavenger.restrictTo(target, 16);
            });
        }
    }

    private static Optional<BlockPos> locate(LevelReader level, BlockPos blockPos, RandomSource randomSource)
    {
        SpawnPlacements.Type spawnPlacementType = SpawnPlacements.getPlacementType(EntityTypeRegistry.SCAVENGER.get());

        for (int i = 0; i < 16; i++)
        {
            // Get a random block position near the target.
            int x = blockPos.getX() + randomSource.nextInt(64) - 32;
            int z = blockPos.getZ() + randomSource.nextInt(64) - 32;
            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
            BlockPos randomPos = new BlockPos(x, y, z);

            // Validate spawn position.
            if (spawnPlacementType.canSpawnAt(level, randomPos, EntityTypeRegistry.SCAVENGER.get()) && PlayerHooks.accessible(level, randomPos))
            {
                return Optional.of(randomPos);
            }
        }

        return Optional.empty();
    }

    private static boolean accessible(BlockGetter level, BlockPos pos)
    {
        // Check if all blocks within the position are accessible.
        for (BlockPos blockpos : BlockPos.betweenClosed(pos, pos.offset(1, 2, 1)))
        {
            if (!level.getBlockState(blockpos).getCollisionShape(level, blockpos).isEmpty())
            {
                return false;
            }
        }

        return true;
    }
}
