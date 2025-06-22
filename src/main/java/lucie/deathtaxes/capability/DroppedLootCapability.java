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

public class DroppedLootCapability implements ICapabilityProvider, INBTSerializable<CompoundTag>
{
    public static Capability<DroppedLootInitializer> DROPPED_LOOT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private DroppedLootInitializer droppedLoot;

    private final LazyOptional<DroppedLootInitializer> optional = LazyOptional.of(this::createDrops);

    private DroppedLootInitializer createDrops()
    {
        if (this.droppedLoot == null)
        {
            this.droppedLoot = new DroppedLootInitializer();
        }

        return this.droppedLoot;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == DroppedLootCapability.DROPPED_LOOT_CAPABILITY)
        {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        return this.createDrops().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        this.createDrops().deserializeNBT(nbt);
    }

}