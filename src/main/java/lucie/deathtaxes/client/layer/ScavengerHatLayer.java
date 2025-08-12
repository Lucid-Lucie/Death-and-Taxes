package lucie.deathtaxes.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class ScavengerHatLayer<T extends Scavenger, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M>
{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(DeathTaxes.withModNamespace("scavenger"), "hat");

    public static final ResourceLocation TEXTURE_LOCATION = DeathTaxes.withModNamespace("textures/entity/scavenger_hat.png");

    private final ModelPart hat;

    public ScavengerHatLayer(RenderLayerParent<T, M> renderer, ModelPart hat)
    {
        super(renderer);
        this.hat = hat;
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -14.0F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.01F))
                .texOffs(0, 15).addBox(-4.5F, -9.0F, -4.5F, 9.0F, 2.0F, 9.0F, new CubeDeformation(0.01F)), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource multiBufferSource, int packedLight, @Nonnull T scavenger, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if (scavenger.isInvisible()) return;

        VertexConsumer consumer = multiBufferSource.getBuffer(RenderType.entityCutout(TEXTURE_LOCATION));
        int overlay = LivingEntityRenderer.getOverlayCoords(scavenger, 0.0F);

        poseStack.pushPose();
        this.getParentModel().getHead().translateAndRotate(poseStack);
        this.hat.render(poseStack, consumer, packedLight, overlay);
        poseStack.popPose();
    }
}
