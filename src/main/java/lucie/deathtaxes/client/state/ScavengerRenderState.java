package lucie.deathtaxes.client.state;

import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.world.entity.HumanoidArm;

public class ScavengerRenderState extends ArmedEntityRenderState
{
    public boolean isAggressive;

    public boolean isUnhappy;

    public HumanoidArm mainArm;

    public float attackAnim;

    public ScavengerRenderState()
    {
        this.mainArm = HumanoidArm.RIGHT;
    }
}
