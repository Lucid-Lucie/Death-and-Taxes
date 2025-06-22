package lucie.deathtaxes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import lucie.deathtaxes.DeathTaxes;
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
    public ScavengerRenderer(EntityRendererProvider.Context context)
    {
        super(context, new ScavengerModel(context.bakeLayer(ScavengerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new DisplayItemArmLayer(this, context.getItemInHandRenderer()));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer())
        {
            @Override
            public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int packedLight, @Nonnull Scavenger scavenger, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
            {
                if (scavenger.isAggressive())
                {
                    super.render(poseStack, bufferSource, packedLight, scavenger, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                }
            }
        });
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull Scavenger scavenger)
    {
        return DeathTaxes.withModNamespace("textures/entity/scavenger.png");
    }
}
