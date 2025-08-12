package lucie.deathtaxes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.client.layer.ScavengerCoatLayer;
import lucie.deathtaxes.client.layer.ScavengerHatLayer;
import lucie.deathtaxes.client.layer.ScavengerItemLayer;
import lucie.deathtaxes.client.layer.ScavengerLanternLayer;
import lucie.deathtaxes.client.model.ScavengerModel;
import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class ScavengerRenderer extends MobRenderer<Scavenger, ScavengerModel>
{
    private static final ResourceLocation TEXTURE = DeathTaxes.withModNamespace("textures/entity/scavenger.png");

    public ScavengerRenderer(EntityRendererProvider.Context context)
    {
        super(context, new ScavengerModel(context.bakeLayer(ScavengerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new ScavengerCoatLayer<>(this, new ScavengerModel(context.bakeLayer(ScavengerCoatLayer.LAYER_LOCATION))));
        this.addLayer(new ScavengerHatLayer<>(this, context.bakeLayer(ScavengerHatLayer.LAYER_LOCATION)));
        this.addLayer(new ScavengerItemLayer(this, context.getItemInHandRenderer()));
        this.addLayer(new ScavengerLanternLayer<>(this, context.getBlockRenderDispatcher()));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer())
        {
            @Override
            public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource multiBufferSource, int packedLight, @Nonnull Scavenger scavenger, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
            {
                if (scavenger.getPoseData() == Scavenger.Pose.ATTACKING)
                {
                    super.render(poseStack, multiBufferSource, packedLight, scavenger, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                }
            }
        });
    }

    @Override
    protected void scale(@Nonnull Scavenger scavenger, @Nonnull PoseStack poseStack, float partialTick)
    {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull Scavenger scavenger)
    {
        return TEXTURE;
    }
}
