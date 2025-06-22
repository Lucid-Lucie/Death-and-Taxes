package lucie.deathtaxes.registry;

import lucie.deathtaxes.DeathTaxes;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SoundEventRegistry
{
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, DeathTaxes.MODID);

    public static final RegistryObject<SoundEvent> SCAVENGER_AMBIENT = SOUND_EVENTS.register("entity.scavenger.ambient", () -> SoundEvent.createVariableRangeEvent(DeathTaxes.withModNamespace("entity.scavenger.ambient")));

    public static final RegistryObject<SoundEvent> SCAVENGER_DEATH = SOUND_EVENTS.register("entity.scavenger.death", () -> SoundEvent.createVariableRangeEvent(DeathTaxes.withModNamespace("entity.scavenger.death")));

    public static final RegistryObject<SoundEvent> SCAVENGER_HURT = SOUND_EVENTS.register("entity.scavenger.hurt", () -> SoundEvent.createVariableRangeEvent(DeathTaxes.withModNamespace("entity.scavenger.hurt")));

    public static final RegistryObject<SoundEvent> SCAVENGER_YES = SOUND_EVENTS.register("entity.scavenger.yes", () -> SoundEvent.createVariableRangeEvent(DeathTaxes.withModNamespace("entity.scavenger.yes")));

    public static final RegistryObject<SoundEvent> SCAVENGER_NO = SOUND_EVENTS.register("entity.scavenger.no", () -> SoundEvent.createVariableRangeEvent(DeathTaxes.withModNamespace("entity.scavenger.no")));

    public static final RegistryObject<SoundEvent> SCAVENGER_TRADE = SOUND_EVENTS.register("entity.scavenger.trade", () -> SoundEvent.createVariableRangeEvent(DeathTaxes.withModNamespace("entity.scavenger.trade")));

    public static final RegistryObject<SoundEvent> SOMETHING_TELEPORTS = SOUND_EVENTS.register("misc.teleport", () -> SoundEvent.createVariableRangeEvent(DeathTaxes.withModNamespace("misc.teleport")));

    public static final RegistryObject<SoundEvent> FLIES_BUZZING = SOUND_EVENTS.register("misc.flies", () -> SoundEvent.createVariableRangeEvent(DeathTaxes.withModNamespace("misc.flies")));
}
