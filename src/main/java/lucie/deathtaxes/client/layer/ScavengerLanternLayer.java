package lucie.deathtaxes.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nonnull;

public class ScavengerLanternLayer<T extends Scavenger, M extends EntityModel<T> & ArmedModel> extends RenderLayer<T, M>
{
    private final BlockRenderDispatcher blockRenderer;

    public ScavengerLanternLayer(RenderLayerParent<T, M> renderer, BlockRenderDispatcher blockRenderer)
    {
        super(renderer);
        this.blockRenderer = blockRenderer;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource multiBufferSource, int packedLight, @Nonnull T scavenger, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if (scavenger.getPoseData() != Scavenger.Pose.LANTERN) return;

        BlockState blockState = Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true);
        HumanoidArm humanoidarm = scavenger.getMainArm();

        poseStack.pushPose();
        this.getParentModel().translateToHand(humanoidarm, poseStack);
        poseStack.scale(0.75F, 0.75F, 0.75F);
        poseStack.translate((humanoidarm == HumanoidArm.RIGHT ? -0.58F : -0.42), -0.25, -0.45F);
        poseStack.translate(0.5, 0.9375, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(-105 + ((float) Math.sin(ageInTicks * 0.0625F) * 2.5F)));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) Math.cos(ageInTicks * 0.0625F) * 5.0F));
        poseStack.translate(-0.5, -0.9375, -0.5);
        this.blockRenderer.renderSingleBlock(blockState, poseStack, multiBufferSource, (int) (LightTexture.FULL_BRIGHT * 0.75F), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.cutout());
        poseStack.popPose();
    }
}
