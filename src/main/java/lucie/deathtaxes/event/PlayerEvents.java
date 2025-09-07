package lucie.deathtaxes.event;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.capability.DroppedItemsCapability;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeathTaxes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents
{
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (!(event.getObject() instanceof Player player)) return;

        event.addCapability(DeathTaxes.withModNamespace("dropped_items"), new DroppedItemsCapability(player));
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event)
    {
        boolean gameRule = event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);

        if (!(event.getEntity() instanceof Player player) || gameRule) return;

        player.getCapability(DroppedItemsCapability.CAPABILITY).ifPresent(droppedItems -> droppedItems.setItems(event.getDrops().stream().map(ItemEntity::getItem).toList()));

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onCopyDeath(PlayerEvent.Clone event)
    {
        if (!event.isWasDeath()) return;

        Player originalPlayer = event.getOriginal();
        Player currentPlayer = event.getEntity();

        originalPlayer.reviveCaps();
        originalPlayer.getCapability(DroppedItemsCapability.CAPABILITY)
                .ifPresent(originalDroppedItems -> currentPlayer.getCapability(DroppedItemsCapability.CAPABILITY)
                .ifPresent(currentDroppedItems -> currentDroppedItems.setItems(originalDroppedItems.getItems())));
    }
}
