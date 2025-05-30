package lucie.deathtaxes.client.state;

import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;

public class ScavengerRenderState extends ArmedEntityRenderState
{
    public boolean isAggressive;

    public boolean isUnhappy;

    public boolean isDramatic;

    public boolean isHandsRaised;

    public HumanoidArm mainArm;

    public float attackAnim;

    public final ItemStackRenderState displayItem = new ItemStackRenderState();

    public ScavengerRenderState()
    {
        this.mainArm = HumanoidArm.RIGHT;
    }
}
