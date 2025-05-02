package lucie.deathtaxes.entity;

import lucie.deathtaxes.registry.AttachmentTypeRegistry;
import lucie.deathtaxes.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Optional;

public class ScavengerSpawner
{
    private static final int MAX_DISTANCE = 48;

    public static void attemptSpawn(ServerLevel level, ServerPlayer player, ItemContainerContents content)
    {
        // Try to set target as players respawn position.
        BlockPos target = Optional.ofNullable(player.getRespawnConfig())
                .map(ServerPlayer.RespawnConfig::pos)
                .orElse(player.blockPosition());

        // Try to spawn entity around target position.
        BlockPos spawnpoint = ScavengerSpawner.locatePosition(level, target, level.random).orElse(target);

        // Spawn entity at specified location.
        Scavenger scavenger = EntityTypeRegistry.SCAVENGER.value().spawn(level, spawnpoint, EntitySpawnReason.TRIGGERED);

        if (scavenger != null)
        {
            scavenger.setData(AttachmentTypeRegistry.DEATH_LOOT, content);
            scavenger.setData(AttachmentTypeRegistry.HOME_POS, Optional.of(target));
        }

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
