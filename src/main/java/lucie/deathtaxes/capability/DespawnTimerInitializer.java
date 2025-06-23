package lucie.deathtaxes.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class DespawnTimerInitializer implements INBTSerializable<CompoundTag>
{
    public long despawnTime = 0L;

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.putLong("DespawnTime", despawnTime);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag)
    {
        despawnTime = compoundTag.getLong("DespawnTime");
    }
}
