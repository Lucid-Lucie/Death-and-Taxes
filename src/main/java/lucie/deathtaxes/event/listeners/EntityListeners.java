package lucie.deathtaxes.event.listeners;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.capability.DespawnTimerCapability;
import lucie.deathtaxes.event.hooks.EntityHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeathTaxes.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityListeners
{
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof Bat)
        {
            if (!event.getObject().getCapability(DespawnTimerCapability.DESPAWN_TIMER_CAPABILITY).isPresent())
            {
                event.addCapability(DeathTaxes.withModNamespace("despawn_timer"), new DespawnTimerCapability());
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event)
    {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof LivingEntity)
        {
            EntityHooks.despawnEntity(event.getEntity().level(), event.getEntity());
        }
    }
}
