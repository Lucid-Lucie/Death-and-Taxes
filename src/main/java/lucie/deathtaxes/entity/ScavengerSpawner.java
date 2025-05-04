package lucie.deathtaxes.entity;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.registry.AttachmentTypeRegistry;
import lucie.deathtaxes.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Optional;

public class ScavengerSpawner
{
    private static final int MAX_DISTANCE = 48;

    private static final TagKey<Item> BLACKLISTED_LOOT = ItemTags.create(DeathTaxes.withModNamespace("scavenger/blacklisted_loot"));

    private static final ResourceKey<LootTable> LOOT_TABLE = ResourceKey.create(Registries.LOOT_TABLE, DeathTaxes.withModNamespace("gameplay/scavenger_pricing"));

    private static void createOffers(ServerPlayer serverPlayer, ServerLevel serverLevel, Scavenger scavenger, ItemContainerContents content)
    {
        // Use loot table to calculate cost of item.
        LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(ScavengerSpawner.LOOT_TABLE);

        MerchantOffers merchantOffers = new MerchantOffers();

        for (ItemStack itemStack : content.stream().toList())
        {
            // Remove blacklisted items from offers.
            if (itemStack.is(ScavengerSpawner.BLACKLISTED_LOOT)) continue;

            // Parse item as tool to be used for reference.
            LootParams lootParams = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
                    .withParameter(LootContextParams.ORIGIN, serverPlayer.position())
                    .withParameter(LootContextParams.TOOL, itemStack)
                    .withParameter(LootContextParams.BLOCK_STATE, Blocks.AIR.defaultBlockState())
                    .create(LootContextParamSets.BLOCK);

            // Create cost for item.
            lootTable.getRandomItems(lootParams).stream()
                    .findFirst()
                    .map(itemCost -> new MerchantOffer(new ItemCost(itemCost.getItem(), itemCost.getCount()), itemStack, 1, itemCost.getCount() * 4, 1.0F))
                    .ifPresent(merchantOffers::add);
        }

        scavenger.setData(AttachmentTypeRegistry.SCAVENGED_GOODS, merchantOffers);
    }

    public static void spawn(ServerLevel level, ServerPlayer player, ItemContainerContents content)
    {
        // Try to set target as players respawn position.
        BlockPos target = Optional.ofNullable(player.getRespawnConfig())
                .map(ServerPlayer.RespawnConfig::pos)
                .orElse(player.blockPosition());

        // Try to spawn entity around target position.
        BlockPos spawnpoint = ScavengerSpawner.locatePosition(level, target, level.random).orElse(target);
        Scavenger scavenger = EntityTypeRegistry.SCAVENGER.value().spawn(level, spawnpoint, EntitySpawnReason.TRIGGERED);
        Optional.ofNullable(scavenger).ifPresent(entity ->
        {
            ScavengerSpawner.createOffers(player, level, entity, content);
            entity.setData(AttachmentTypeRegistry.HOME_POS, Optional.of(target));
        });
    }


    private static Optional<BlockPos> locatePosition(LevelReader level, BlockPos blockPos, RandomSource randomSource)
    {
        SpawnPlacementType spawnPlacementType = SpawnPlacements.getPlacementType(EntityTypeRegistry.SCAVENGER.value());

        for (int i = 0; i < 10; i++)
        {
            int x = blockPos.getX() + randomSource.nextInt(ScavengerSpawner.MAX_DISTANCE * 2) - ScavengerSpawner.MAX_DISTANCE;
            int z = blockPos.getZ() + randomSource.nextInt(ScavengerSpawner.MAX_DISTANCE * 2) - ScavengerSpawner.MAX_DISTANCE;
            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

            BlockPos randomPos = new BlockPos(x, y, z);

            if (spawnPlacementType.isSpawnPositionOk(level, randomPos, EntityTypeRegistry.SCAVENGER.value()) && ScavengerSpawner.hasEnoughSpace(level, randomPos))
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
}
