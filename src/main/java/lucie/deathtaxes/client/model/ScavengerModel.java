package lucie.deathtaxes.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.entity.Scavenger;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

import javax.annotation.Nonnull;

public class ScavengerModel extends EntityModel<Scavenger> implements ArmedModel, HeadedModel
{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(DeathTaxes.withModNamespace("scavenger"), "main");

    private final ModelPart root, head, hat, nose, body, crossedArms, arms, rightArm, leftArm, rightLeg, leftLeg;

    public ScavengerModel(ModelPart root)
    {
        this.root = root;
        this.head = root.getChild("head");
        this.nose = this.head.getChild("nose");
        this.hat = this.head.getChild("hat");
        this.body = root.getChild("body");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.arms = root.getChild("arms");
        this.leftArm = this.arms.getChild("left_arm");
        this.rightArm = this.arms.getChild("right_arm");
        this.crossedArms = root.getChild("crossed_arms");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Head and hat
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));
        head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -14.0F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.01F))
                .texOffs(28, 53).addBox(-4.5F, -9.0F, -4.5F, 9.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        // Body
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 18).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 36).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        // Legs
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 20).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));

        // Arms
        PartDefinition arms = partdefinition.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(-2.0F, 12.0F, 0.0F));
        arms.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(28, 36).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(44, 36).mirror().addBox(-1.0F, 3.75F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(7.0F, -10.0F, 0.0F));
        arms.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(28, 36).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(44, 36).addBox(-3.0F, 3.75F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-3.0F, -10.0F, 0.0F));

        // Crossed arms
        partdefinition.addOrReplaceChild("crossed_arms", CubeListBuilder.create().texOffs(44, 24).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(44, 24).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(38, 16).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void translateToHand(@Nonnull HumanoidArm humanoidArm, @Nonnull PoseStack poseStack)
    {

    }

    @Override
    public void setupAnim(@Nonnull Scavenger scavenger, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        Scavenger.Pose pose = scavenger.getPoseData();

        this.crossedArms.visible = pose.isArmsCrossed();
        this.arms.visible = !pose.isArmsCrossed();

        // Looking directions
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);

        // Walking animation
        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.rightArm.yRot = 0.0F;
        this.rightArm.zRot = 0.0F;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.leftArm.yRot = 0.0F;
        this.leftArm.zRot = 0.0F;
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.rightLeg.yRot = 0.0F;
        this.rightLeg.zRot = 0.0F;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.leftLeg.yRot = 0.0F;
        this.leftLeg.zRot = 0.0F;

        // Crossed arms animation
        this.crossedArms.xRot = (float) Math.toRadians(-45);
    }

    @Nonnull
    @Override
    public ModelPart getHead()
    {
        return this.head;
    }

    @Override
    public void renderToBuffer(@Nonnull PoseStack poseStack, @Nonnull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}