package lucie.deathtaxes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lucie.deathtaxes.client.model.ScavengerModel;
import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemDisplayContext;

import javax.annotation.Nonnull;

public class DisplayItemArmLayer extends RenderLayer<Scavenger, ScavengerModel>
{
    private final ItemInHandRenderer itemInHandRenderer;

    public DisplayItemArmLayer(RenderLayerParent<Scavenger, ScavengerModel> renderer, ItemInHandRenderer itemInHandRenderer)
    {
        super(renderer);
        this.itemInHandRenderer = itemInHandRenderer;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int packedLight, @Nonnull Scavenger scavenger, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if (!scavenger.getDisplayItem().isEmpty() && !scavenger.isAggressive() && scavenger.level().getGameTime() > scavenger.handCounter)
        {
            poseStack.pushPose();
            this.getParentModel().translateToArms(poseStack);
            poseStack.mulPose(Axis.XP.rotation(0.75F));
            poseStack.scale(1.07F, 1.07F, 1.07F);
            poseStack.translate(0.0F, 0.13F, -0.34F);
            poseStack.mulPose(Axis.XP.rotation((float)Math.PI));
            this.itemInHandRenderer.renderItem(scavenger, scavenger.getDisplayItem(), ItemDisplayContext.GROUND, false, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }
}
