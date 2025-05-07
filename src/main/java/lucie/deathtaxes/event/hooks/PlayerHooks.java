package lucie.deathtaxes.event.hooks;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.entity.Scavenger;
import lucie.deathtaxes.loot.ItemEvaluation;
import lucie.deathtaxes.registry.AttachmentTypeRegistry;
import lucie.deathtaxes.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Collection;
import java.util.Optional;

public class PlayerHooks
{
    /* Player Drops */

    public static boolean collectDrops(ServerLevel level, ServerPlayer player, Collection<ItemEntity> drops)
    {
        // Only process drops if keep inventory is disabled and drops exist.
        if (!level.getServer().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).get() && !drops.isEmpty())
        {
            TagKey<Item> blacklist = ItemTags.create(DeathTaxes.withModNamespace("scavenger/blacklisted_drops"));

            // Create a filtered container excluding blacklisted items.
            ItemContainerContents contents = ItemContainerContents.fromItems(drops.stream()
                    .map(ItemEntity::getItem)
                    .filter(item -> !item.is(blacklist))
                    .toList());

            // Store valid drops in player data.
            if (contents != ItemContainerContents.EMPTY)
            {
                player.setData(AttachmentTypeRegistry.PLAYER_INVENTORY_DROPS, contents);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public static void copyDrops(Player original, Player current, boolean isDeath)
    {
        ItemContainerContents contents = original.getData(AttachmentTypeRegistry.PLAYER_INVENTORY_DROPS);

        // Copy dropped inventory data.
        if (!contents.equals(ItemContainerContents.EMPTY) && isDeath)
        {
            current.setData(AttachmentTypeRegistry.PLAYER_INVENTORY_DROPS, contents);
        }
    }

    public static void checkDrops(ServerLevel level, ServerPlayer player)
    {
        ItemContainerContents contents = player.getData(AttachmentTypeRegistry.PLAYER_INVENTORY_DROPS);

        // Copy dropped inventory data.
        if (!contents.equals(ItemContainerContents.EMPTY))
        {
            // Try spawn entity.
            PlayerHooks.spawn(player, level, contents);

            // Remove used data.
            player.removeData(AttachmentTypeRegistry.PLAYER_INVENTORY_DROPS);
        }
    }

    /* Scavenger Spawning */

    private static void spawn(ServerPlayer player, ServerLevel level, ItemContainerContents contents)
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
            Optional.ofNullable(EntityTypeRegistry.SCAVENGER.value().spawn(level, spawnpoint, EntitySpawnReason.TRIGGERED)).ifPresent(scavenger ->
            {
                scavenger.merchantOffers = offers;
                scavenger.homePosition = target;
            });
        }
    }

    private static Optional<BlockPos> locate(LevelReader level, BlockPos blockPos, RandomSource randomSource)
    {
        SpawnPlacementType spawnPlacementType = SpawnPlacements.getPlacementType(EntityTypeRegistry.SCAVENGER.value());

        for (int i = 0; i < 16; i++)
        {
            // Get a random block position near the target.
            int x = blockPos.getX() + randomSource.nextInt(64) - 32;
            int z = blockPos.getZ() + randomSource.nextInt(64) - 32;
            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
            BlockPos randomPos = new BlockPos(x, y, z);

            // Validate spawn position.
            if (spawnPlacementType.isSpawnPositionOk(level, randomPos, EntityTypeRegistry.SCAVENGER.value()) && PlayerHooks.accessible(level, randomPos))
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
