package lucie.deathtaxes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.client.model.ScavengerModel;
import lucie.deathtaxes.client.state.ScavengerRenderState;
import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

import javax.annotation.Nonnull;

public class ScavengerRenderer extends MobRenderer<Scavenger, ScavengerRenderState, ScavengerModel<ScavengerRenderState>>
{
    public ScavengerRenderer(EntityRendererProvider.Context context)
    {
        super(context, new ScavengerModel<>(context.bakeLayer(ScavengerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new DisplayItemArmLayer(this));
        this.addLayer(new ItemInHandLayer<>(this)
        {
            @Override
            public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource multiBufferSource, int packedLight, @Nonnull ScavengerRenderState renderState, float xRot, float yRot)
            {
                if (renderState.isAggressive)
                {
                    super.render(poseStack, multiBufferSource, packedLight, renderState, xRot, yRot);
                }
            }
        });
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
        ArmedEntityRenderState.extractArmedEntityRenderState(scavenger, renderState, this.itemModelResolver);
        this.itemModelResolver.updateForLiving(renderState.displayItem, scavenger.getDisplayItem(), ItemDisplayContext.GROUND, scavenger);
        scavenger.registerRenderState(renderState, partialTick);
    }

    @Override
    public void render(@Nonnull ScavengerRenderState renderState, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int packedLight)
    {
        if (!renderState.isDramatic)
        {
            super.render(renderState, poseStack, bufferSource, packedLight);
        }
    }
}
