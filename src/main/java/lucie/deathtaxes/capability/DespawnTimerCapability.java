package lucie.deathtaxes.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DespawnTimerCapability implements ICapabilityProvider, INBTSerializable<CompoundTag>
{
    public static Capability<DespawnTimerInitializer> DESPAWN_TIMER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private DespawnTimerInitializer despawnTimer;

    private final LazyOptional<DespawnTimerInitializer> optional = LazyOptional.of(this::createTimer);

    private DespawnTimerInitializer createTimer()
    {
        if (this.despawnTimer == null)
        {
            this.despawnTimer = new DespawnTimerInitializer();
        }

        return this.despawnTimer;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == DespawnTimerCapability.DESPAWN_TIMER_CAPABILITY)
        {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        return this.createTimer().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag)
    {
        this.createTimer().deserializeNBT(compoundTag);
    }
}
