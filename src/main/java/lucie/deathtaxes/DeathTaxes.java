package lucie.deathtaxes;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(DeathTaxes.MODID)
public class DeathTaxes
{
    public static final String MODID = "deathtaxes";

    public DeathTaxes(IEventBus modBus)
    {

    }

    public static ResourceLocation withModNamespace(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(DeathTaxes.MODID, path);
    }
}
