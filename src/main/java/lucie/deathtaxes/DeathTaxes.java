package lucie.deathtaxes;

import lucie.deathtaxes.registry.EntityTypeRegistry;
import lucie.deathtaxes.registry.ItemRegistry;
import lucie.deathtaxes.registry.MemoryModuleTypeRegistry;
import lucie.deathtaxes.registry.ParticleTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DeathTaxes.MOD_ID)
public class DeathTaxes
{
    public static final String MOD_ID = "deathtaxes";

    public DeathTaxes(FMLJavaModLoadingContext context)
    {
        IEventBus bus = context.getModEventBus();
        EntityTypeRegistry.ENTITY_TYPES.register(bus);
        ItemRegistry.ITEMS.register(bus);
        ParticleTypeRegistry.PARTICLE_TYPES.register(bus);
        MemoryModuleTypeRegistry.MEMORY_MODULE_TYPES.register(bus);
    }

    public static ResourceLocation withModNamespace(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}