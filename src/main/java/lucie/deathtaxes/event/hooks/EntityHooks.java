package lucie.deathtaxes.event.hooks;

import lucie.deathtaxes.registry.AttachmentTypeRegistry;
import lucie.deathtaxes.registry.SoundEventRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityHooks
{
    public static void despawnEntity(Level level, LivingEntity entity)
    {
        long despawnTime = entity.getData(AttachmentTypeRegistry.DESPAWN_TIME.get());

        if (level.getGameTime() > despawnTime && despawnTime > 0L)
        {
            level.broadcastEntityEvent(entity, (byte) 60);
            entity.makeSound(SoundEventRegistry.SOMETHING_TELEPORTS.value());
            entity.discard();
        }
    }
}
