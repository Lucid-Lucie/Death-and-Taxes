package lucie.deathtaxes.event.listeners;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.event.hooks.EntityHooks;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = DeathTaxes.MODID, bus = EventBusSubscriber.Bus.GAME)
public class EntityListeners
{
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event)
    {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof LivingEntity)
        {
            EntityHooks.despawnEntity(event.getEntity().level(), (LivingEntity) event.getEntity());
        }
    }
}
