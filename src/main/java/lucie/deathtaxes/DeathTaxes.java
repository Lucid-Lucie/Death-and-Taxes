package lucie.deathtaxes;

import lucie.deathtaxes.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DeathTaxes.MODID)
public class DeathTaxes
{
    public static final String MODID = "deathtaxes";

    public DeathTaxes(FMLJavaModLoadingContext context)
    {
        IEventBus modBus = context.getModEventBus();
        ParticleTypeRegistry.PARTICLE_TYPES.register(modBus);
        SoundEventRegistry.SOUND_EVENTS.register(modBus);
        EntityTypeRegistry.ENTITY_TYPES.register(modBus);
        ItemRegistry.ITEMS.register(modBus);
        LootConditionRegistry.LOOT_CONDITIONS.register(modBus);
    }

    public static ResourceLocation withModNamespace(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(DeathTaxes.MODID, path);
    }
}
