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
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class ScavengerCoatLayer<T extends Scavenger, M extends EntityModel<T>> extends RenderLayer<T, M>
{
    private static final ResourceLocation TEXTURE_COAT_OPEN_LOCATION = DeathTaxes.withModNamespace("textures/entity/scavenger_coat_open.png");

    private static final ResourceLocation TEXTURE_COAT_CLOSED_LOCATION = DeathTaxes.withModNamespace("textures/entity/scavenger_coat_closed.png");

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(DeathTaxes.withModNamespace("scavenger"), "coat");

    private final ScavengerModel model;

    public ScavengerCoatLayer(RenderLayerParent<T, M> renderer, ScavengerModel model)
    {
        super(renderer);
        this.model = model;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource multiBufferSource, int packedLight, @Nonnull T scavenger, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if (scavenger.isInvisible()) return;

        ResourceLocation texture = scavenger.getPoseData() == Scavenger.Pose.OFFERING ? TEXTURE_COAT_OPEN_LOCATION : TEXTURE_COAT_CLOSED_LOCATION;
        VertexConsumer consumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
        int overlay = LivingEntityRenderer.getOverlayCoords(scavenger, 0.0F);
        this.model.setupAnim(scavenger, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.model.renderToBuffer(poseStack, consumer, packedLight, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
