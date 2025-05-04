package lucie.deathtaxes.event;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.entity.Scavenger;
import lucie.deathtaxes.registry.AttachmentTypeRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Optional;

@EventBusSubscriber(modid = DeathTaxes.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PlayerDropEvent
{
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event)
    {
        if (event.getEntity().level() instanceof ServerLevel serverLevel)
        {
            // Filter for suitable player.
            Optional<LivingEntity> player = Optional.of(event.getEntity())
                    .filter(livingEntity -> livingEntity.getType().equals(EntityType.PLAYER))
                    .filter(livingEntity -> !serverLevel.getServer().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).get())
                    .filter(livingEntity -> !event.getDrops().isEmpty());

            // Convert drops into container content.
            ItemContainerContents content = ItemContainerContents.fromItems(event.getDrops().stream()
                    .map(ItemEntity::getItem)
                    .toList());

            // Attach container to player and stop stolen content from spawning.
            player.ifPresent(livingEntity ->
            {
                livingEntity.setData(AttachmentTypeRegistry.DEATH_LOOT, content);
                event.setCanceled(true);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        // Copy container to new player instance.
        Optional.of(event.getOriginal().getData(AttachmentTypeRegistry.DEATH_LOOT))
                .filter(content -> !content.equals(ItemContainerContents.EMPTY))
                .filter(content -> event.isWasDeath())
                .ifPresent(content -> event.getEntity().setData(AttachmentTypeRegistry.DEATH_LOOT, content));
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        Player player = event.getEntity();

        // Check for non-empty container content.
        Optional<ItemContainerContents> optional = Optional.of(player.getData(AttachmentTypeRegistry.DEATH_LOOT))
                .filter(content -> !player.level().isClientSide)
                .filter(content -> !content.equals(ItemContainerContents.EMPTY));

        // Spawn Scavenger.
        optional.ifPresent(content ->
        {
            Scavenger.spawn((ServerLevel) player.level(), (ServerPlayer) player, content);
            player.removeData(AttachmentTypeRegistry.DEATH_LOOT);
        });
    }
}
