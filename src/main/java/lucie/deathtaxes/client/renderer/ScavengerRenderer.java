package lucie.deathtaxes.client.renderer;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.client.model.ScavengerModel;
import lucie.deathtaxes.client.state.ScavengerRenderState;
import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class ScavengerRenderer extends MobRenderer<Scavenger, ScavengerRenderState, ScavengerModel<ScavengerRenderState>>
{
    public ScavengerRenderer(EntityRendererProvider.Context context)
    {
        super(context, new ScavengerModel<>(context.bakeLayer(ScavengerModel.LAYER_LOCATION)), 0.5F);
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull ScavengerRenderState renderState)
    {
        return DeathTaxes.withModNamespace("textures/entity/scavenger.png");
    }

    @Nonnull
    @Override
    public ScavengerRenderState createRenderState()
    {
        return new ScavengerRenderState();
    }

    @Override
    public void extractRenderState(@Nonnull Scavenger scavenger, @Nonnull ScavengerRenderState renderState, float partialTick)
    {
        super.extractRenderState(scavenger, renderState, partialTick);

        renderState.isAggressive = false;
    }
}
