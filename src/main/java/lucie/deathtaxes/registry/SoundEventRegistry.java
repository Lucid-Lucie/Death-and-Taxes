package lucie.deathtaxes.registry;

import lucie.deathtaxes.DeathTaxes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundEventRegistry
{
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, DeathTaxes.MODID);

    public static final Holder<SoundEvent> SCAVENGER_AMBIENT = SOUND_EVENTS.register("entity.scavenger.ambient", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> SCAVENGER_DEATH = SOUND_EVENTS.register("entity.scavenger.death", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> SCAVENGER_HURT = SOUND_EVENTS.register("entity.scavenger.hurt", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> SCAVENGER_YES = SOUND_EVENTS.register("entity.scavenger.yes", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> SCAVENGER_NO = SOUND_EVENTS.register("entity.scavenger.no", SoundEvent::createVariableRangeEvent);

    public static final Holder<SoundEvent> SCAVENGER_TRADE = SOUND_EVENTS.register("entity.scavenger.trade", SoundEvent::createVariableRangeEvent);
}
