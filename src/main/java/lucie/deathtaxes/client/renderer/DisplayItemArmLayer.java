package lucie.deathtaxes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lucie.deathtaxes.client.model.ScavengerModel;
import lucie.deathtaxes.client.state.ScavengerRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

import javax.annotation.Nonnull;

public class DisplayItemArmLayer extends RenderLayer<ScavengerRenderState, ScavengerModel<ScavengerRenderState>>
{
    public DisplayItemArmLayer(RenderLayerParent<ScavengerRenderState, ScavengerModel<ScavengerRenderState>> renderer)
    {
        super(renderer);
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource multiBufferSource, int packedLight, @Nonnull ScavengerRenderState renderState, float xRot, float yRot)
    {
        ItemStackRenderState itemStackRenderState = renderState.displayItem;

        if (!itemStackRenderState.isEmpty() && !renderState.isAggressive && !renderState.isDramatic)
        {
            poseStack.pushPose();
            this.getParentModel().translateToArms(poseStack);
            poseStack.mulPose(Axis.XP.rotation(0.75F));
            poseStack.scale(1.07F, 1.07F, 1.07F);
            poseStack.translate(0.0F, 0.13F, -0.34F);
            poseStack.mulPose(Axis.XP.rotation((float)Math.PI));
            itemStackRenderState.render(poseStack, multiBufferSource, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}
