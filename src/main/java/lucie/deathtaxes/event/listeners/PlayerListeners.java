package lucie.deathtaxes.event.listeners;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.capability.DespawnTimerCapability;
import lucie.deathtaxes.capability.DroppedLootCapability;
import lucie.deathtaxes.event.hooks.PlayerHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeathTaxes.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerListeners
{
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof Player)
        {
            if (!event.getObject().getCapability(DroppedLootCapability.DROPPED_LOOT_CAPABILITY).isPresent())
            {
                event.addCapability(DeathTaxes.withModNamespace("dropped_loot"), new DroppedLootCapability());
            }
        }
    }

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
