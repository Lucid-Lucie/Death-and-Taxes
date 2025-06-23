package lucie.deathtaxes.event.hooks;

import lucie.deathtaxes.capability.DespawnTimerCapability;
import lucie.deathtaxes.registry.SoundEventRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityHooks
{
    public static void despawnEntity(Level level, LivingEntity entity)
    {
        entity.getCapability(DespawnTimerCapability.DESPAWN_TIMER_CAPABILITY).filter(cap -> level.getGameTime() > cap.despawnTime).ifPresent(cap -> {
            level.broadcastEntityEvent(entity, (byte) 60);
            entity.playSound(SoundEventRegistry.SOMETHING_TELEPORTS.get());
            entity.discard();
        });
    }
}
