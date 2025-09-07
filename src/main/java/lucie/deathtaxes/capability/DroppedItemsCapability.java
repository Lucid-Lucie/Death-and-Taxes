package lucie.deathtaxes.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DroppedItemsCapability implements DroppedItems, ICapabilityProvider
{
    public static final Capability<DroppedItems> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private static final String TAG_KEY = "DroppedItems";

    private final Player player;

    private List<ItemStack> items;

    public DroppedItemsCapability(Player player)
    {
        this.player = player;
    }

    @Override
    public Player getPlayer()
    {
        return this.player;
    }

    @Override
    public void setItems(List<ItemStack> items)
    {
        this.items = items;
    }

    @Override
    public List<ItemStack> getItems()
    {
        return this.items;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag compoundTag = new CompoundTag();

        if (this.items != null)
        {
            ListTag listTag = new ListTag();
            items.forEach(item -> listTag.add(item.save(new CompoundTag())));
            compoundTag.put(TAG_KEY, listTag);
        }

        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag)
    {
        if (compoundTag.contains(TAG_KEY))
        {
            ListTag listTag = compoundTag.getList(TAG_KEY, 10);
            this.items = listTag.stream().map(tag -> ItemStack.of((CompoundTag)tag)).toList();
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        return cap == CAPABILITY ? LazyOptional.of(() -> this).cast() : LazyOptional.empty();
    }
}
