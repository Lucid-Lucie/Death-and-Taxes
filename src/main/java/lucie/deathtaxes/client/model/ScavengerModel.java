package lucie.deathtaxes.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lucie.deathtaxes.DeathTaxes;
import lucie.deathtaxes.entity.Scavenger;
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

    private final ModelPart root, head, crossedArms, rightArm, leftArm, rightLeg, leftLeg;

    public ScavengerModel(ModelPart root)
    {
        this.root = root;
        this.head = root.getChild("head");
        this.crossedArms = root.getChild("crossed_arms");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");

    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Head
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        // Hat.
        head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(10.0F, -38.75F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.125F))
                .texOffs(28, 53).addBox(9.5F, -33.75F, -4.5F, 9.0F, 2.0F, 9.0F, new CubeDeformation(0.125F)), PartPose.offset(-14.0F, 24.75F, 0.0F));

        // Body.
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 18).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 36).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        // Limbs.
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 20).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(28, 36).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(28, 36).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("crossed_arms", CubeListBuilder.create().texOffs(44, 20).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(44, 20).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(40, 32).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(@Nonnull Scavenger scavenger, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        long gameTime = scavenger.level().getGameTime();

        // Looking directions.
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);

        // Walking animation.
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

        // Shaking head.
        if (scavenger.unhappyCounter > gameTime)
        {
            this.head.yRot += 0.3F * Mth.sin(0.45F * ageInTicks);
            this.head.xRot = 0.2F;
        }

        // Render either crossed or normal arms.
        if (scavenger.isAggressive() || scavenger.handCounter > gameTime)
        {
            this.leftArm.visible = true;
            this.rightArm.visible = true;
            this.crossedArms.visible = false;

            if (scavenger.isAggressive())
            {
                if (scavenger.getMainHandItem().isEmpty())
                {
                    AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, true, this.attackTime, ageInTicks);
                }
                else
                {
                    AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, scavenger, this.attackTime, ageInTicks);
                }
            }
            else
            {
                this.rightArm.z = 0.0F;
                this.rightArm.x = -5.0F;
                this.leftArm.z = 0.0F;
                this.leftArm.x = 5.0F;
                this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                this.rightArm.zRot = 2.3561945F;
                this.leftArm.zRot = -2.3561945F;
                this.rightArm.yRot = 0.0F;
                this.leftArm.yRot = 0.0F;
            }
        }
        else
        {
            this.leftArm.visible = false;
            this.rightArm.visible = false;
            this.crossedArms.visible = true;
            this.crossedArms.xRot = (float) Math.toRadians(-45);
        }
    }

    public void translateToArms(PoseStack poseStack)
    {
        this.root.translateAndRotate(poseStack);
        this.crossedArms.translateAndRotate(poseStack);
    }

    @Override
    public void translateToHand(@Nonnull HumanoidArm humanoidArm, @Nonnull PoseStack poseStack)
    {
        this.root.translateAndRotate(poseStack);

        if (humanoidArm == HumanoidArm.LEFT)
        {
            this.leftArm.translateAndRotate(poseStack);
        }
        else
        {
            this.rightArm.translateAndRotate(poseStack);
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
