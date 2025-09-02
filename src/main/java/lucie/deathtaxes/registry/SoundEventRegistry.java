package lucie.deathtaxes.registry;

import lucie.deathtaxes.DeathTaxes;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SoundEventRegistry
{
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, DeathTaxes.MOD_ID);

    public static final RegistryObject<SoundEvent> SCAVENGER_AMBIENT = SOUND_EVENTS.register("entity.scavenger.ambient", () -> SoundEvent.createVariableRangeEvent(DeathTaxes.withModNamespace("entity.scavenger.ambient")));

    public static final RegistryObject<SoundEvent> SCAVENGER_DEATH = SOUND_EVENTS.register("entity.scavenger.death", () -> SoundEvent.createVariableRangeEvent(DeathTaxes.withModNamespace("entity.scavenger.death")));

    public static final RegistryObject<SoundEvent> SCAVENGER_HURT = SOUND_EVENTS.register("entity.scavenger.hurt", () -> SoundEvent.createVariableRangeEvent(DeathTaxes.withModNamespace("entity.scavenger.hurt")));

    public static final RegistryObject<SoundEvent> SCAVENGER_EAT = SOUND_EVENTS.register("entity.scavenger.eat", () -> SoundEvent.createVariableRangeEvent(DeathTaxes.withModNamespace("entity.scavenger.eat")));

    public static final RegistryObject<SoundEvent> SCAVENGER_BURP = SOUND_EVENTS.register("entity.scavenger.burp", () -> SoundEvent.createVariableRangeEvent(DeathTaxes.withModNamespace("entity.scavenger.burp")));

    public static final RegistryObject<SoundEvent> FLIES_BUZZING = SOUND_EVENTS.register("misc.flies", () -> SoundEvent.createVariableRangeEvent(DeathTaxes.withModNamespace("misc.flies")));
}