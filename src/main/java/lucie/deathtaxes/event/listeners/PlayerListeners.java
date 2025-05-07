package lucie.deathtaxes.event.listeners;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.event.hooks.PlayerHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = DeathTaxes.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PlayerListeners
{
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event)
    {
        if (event.getEntity().getType().equals(EntityType.PLAYER))
        {
            ServerPlayer player = (ServerPlayer) event.getEntity();

            event.setCanceled(PlayerHooks.collectDrops((ServerLevel) player.level(), player, event.getDrops()));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        PlayerHooks.copyDrops(event.getOriginal(), event.getEntity(), event.isWasDeath());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        PlayerHooks.checkDrops((ServerLevel) player.level(), player);
    }
}
