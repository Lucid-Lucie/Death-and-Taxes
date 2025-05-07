package lucie.deathtaxes;

import lucie.deathtaxes.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(DeathTaxes.MODID)
public class DeathTaxes
{
    public static final String MODID = "deathtaxes";

    public DeathTaxes(IEventBus modBus)
    {
        ParticleTypeRegistry.PARTICLE_TYPES.register(modBus);
        AttachmentTypeRegistry.ATTACHMENT_TYPES.register(modBus);
        LootConditionRegistry.LOOT_CONDITIONS.register(modBus);
        SoundEventRegistry.SOUND_EVENTS.register(modBus);
        EntityTypeRegistry.ENTITY_TYPES.register(modBus);
        ItemRegistry.ITEMS.register(modBus);
    }

    public static ResourceLocation withModNamespace(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(DeathTaxes.MODID, path);
    }
}
