package lucie.deathtaxes.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lucie.deathtaxes.client.model.ScavengerModel;
import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;

public class ScavengerItemLayer extends RenderLayer<Scavenger, ScavengerModel>
{
    private final ItemInHandRenderer itemRenderer;

    public ScavengerItemLayer(RenderLayerParent<Scavenger, ScavengerModel> parentRenderer, ItemInHandRenderer itemRenderer)
    {
        super(parentRenderer);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int packedLight, @Nonnull Scavenger scavenger, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch)
    {
        Scavenger.Pose pose = scavenger.getPoseData();

        if (pose == Scavenger.Pose.OFFERING)
        {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotation((float)Math.PI));
            poseStack.mulPose(Axis.YP.rotationDegrees(-40.0F));
            poseStack.translate(0.4F, -0.5F, 0.15F);
            poseStack.scale(0.75F, 0.75F, 0.75F);
            this.itemRenderer.renderItem(scavenger, Items.DIAMOND.getDefaultInstance(), ItemDisplayContext.GROUND, false, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }

        if (pose == Scavenger.Pose.CONSUMING)
        {
            poseStack.pushPose();
            this.getParentModel().translateToArms(poseStack);
            poseStack.mulPose(Axis.XP.rotation(0.75F));
            poseStack.translate(0.0F, 0.2F, -0.34F);
            poseStack.mulPose(Axis.XP.rotation((float)Math.PI));
            this.itemRenderer.renderItem(scavenger, Items.MILK_BUCKET.getDefaultInstance(), ItemDisplayContext.GROUND, false, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }
}
