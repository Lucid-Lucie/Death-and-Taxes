package lucie.deathtaxes.event;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.client.layer.ScavengerCoatLayer;
import lucie.deathtaxes.client.layer.ScavengerHatLayer;
import lucie.deathtaxes.client.model.ScavengerModel;
import lucie.deathtaxes.client.particle.EmberParticle;
import lucie.deathtaxes.client.particle.FlyParticle;
import lucie.deathtaxes.client.particle.FootprintParticle;
import lucie.deathtaxes.client.renderer.ScavengerRenderer;
import lucie.deathtaxes.registry.EntityTypeRegistry;
import lucie.deathtaxes.registry.ParticleTypeRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeathTaxes.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvent
{
    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(ScavengerModel.LAYER_LOCATION, ScavengerModel::createBodyLayer);
        event.registerLayerDefinition(ScavengerCoatLayer.LAYER_LOCATION, ScavengerModel::createBodyLayer);
        event.registerLayerDefinition(ScavengerHatLayer.LAYER_LOCATION, ScavengerHatLayer::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(EntityTypeRegistry.SCAVENGER.get(), ScavengerRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event)
    {
        event.registerSpriteSet(ParticleTypeRegistry.FOOTPRINT.get(), FootprintParticle.Provider::new);
        event.registerSpriteSet(ParticleTypeRegistry.EMBER.get(), EmberParticle.Provider::new);
        event.registerSpriteSet(ParticleTypeRegistry.FLY.get(), FlyParticle.Provider::new);
    }
}
