package lucie.deathtaxes.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.client.model.ScavengerModel;
import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class ScavengerOuterLayer<T extends Scavenger, M extends EntityModel<T>> extends RenderLayer<T, M>
{
    private static final ResourceLocation TEXTURE_OPEN = DeathTaxes.withModNamespace("textures/entity/scavenger_outer_layer_open.png");

    private static final ResourceLocation TEXTURE_CLOSED = DeathTaxes.withModNamespace("textures/entity/scavenger_outer_layer_closed.png");

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(DeathTaxes.withModNamespace("scavenger"), "outer_layer");

    private final ScavengerModel clothing;

    public ScavengerOuterLayer(RenderLayerParent<T, M> renderer, EntityRendererProvider.Context context)
    {
        super(renderer);
        this.clothing = new ScavengerModel(context.bakeLayer(LAYER_LOCATION));
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, @Nonnull T scavenger, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch)
    {
        ResourceLocation texture = scavenger.getPoseData() == Scavenger.Pose.OFFERING ? TEXTURE_OPEN : TEXTURE_CLOSED;
        VertexConsumer consumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
        int overlay = LivingEntityRenderer.getOverlayCoords(scavenger, 0.0F);
        this.clothing.setupAnim(scavenger, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.clothing.renderToBuffer(poseStack, consumer, packedLight, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
