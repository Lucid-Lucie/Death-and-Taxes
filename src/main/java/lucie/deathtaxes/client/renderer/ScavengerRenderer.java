package lucie.deathtaxes.client.renderer;

import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.client.layer.ScavengerOuterLayer;
import lucie.deathtaxes.client.model.ScavengerModel;
import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class ScavengerRenderer extends MobRenderer<Scavenger, ScavengerModel>
{
    private static final ResourceLocation TEXTURE = DeathTaxes.withModNamespace("textures/entity/scavenger.png");

    public ScavengerRenderer(EntityRendererProvider.Context context)
    {
        super(context, new ScavengerModel(context.bakeLayer(ScavengerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new ScavengerOuterLayer<>(this, context));
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull Scavenger scavenger)
    {
        return TEXTURE;
    }
}
