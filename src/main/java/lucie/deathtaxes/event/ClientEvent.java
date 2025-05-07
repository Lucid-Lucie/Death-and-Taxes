package lucie.deathtaxes.event;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.client.model.ScavengerModel;
import lucie.deathtaxes.client.particle.FlyParticle;
import lucie.deathtaxes.client.renderer.ScavengerRenderer;
import lucie.deathtaxes.registry.EntityTypeRegistry;
import lucie.deathtaxes.registry.ParticleTypeRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = DeathTaxes.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvent
{
    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(ScavengerModel.LAYER_LOCATION, ScavengerModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(EntityTypeRegistry.SCAVENGER.value(), ScavengerRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event)
    {
        event.registerSpriteSet(ParticleTypeRegistry.FLY.get(), FlyParticle.FlyProvider::new);
    }
}
