package lucie.deathtaxes.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.entity.Scavenger;
import lucie.deathtaxes.entity.ScavengerPose;
import net.minecraft.client.model.AnimationUtils;
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

    private final ModelPart root, head, nose, body, pocket, crossedArms, arms, rightArm, leftArm, rightLeg, leftLeg;

    public ScavengerModel(ModelPart root)
    {
        this.root = root;
        this.head = root.getChild("head");
        this.nose = this.head.getChild("nose");
        this.body = root.getChild("body");
        this.pocket = body.getChild("pocket");
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

        // Head and nose
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        // Body and pocket
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 18).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 36).addBox(-4.5F, -0.25F, -3.5F, 9.0F, 18.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("pocket", CubeListBuilder.create().texOffs(32, 43).mirror().addBox(-4.0F, -9.0F, 0.0F, 4.0F, 18.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(3.5F, 8.75F, -3.5F));

        // Legs
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 20).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

        // Arms
        PartDefinition arms = partdefinition.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        arms.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 0).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(48, 0).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));
        arms.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

        // Crossed arms
        PartDefinition crossed_arms = partdefinition.addOrReplaceChild("crossed_arms", CubeListBuilder.create().texOffs(44, 24).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(44, 24).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(38, 16).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));
        crossed_arms.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 6).addBox(-2.0F, 3.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, -1.5708F, -1.5708F));
        crossed_arms.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(48, 6).mirror().addBox(-2.0F, -1.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 4.0F, 0.0F, 0.0F, 1.5708F, 1.5708F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void translateToArms(PoseStack poseStack)
    {
        this.crossedArms.translateAndRotate(poseStack);
    }

    @Override
    public void translateToHand(@Nonnull HumanoidArm humanoidArm, @Nonnull PoseStack poseStack)
    {
        if (humanoidArm == HumanoidArm.LEFT)
        {
            this.leftArm.translateAndRotate(poseStack);
        }
        else
        {
            this.rightArm.translateAndRotate(poseStack);
        }
    }

    @Override
    public void setupAnim(@Nonnull Scavenger scavenger, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        ScavengerPose pose = scavenger.getScavengerPose();
        HumanoidArm arm = scavenger.getMainArm();

        // Reset extra additions
        this.crossedArms.visible = false;
        this.pocket.visible = false;
        this.arms.visible = false;
        this.nose.xRot = 0.0F;
        this.leftArm.x = 5.0F;
        this.rightArm.x = -5.0F;
        this.leftArm.xRot = 0.0F;
        this.rightArm.xRot = 0.0F;
        this.leftArm.zRot = 0.0F;
        this.rightArm.zRot = 0.0F;
        this.leftArm.yRot = 0.0F;
        this.rightArm.yRot = 0.0F;

        // Looking directions
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);

        // Swing legs
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.rightLeg.yRot = 0.0F;
        this.rightLeg.zRot = 0.0F;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.leftLeg.yRot = 0.0F;
        this.leftLeg.zRot = 0.0F;

        if (pose == ScavengerPose.OFFERING)
        {
            this.arms.visible = true;

            // Stretch left arm along coat
            this.pocket.visible = true;
            this.pocket.yRot = (float) Math.toRadians(-125.0D);
            this.leftArm.xRot = (float) Math.toRadians(-45.0D);
            this.leftArm.yRot = (float) Math.toRadians(-45.0D);

            // Animate idle right arm
            this.rightArm.xRot = 0;
            this.rightArm.zRot = 0;
            AnimationUtils.bobModelPart(this.rightArm, ageInTicks, 1.0F);
        }

        if (pose == ScavengerPose.ATTACKING)
        {
            this.arms.visible = true;

            // Swing arms
            this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;

            if (scavenger.getMainHandItem().isEmpty())
            {
                AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, true, 0, ageInTicks);
            }
            else
            {
                AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, scavenger, this.attackTime, ageInTicks);
            }
        }

        if (pose == ScavengerPose.APPEARING)
        {
            this.arms.visible = true;

            // Wave arms above head
            this.leftArm.x = 4.0F;
            this.rightArm.x = -4.0F;
            this.leftArm.zRot = (float) Math.toRadians(45);
            this.rightArm.zRot = (float) Math.toRadians(-45);
            this.leftArm.xRot = (float) (Math.toRadians(180) + Mth.cos(ageInTicks * 0.65F) * 0.25F);
            this.rightArm.xRot = (float) (Math.toRadians(180) + Mth.cos(ageInTicks * 0.65F) * 0.25F);

            // Lift head upwards
            this.head.yRot = this.body.yRot;
            this.head.xRot = (float) Math.toRadians(-10);
        }

        if (pose == ScavengerPose.CONSUMING)
        {
            this.crossedArms.visible = true;

            // Eating animation
            float animation = Mth.abs(Mth.cos(ageInTicks / 8.0F * (float)Math.PI));
            this.head.yRot = this.body.yRot;
            this.head.xRot = (float) (this.body.xRot - Math.toRadians(-5 + (-5 * animation)));
            this.nose.xRot = (float) Math.toRadians(-10);
            this.crossedArms.xRot = (float) Math.toRadians(-50 + (animation * - 10));
        }

        if (pose == ScavengerPose.LANTERN)
        {
            this.arms.visible = true;

            if (arm == HumanoidArm.RIGHT)
            {
                this.rightArm.xRot = (float) Math.toRadians(-75);
                AnimationUtils.bobModelPart(this.leftArm, ageInTicks, -1.0F);
            }
            else
            {
                this.leftArm.xRot = (float) Math.toRadians(-75);
                AnimationUtils.bobModelPart(this.rightArm, ageInTicks, 1.0F);
            }
        }

        if (pose == ScavengerPose.IDLE)
        {
            this.crossedArms.visible = true;
            this.crossedArms.xRot = (float) Math.toRadians(-45);
        }
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